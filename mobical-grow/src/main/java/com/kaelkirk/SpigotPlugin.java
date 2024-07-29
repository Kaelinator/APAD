package com.kaelkirk;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import com.kaelkirk.events.PlantMobEvent;
import com.kaelkirk.events.UprootMobEvent;
import com.kaelkirk.util.MobicalManager;
import com.kaelkirk.events.PlayerFarmMobEvent;

public class SpigotPlugin extends JavaPlugin {

  private NamespacedKey mobicalKey;

  @Override
  public void onDisable() { }

  @Override
  public void onEnable() {
    mobicalKey = new NamespacedKey(this, "MOBICAL_GROW");
    MobicalManager manager = new MobicalManager(mobicalKey);
    getServer().getPluginManager().registerEvents(new PlantMobEvent(manager), this);
    getServer().getPluginManager().registerEvents(new UprootMobEvent(manager), this);
    getServer().getPluginManager().registerEvents(new PlayerFarmMobEvent(manager), this);
  }
}
