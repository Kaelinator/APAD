package com.kaelkirk;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import com.kaelkirk.events.TameCreeperEvent;
import com.kaelkirk.events.TamedCreeperLoadsEvent;

public class SpigotPlugin extends JavaPlugin {

  private NamespacedKey ownerKey;

  @Override
  public void onDisable() {
    // Don't log disabling, Spigot does that for you automatically!
  }

  @Override
  public void onEnable() {
    // Don't log enabling, Spigot does that for you automatically!

    // Commands enabled with following method must have entries in plugin.yml
    // getCommand("example").setExecutor(new ExampleCommand(this));
    System.out.println("Hello world from TamableCreepers");
    ownerKey = new NamespacedKey(this, "Owner");
    getServer().getPluginManager().registerEvents(new TameCreeperEvent(this, ownerKey), this);
    getServer().getPluginManager().registerEvents(new TamedCreeperLoadsEvent(this, ownerKey), this);
  }
}
