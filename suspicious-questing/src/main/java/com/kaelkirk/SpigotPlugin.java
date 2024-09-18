package com.kaelkirk;

import org.bukkit.plugin.java.JavaPlugin;

import com.kaelkirk.events.PlayerInteractWithQuestGiver;

public class SpigotPlugin extends JavaPlugin {

  @Override
  public void onDisable() { }

  @Override
  public void onEnable() {
    getServer().getPluginManager().registerEvents(new PlayerInteractWithQuestGiver(), this);
  }
}
