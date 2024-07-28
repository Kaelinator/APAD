package com.kaelkirk;

import java.io.IOException;

import org.bukkit.plugin.java.JavaPlugin;

import com.kaelkirk.event.MegaOakGrowEvent;

public class MegaOakPlugin extends JavaPlugin {

  @Override
  public void onDisable() { }

  @Override
  public void onEnable() {
    try {
      getServer().getPluginManager().registerEvents(new MegaOakGrowEvent(new TreeGenerator()), this);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
