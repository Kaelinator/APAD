package com.kaelkirk.events;

import java.util.HashMap;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.kaelkirk.trackers.PlayerBackflipTracker;

public class PlayerBackflipEvent implements Listener {

  HashMap<Player, PlayerBackflipTracker> map;

  public PlayerBackflipEvent() {
    map = new HashMap<Player, PlayerBackflipTracker>();
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
    player.setGameMode(GameMode.SURVIVAL);
    map.get(player).stop();
    map.remove(player);
  }

}
