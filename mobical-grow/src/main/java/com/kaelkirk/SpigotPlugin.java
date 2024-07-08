package com.kaelkirk;

import org.bukkit.plugin.java.JavaPlugin;

import com.kaelkirk.events.PlantMobEvent;
import com.kaelkirk.events.UprootMobEvent;
import com.kaelkirk.events.PlayerFarmMobEvent;

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
    // key = new NamespacedKey(this, "EVERBLADE");
    System.out.println("Hello world from Mobical Grow");
    getServer().getPluginManager().registerEvents(new PlantMobEvent(), this);
    getServer().getPluginManager().registerEvents(new UprootMobEvent(), this);
    getServer().getPluginManager().registerEvents(new PlayerFarmMobEvent(), this);
  }
}
