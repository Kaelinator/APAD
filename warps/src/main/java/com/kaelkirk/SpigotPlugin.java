package com.kaelkirk;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import com.kaelkirk.events.PlayerCreateWarpEvent;
import com.kaelkirk.events.PlayerEatPearlEvent;
import com.kaelkirk.registry.WarpRegistry;

public class SpigotPlugin extends JavaPlugin {

  private NamespacedKey warpKey;
  private WarpRegistry warpRegistry;
  @Override
  public void onDisable() {
    // Don't log disabling, Spigot does that for you automatically!
  }

  @Override
  public void onEnable() {
    // Don't log enabling, Spigot does that for you automatically!

    // Commands enabled with following method must have entries in plugin.yml
    // getCommand("example").setExecutor(new ExampleCommand(this));
    System.out.println("Hello world from Warps");

    warpKey = new NamespacedKey(this, "Warps");
    warpRegistry = new WarpRegistry(warpKey);

    getServer().getPluginManager().registerEvents(new PlayerEatPearlEvent(this, warpRegistry), this);
    getServer().getPluginManager().registerEvents(new PlayerCreateWarpEvent(this, warpRegistry), this);
  }
}
