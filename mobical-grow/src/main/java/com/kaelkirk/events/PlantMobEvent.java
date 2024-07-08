package com.kaelkirk.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import com.kaelkirk.util.MobicalManager;

import org.bukkit.event.block.Action;

public class PlantMobEvent implements Listener {

  private MobicalManager manager;

  public PlantMobEvent(MobicalManager manager) {
    this.manager = manager;
  }

  @EventHandler
  public void onPlayerPlantMobEvent(PlayerInteractEvent event) {
    Action action = event.getAction();

    if (action != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    EquipmentSlot hand = event.getHand();
    Player player = event.getPlayer();
    ItemStack item = player.getEquipment().getItem(EquipmentSlot.HAND);

    Block block = event.getClickedBlock();

    if (block.getType() != Material.FARMLAND) {
      return;
    }

    BlockFace blockFace = event.getBlockFace();

    if (blockFace != BlockFace.UP) {
      return;
    }

    if (hand != EquipmentSlot.HAND || (item != null && item.getType() != Material.AIR)) {
      return;
    }
    
    if (isFarmlandOccupied(block)) {
      return;
    }

    LivingEntity mob = getALeashedYoungMob(player);

    if (mob == null) {
      return;
    }

    // turn off behavior
    manager.setMobical(mob);
    mob.setAI(false);
    mob.setInvulnerable(true);
    // placed into block
    float yaw = mob.getBodyYaw();
    Location blockLocation = block.getLocation().clone();
    blockLocation.add(0.5, 0.5, 0.5);
    mob.teleport(blockLocation);
    // mob.setBodyYaw(yaw);
    mob.setRotation(yaw, 0);
    
    mob.setLeashHolder(null);
  }


  private LivingEntity getALeashedYoungMob(Player player) {
    for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
      if (!(entity instanceof LivingEntity)) {
        continue;
      }

      LivingEntity mob = (LivingEntity) entity;
      if (mob.isLeashed() && mob.getLeashHolder().equals(player) && mob instanceof Ageable && !((Ageable) mob).isAdult()) {
        return mob;
      }
    }
    return null;
  }

  private boolean isFarmlandOccupied(Block block) {

    // check to see if a mob was planted here
    Location location = block.getLocation().add(0.5, 0, 0.5);
    World world = location.getWorld();
    for (Entity entity : world.getNearbyEntities(location, 0.25, 0.6, 0.25)) {
      if (!(entity instanceof LivingEntity)) {
        continue;
      }
      LivingEntity mob = (LivingEntity) entity;
      if (manager.isMobical(mob)) {
        return true;
      }
    }

    return false;
  }
}
