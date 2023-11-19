package com.kaelkirk.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.EntityEquipment;

public class LightTheWayEvent implements Listener {
  
  private HashMap<UUID, Block> blocksBeforeLight;
  private HashMap<UUID, Material> blockTypeBeforeLight;

  public LightTheWayEvent() {
    blocksBeforeLight = new HashMap<UUID, Block>();
    blockTypeBeforeLight = new HashMap<UUID, Material>();
  }

  @EventHandler
  public void onLightTheWayEvent(VehicleMoveEvent event) {
    Vehicle vehicle = event.getVehicle();
    if (vehicle == null) {
      return;
    }

    for (Entity passenger : vehicle.getPassengers()) {
      if (passenger instanceof Player) {
        lightTheWay((Player) passenger);
      }
    }
  }

  @EventHandler
  public void onLightTheWayEvent(PlayerMoveEvent event) {
    lightTheWay(event.getPlayer());
  }

  public void lightTheWay(Player player) {
    EntityEquipment equipment = player.getEquipment();

    if (blocksBeforeLight.containsKey(player.getUniqueId())) {
      Block previousLightBlock = blocksBeforeLight.get(player.getUniqueId());
      previousLightBlock.setType(blockTypeBeforeLight.get(player.getUniqueId()));
    }
    
    if (equipment.getItemInMainHand().getType() != Material.TORCH
      && equipment.getItemInOffHand().getType() != Material.TORCH) {
      return;
    }

    Block block = player.getEyeLocation().getBlock();
    Material type = block.getType();

    if (!isIlluminatable(block)) {
      block = getNearestIlluminatableBlock(player.getEyeLocation());
      if (block == null) {
        return;
      }
      type = block.getType();
    }


    blocksBeforeLight.put(player.getUniqueId(), block);
    blockTypeBeforeLight.put(player.getUniqueId(), block.getType());
    block.setType(Material.LIGHT);

    if (type == Material.WATER) {
      BlockData bd = block.getBlockData();
      Waterlogged wl = (Waterlogged) bd;
      wl.setWaterlogged(true);
      block.setBlockData(wl);
    }
  }

  private ArrayList<Block> getBlocksAtRadius(Location location, double radius) {
    World world = location.getWorld();
    // location = location.toCenterLocation();
    ArrayList<Block> blocksAtRadius = new ArrayList<Block>();
    double startingX = location.getX();
    double startingY = location.getY();
    double startingZ = location.getZ();
    for (double x = startingX - radius; x <= startingX + radius; x++) {
      for (double y = startingY - radius; y <= startingY + radius; y++) {
        for (double z = startingZ - radius; z <= startingZ + radius; z++) {
          Location locationAtRadius = new Location(world, x, y, z);
          double distance = locationAtRadius.distance(location);
          Block b = world.getBlockAt(locationAtRadius);
          System.out.print("[" + b.getX() + "," + b.getY() +"," + b.getZ() + "," + radius + "], ");
          if (distance <= radius) {
            blocksAtRadius.add(world.getBlockAt(locationAtRadius));
          }
        }
      }
    }
    return blocksAtRadius;
  }

  private Block getNearestIlluminatableBlock(Location location) {
    for (double radius = 1; radius < 4; radius += 1) {
      ArrayList<Block> blockCandidates = getBlocksAtRadius(location, radius);
      for (Block block : blockCandidates) {
        if (isIlluminatable(block)) {
          return block;
        }
      }
    }
    return null;
  }

  private boolean isIlluminatable(Block block) {
    Material type = block.getType();
    BlockData blockData = block.getBlockData();

    boolean isWaterSourceBlock = false;

    if(blockData instanceof Levelled){
      Levelled lv = (Levelled)blockData;
      isWaterSourceBlock = lv.getLevel() == 0;
    }

    return type == Material.AIR
        || (type == Material.WATER && isWaterSourceBlock)
        || type == Material.CAVE_AIR
        || type == Material.VOID_AIR;
  }
}
