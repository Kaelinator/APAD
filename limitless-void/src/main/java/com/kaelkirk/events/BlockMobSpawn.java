package com.kaelkirk.events;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class BlockMobSpawn implements Listener {
  
  private World limitlessVoidWorld;

  public BlockMobSpawn(World limitlessVoidWorld) {
    this.limitlessVoidWorld = limitlessVoidWorld;
  }

  @EventHandler
  public void onMobSpawnEvent(CreatureSpawnEvent event) {
    if (!event.getLocation().getWorld().equals(limitlessVoidWorld)) {
      return;
    }

    if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
      event.setCancelled(true);
    }
  }
}
