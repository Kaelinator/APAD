package com.kaelkirk;

import org.bukkit.plugin.java.JavaPlugin;

import com.kaelkirk.events.PlayerBackflipEvent;
import com.kaelkirk.trackers.PlayerBackflipTracker;

public class SpigotPlugin extends JavaPlugin {

  @Override
  public void onDisable() {
    // Don't log disabling, Spigot does that for you automatically!
  }

  @Override
  public void onEnable() {
    // Don't log enabling, Spigot does that for you automatically!

    // Commands enabled with following method must have entries in plugin.yml
    // getCommand("example").setExecutor(new ExampleCommand(this));
    System.out.println("Hello world from Backflip");

    PlayerBackflipTracker.register(this);
    getServer().getPluginManager().registerEvents(new PlayerBackflipEvent(), this);
  }
}
