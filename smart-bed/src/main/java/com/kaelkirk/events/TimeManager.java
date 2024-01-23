package com.kaelkirk.events;

import java.util.HashMap;

import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

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

    if (sleepingCount == 1) {
      scheduler.scheduleSyncDelayedTask(plugin, new TimeMachine(world));
    }
  }

  public void removePlayerSleeping(World world) {
    int sleepingCount = sleepCount.getOrDefault(world, 0);
    sleepingCount = sleepingCount > 0 ? sleepingCount - 1 : sleepingCount;
    sleepCount.put(world, sleepingCount);
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

      System.out.println("Skipping " + skipTicks + " waiting " + realtimeTicks + " currently: " + world.getTime());

      scheduler.scheduleSyncDelayedTask(plugin, this, realtimeTicks);
    }

    private int greatestCommonFactor(int a, int b) {
      return (b == 0) ? a : greatestCommonFactor(b, a % b);
    }

  }
}
