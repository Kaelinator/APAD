package com.kaelkirk;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import com.kaelkirk.events.OnPlayerOpenSuperBundle;

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
    System.out.println("Hello world from Super Bundles");
    key = new NamespacedKey(this, "SUPER_BUNDLE");
    getServer().getPluginManager().registerEvents(new OnPlayerOpenSuperBundle(this, key), this);
  }
}
