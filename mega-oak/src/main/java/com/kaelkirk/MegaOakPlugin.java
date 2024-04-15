package com.kaelkirk;

import java.io.IOException;

import org.bukkit.plugin.java.JavaPlugin;

import com.kaelkirk.event.MegaOakGrowEvent;

public class MegaOakPlugin extends JavaPlugin {

  @Override
  public void onDisable() {
    // Don't log disabling, Spigot does that for you automatically!
  }

  @Override
  public void onEnable() {
    // Don't log enabling, Spigot does that for you automatically!

    // Commands enabled with following method must have entries in plugin.yml
    // getCommand("example").setExecutor(new ExampleCommand(this));
    System.out.println("Hello world from Mega Oak " + this.getDataFolder().getAbsolutePath());
    try {
      getServer().getPluginManager().registerEvents(new MegaOakGrowEvent(new TreeGenerator()), this);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
