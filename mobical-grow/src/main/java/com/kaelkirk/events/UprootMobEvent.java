package com.kaelkirk.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;

import com.kaelkirk.util.MobicalManager;

public class UprootMobEvent implements Listener {

  private MobicalManager manager;

  public UprootMobEvent(MobicalManager manager) {
    this.manager = manager;
  }

  @EventHandler
  public void onTrample(BlockFadeEvent event) {
    if (event.isCancelled()) {
      return;
    }

    Block block = event.getBlock();

    boolean wasFarmlandButGotTrampled = block.getType() == Material.FARMLAND && event.getNewState().getType() == Material.DIRT;

    if (!wasFarmlandButGotTrampled) {
      return;
    }

    uprootMob(block);
  }

  @EventHandler
  public void onPlantedMobBlockBroken(BlockBreakEvent event) {
    if (event.isCancelled()) {
      return;
    }

    Block block = event.getBlock();

    if (block.getType() != Material.FARMLAND) {
      return;
    }

    uprootMob(block);
  }

  private void uprootMob(Block block) {

    // check to see if a mob was planted here
    Location location = block.getLocation().add(0.5, 0, 0.5);
    World world = location.getWorld();
    for (Entity entity : world.getNearbyEntities(location, 0.25, 0.6, 0.25)) {
      if (!(entity instanceof LivingEntity)) {
        continue;
      }
      LivingEntity mob = (LivingEntity) entity;
      if (manager.isMobical(mob)) {
        manager.unsetMobical(mob);
        mob.setAI(true);
      }
    }
  }
}
