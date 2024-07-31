package com.kaelkirk;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import com.kaelkirk.events.TameCreeperEvent;
import com.kaelkirk.events.TamedCreeperLoadsEvent;

public class SpigotPlugin extends JavaPlugin {

  private NamespacedKey ownerKey;

  @Override
  public void onDisable() { }

  @Override
  public void onEnable() {
    ownerKey = new NamespacedKey(this, "Owner");
    getServer().getPluginManager().registerEvents(new TameCreeperEvent(this, ownerKey), this);
    getServer().getPluginManager().registerEvents(new TamedCreeperLoadsEvent(this, ownerKey), this);
  }
}
