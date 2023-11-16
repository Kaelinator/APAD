package com.kaelkirk.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import com.destroystokyo.paper.entity.ai.GoalType;
import com.kaelkirk.goals.FollowPlayerGoal;

public class TamedCreeperLoadsEvent implements Listener {

  private NamespacedKey ownerKey;
  private Plugin plugin;

  public TamedCreeperLoadsEvent(Plugin plugin, NamespacedKey ownerKey) {
    this.ownerKey = ownerKey;
    this.plugin = plugin;
  }
  
  @EventHandler
  public void onTamedCreeperLoadInEvent(ChunkLoadEvent event) {
    Entity[] entities = event.getChunk().getEntities();
    updateTamedCreeperTargets(Arrays.asList(entities), null);
  }

  @EventHandler
  public void onPlayerJoinEvent(PlayerJoinEvent event) {
    updateTamedCreeperTargets(event.getPlayer().getWorld().getEntities(), event.getPlayer());
  }

  private void updateTamedCreeperTargets(List<Entity> entities, Player player) {
    UUID playerUUID = player == null ? null : player.getUniqueId();
    for (Entity entity : entities) {

      if (!entity.getType().equals(EntityType.CREEPER)) {
        continue;
      }

      PersistentDataContainer container = entity.getPersistentDataContainer();

      if (!container.has(ownerKey, PersistentDataType.STRING)) {
        continue;
      }

      UUID owner = UUID.fromString(container.get(ownerKey, PersistentDataType.STRING));

      if (playerUUID != null && !playerUUID.equals(owner)) {
        return;
      }

      Player tamer = Bukkit.getPlayer(owner);
      Mob m = (Mob) entity;
      Bukkit.getMobGoals().removeAllGoals(m, GoalType.TARGET);
      Bukkit.getMobGoals().addGoal(m, 0, new FollowPlayerGoal(plugin, m, tamer));
    }
  }
}
