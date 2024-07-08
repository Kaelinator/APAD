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
  public void onDisable() {
    // Don't log disabling, Spigot does that for you automatically!
  }

  @Override
  public void onEnable() {
    // Don't log enabling, Spigot does that for you automatically!

    // Commands enabled with following method must have entries in plugin.yml
    mobicalKey = new NamespacedKey(this, "MOBICAL_GROW");
    MobicalManager manager = new MobicalManager(mobicalKey);
    System.out.println("Hello world from Mobical Grow");
    getServer().getPluginManager().registerEvents(new PlantMobEvent(manager), this);
    getServer().getPluginManager().registerEvents(new UprootMobEvent(manager), this);
    getServer().getPluginManager().registerEvents(new PlayerFarmMobEvent(manager), this);
  }
}
