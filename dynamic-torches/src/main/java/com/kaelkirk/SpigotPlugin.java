package com.kaelkirk;

import org.bukkit.plugin.java.JavaPlugin;

import com.kaelkirk.event.LightTheWayEvent;

public class SpigotPlugin extends JavaPlugin {

  private LightTheWayEvent light;

  @Override
  public void onDisable() {
    // Don't log disabling, Spigot does that for you automatically!
    light.removeAllLights();
  }

  @Override
  public void onEnable() {
    // Don't log enabling, Spigot does that for you automatically!

    // Commands enabled with following method must have entries in plugin.yml
    // getCommand("example").setExecutor(new ExampleCommand(this));
    System.out.println("Hello world from Dynamic Torches");
    getServer().getPluginManager().registerEvents(light = new LightTheWayEvent(), this);
  }
}
