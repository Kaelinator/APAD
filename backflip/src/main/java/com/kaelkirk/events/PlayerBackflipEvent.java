package com.kaelkirk.events;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.kaelkirk.trackers.PlayerBackflipTracker;

public class PlayerBackflipEvent implements Listener {

  HashMap<Player, PlayerBackflipTracker> map;
  private NamespacedKey backflippingKey;
  private Plugin plugin;

  public PlayerBackflipEvent(Plugin plugin, NamespacedKey backflippingKey) {
    map = new HashMap<Player, PlayerBackflipTracker>();
    this.plugin = plugin;
    this.backflippingKey = backflippingKey;
  }
  
  @EventHandler
  public void onPlayerBackflip(PlayerJumpEvent event) {
    Player player = event.getPlayer();
    if (player.getGameMode() == GameMode.CREATIVE) {
      return;
    }
    new PlayerBackflipTracker(player);
  }

  @EventHandler
  public void onPlayerQuitDuringBackflip(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    if (!map.containsKey(player)) {
      return;
    }
    map.get(player).stop();
    map.remove(player);
  }

  @EventHandler
  public void onPlayerJoinAfterBackflipping(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    PersistentDataContainer container = player.getPersistentDataContainer();
    if (container.has(backflippingKey)) {
      // set their gamemode back
      Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
        @Override
        public void run() {
          GameMode resetMode = GameMode.valueOf(container.get(backflippingKey, PersistentDataType.STRING));
          player.setGameMode(resetMode);
        }
      });
    }
  }

}
