package com.kaelkirk;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import com.kaelkirk.events.DamageBlockedByPaladinEvent;
import com.kaelkirk.events.EnchantPaladinEvent;
import com.kaelkirk.events.PaladinReleaseEvent;

public class SpigotPlugin extends JavaPlugin {

  private NamespacedKey key;

  @Override
  public void onDisable() {
    // Don't log disabling, Spigot does that for you automatically!
  }

  @Override
  public void onEnable() {
    // Don't log enabling, Spigot does that for you automatically!

    // Commands enabled with following method must have entries in plugin.yml
    // getCommand("example").setExecutor(new ExampleCommand(this));
    key = new NamespacedKey(this, "PALADIN_SHIELD");
    System.out.println("Hello world from PaladinShield");
    getServer().getPluginManager().registerEvents(new EnchantPaladinEvent(key), this);
    getServer().getPluginManager().registerEvents(new DamageBlockedByPaladinEvent(key), this);
    getServer().getPluginManager().registerEvents(new PaladinReleaseEvent(key), this);
  }
}
