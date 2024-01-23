package com.kaelkirk;

import org.bukkit.plugin.java.JavaPlugin;

import com.kaelkirk.events.PlayerSleepEvent;
import com.kaelkirk.events.TimeManager;

public class SmartBedPlugin extends JavaPlugin {

  private TimeManager manager;

  @Override
  public void onDisable() {

  }

  @Override
  public void onEnable() {
    // Commands enabled with following method must have entries in plugin.yml
    // getCommand("example").setExecutor(new ExampleCommand(this));

    manager = new TimeManager(this);
    getServer().getPluginManager().registerEvents(new PlayerSleepEvent(manager), this);

    System.out.println("Hello world from Smart Beds");
  }

}
