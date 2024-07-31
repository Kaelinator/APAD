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
  public void onDisable() { }

  @Override
  public void onEnable() {
    warpKey = new NamespacedKey(this, "Warps");
    warpRegistry = new WarpRegistry(warpKey);

    getServer().getPluginManager().registerEvents(new PlayerEatPearlEvent(this, warpRegistry), this);
    getServer().getPluginManager().registerEvents(new PlayerCreateWarpEvent(this, warpRegistry), this);
  }
}
