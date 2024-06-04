package com.kaelkirk.events;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class PlayerFallIntoVoid implements Listener {

  private World limitlessVoid;
  private World overworld;

  public PlayerFallIntoVoid(World limitlessVoid, World overworld) {
    this.limitlessVoid = limitlessVoid;
    this.overworld = overworld;
  }

  @EventHandler
  public void onPlayerFallIntoVoidEvent(EntityDamageEvent event) {

    if (event.getCause() != DamageCause.VOID) {
      return;
    }

    Entity entity = event.getEntity();

    if (entity.getWorld().equals(limitlessVoid)) {
      // teleport back to overworld

      if (entity.getType() != EntityType.PLAYER) {
        entity.teleport(overworld.getSpawnLocation());
      }

      Player player = (Player) entity;
      player.teleport(player.getRespawnLocation());
    } else {
      entity.teleport(limitlessVoid.getSpawnLocation());
    }

    // 
    event.setCancelled(true);
    entity.setFallDistance(0);
  }
}
