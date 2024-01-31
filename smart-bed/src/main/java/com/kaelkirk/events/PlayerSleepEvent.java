package com.kaelkirk.events;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedEnterEvent.BedEnterResult;
import org.bukkit.event.player.PlayerBedLeaveEvent;

public class PlayerSleepEvent implements Listener {

  private TimeManager manager;

  public PlayerSleepEvent(TimeManager manager) {
    this.manager = manager;
  }
  
  @EventHandler
  public void onPlayerSleep(PlayerBedEnterEvent event) {

    BedEnterResult result = event.getBedEnterResult();
    if (result != BedEnterResult.OK) {
      return;
    }

    if (event.isCancelled()) {
      return;
    }

    Player player = event.getPlayer();
    World world = player.getWorld();


    manager.addPlayerSleeping(world);
    if (manager.areAllPlayersSleeping(world)) {
      return;
    }
  }

  @EventHandler
  public void onPlayerStopSleep(PlayerBedLeaveEvent event) {
    if (event.isCancelled()) {
      return;
    }

    Player player = event.getPlayer();
    World world = player.getWorld();

    manager.removePlayerSleeping(world);
  }
}
