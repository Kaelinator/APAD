package com.kaelkirk.events;

import java.util.HashMap;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import net.kyori.adventure.text.Component;

public class TimeManager {

  private HashMap<World, Integer> sleepCount;
  private Plugin plugin;
  private BukkitScheduler scheduler;

  public TimeManager(Plugin plugin) {
    sleepCount = new HashMap<World, Integer>();
    this.plugin = plugin;
    scheduler = plugin.getServer().getScheduler();
  }

  public void addPlayerSleeping(World world) {
    int sleepingCount = sleepCount.getOrDefault(world, 0) + 1;
    sleepCount.put(world, sleepingCount);

    if (sleepingCount == world.getPlayerCount()) {
      return;
    }

    scheduler.scheduleSyncDelayedTask(plugin, new TimeMachine(world));

    alertPlayers(world);
  }

  public void removePlayerSleeping(World world) {
    int sleepingCount = sleepCount.getOrDefault(world, 0);
    sleepingCount = sleepingCount > 0 ? sleepingCount - 1 : sleepingCount;
    sleepCount.put(world, sleepingCount);

    if (sleepingCount == 0) {
      return;
    }

    alertPlayers(world);
  }

  public boolean areAllPlayersSleeping(World world) {
    return sleepCount.get(world) == world.getPlayerCount();
  }

  private void alertPlayers(World world) {
    int sleepingCount = sleepCount.get(world);
    int totalPlayers = world.getPlayerCount();
    float nightSpeedFactor = ((float) totalPlayers) / (totalPlayers -  sleepingCount);
    String roundedNightSpeedFactor = String.format("%.2f", nightSpeedFactor);
    scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
      @Override
      public void run() {
        for (Player player : world.getPlayers()) {
          player.sendActionBar(Component.text(sleepingCount + "/" + totalPlayers + " sleeping, night sped up by " + roundedNightSpeedFactor + "x"));
        }
      }
    });
  }

  private int greatestCommonFactor(int a, int b) {
    return (b == 0) ? a : greatestCommonFactor(b, a % b);
  }

  private class TimeMachine implements Runnable {

    private World world;

    public TimeMachine(World world) {
      this.world = world;
    }

    @Override
    public void run() {
      int sleepingCount = sleepCount.get(world);
      int notSleepingCount = world.getPlayerCount() - sleepingCount;

      if (sleepingCount == 0 || notSleepingCount == 0) {
        return;
      }

      int gcf = greatestCommonFactor(sleepingCount, notSleepingCount);
      int skipTicks = sleepingCount / gcf;
      int realtimeTicks = notSleepingCount / gcf;

      long newTime = world.getTime() + skipTicks;
      world.setTime(newTime);

      scheduler.scheduleSyncDelayedTask(plugin, this, realtimeTicks);
    }

  }
}
