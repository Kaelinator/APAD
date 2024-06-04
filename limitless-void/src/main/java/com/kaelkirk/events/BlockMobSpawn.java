package com.kaelkirk.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class BlockMobSpawn implements Listener {
  
  @EventHandler
  public void onMobSpawnEvent(CreatureSpawnEvent event) {
    if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
      event.setCancelled(true);
    }
  }
}
