package com.kaelkirk;

import org.bukkit.plugin.java.JavaPlugin;

import com.kaelkirk.event.LightTheWayEvent;

public class SpigotPlugin extends JavaPlugin {

  private LightTheWayEvent light;

  @Override
  public void onDisable() {
    light.removeAllLights();
  }

  @Override
  public void onEnable() {
    getServer().getPluginManager().registerEvents(light = new LightTheWayEvent(), this);
  }
}
