package com.kaelkirk.event;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EntityEquipment;

public class LightTheWayEvent implements Listener {
  
  private HashMap<UUID, Block> blocksBeforeLight;
  private HashMap<UUID, Material> blockStateBeforeLight;

  public LightTheWayEvent() {
    blocksBeforeLight = new HashMap<UUID, Block>();
    blockStateBeforeLight = new HashMap<UUID, Material>();
  }

  @EventHandler
  public void onLightTheWayEvent(PlayerMoveEvent event) {
    Player player = event.getPlayer();
    EntityEquipment equipment = player.getEquipment();

    if (blocksBeforeLight.containsKey(player.getUniqueId())) {
      Block previousLightBlock = blocksBeforeLight.get(player.getUniqueId());
      previousLightBlock.setType(blockStateBeforeLight.get(player.getUniqueId()));
    }
    
    if (equipment.getItemInMainHand().getType() != Material.TORCH
      && equipment.getItemInOffHand().getType() != Material.TORCH) {
      return;
    }

    Block block = player.getEyeLocation().getBlock();
    Material type = block.getType();

    if (type != Material.AIR && type != Material.WATER) {
      block = getNearestIlluminatableBlock(player.getEyeLocation());
      if (block == null) {
        return;
      }
      type = block.getType();
    }

    blocksBeforeLight.put(player.getUniqueId(), block);
    blockStateBeforeLight.put(player.getUniqueId(), block.getType());
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
      System.out.println(blockCandidates.size() + " candidates at radius " + radius);
      for (Block block : blockCandidates) {
        Material type = block.getType();
        System.out.print(block.getType() + ", ");
        if (type == Material.AIR || type == Material.WATER) {
          return block;
        }
      }
      System.out.println("No match at radius " + radius);
    }
    return null;
  }
}
