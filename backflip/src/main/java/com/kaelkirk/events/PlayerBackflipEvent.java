package com.kaelkirk.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.kaelkirk.trackers.PlayerBackflipTracker;

public class PlayerBackflipEvent implements Listener {
  
  @EventHandler
  public void onPlayerBackflip(PlayerJumpEvent event) {
    Player player = event.getPlayer();
    player.sendMessage("you jumped!");
    new PlayerBackflipTracker(player);
  }

}
