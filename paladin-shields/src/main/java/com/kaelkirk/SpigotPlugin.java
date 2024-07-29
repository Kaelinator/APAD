package com.kaelkirk;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import com.kaelkirk.events.DamageBlockedByPaladinEvent;
import com.kaelkirk.events.EnchantPaladinEvent;
import com.kaelkirk.events.PaladinReleaseEvent;

public class SpigotPlugin extends JavaPlugin {

  private NamespacedKey key;

  @Override
  public void onDisable() { }

  @Override
  public void onEnable() {
    key = new NamespacedKey(this, "PALADIN_SHIELD");
    getServer().getPluginManager().registerEvents(new EnchantPaladinEvent(key), this);
    getServer().getPluginManager().registerEvents(new DamageBlockedByPaladinEvent(key), this);
    getServer().getPluginManager().registerEvents(new PaladinReleaseEvent(key), this);
  }
}
