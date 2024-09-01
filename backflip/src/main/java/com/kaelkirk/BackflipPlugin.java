package com.kaelkirk;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import com.kaelkirk.events.PlayerBackflipEvent;
import com.kaelkirk.trackers.PlayerBackflipTracker;

public class BackflipPlugin extends JavaPlugin {

  public NamespacedKey backflippingKey = new NamespacedKey(this, "previousGamemode");

  @Override
  public void onDisable() { }

  @Override
  public void onEnable() {
    PlayerBackflipTracker.register(this, backflippingKey);
    getServer().getPluginManager().registerEvents(new PlayerBackflipEvent(this, backflippingKey), this);
  }
}
