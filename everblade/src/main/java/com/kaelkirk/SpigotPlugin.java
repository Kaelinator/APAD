package com.kaelkirk;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import com.kaelkirk.events.OnPlayerEnchantEverblade;
import com.kaelkirk.events.OnPlayerEverbladeKill;

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
    key = new NamespacedKey(this, "EVERBLADE");
    System.out.println("Hello world from Everblade");
    getServer().getPluginManager().registerEvents(new OnPlayerEnchantEverblade(key), this);
    getServer().getPluginManager().registerEvents(new OnPlayerEverbladeKill(key), this);
  }
}
