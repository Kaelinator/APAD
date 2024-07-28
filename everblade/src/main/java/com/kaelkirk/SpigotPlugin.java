package com.kaelkirk;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import com.kaelkirk.events.OnPlayerEnchantEverblade;
import com.kaelkirk.events.OnPlayerEverbladeKill;
import com.kaelkirk.events.OnPlayerGrindEverblade;
import com.kaelkirk.events.OnPlayerRepairEverblade;

public class SpigotPlugin extends JavaPlugin {

  private NamespacedKey key;

  @Override
  public void onDisable() { }

  @Override
  public void onEnable() {
    key = new NamespacedKey(this, "EVERBLADE");
    getServer().getPluginManager().registerEvents(new OnPlayerEnchantEverblade(key), this);
    getServer().getPluginManager().registerEvents(new OnPlayerEverbladeKill(key), this);
    getServer().getPluginManager().registerEvents(new OnPlayerRepairEverblade(key), this);
    getServer().getPluginManager().registerEvents(new OnPlayerGrindEverblade(key), this);
  }
}
