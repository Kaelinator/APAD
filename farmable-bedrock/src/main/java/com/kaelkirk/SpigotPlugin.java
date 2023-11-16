package com.kaelkirk;

import org.bukkit.plugin.java.JavaPlugin;

import com.kaelkirk.events.AnvilFallingEvent;


public class SpigotPlugin extends JavaPlugin {
  @Override
  public void onDisable() {
  }

  @Override
  public void onEnable() {

    System.out.println("Hello world from Farmable Bedrock");

    this.saveDefaultConfig();
    int requiredAnvilFallenDistance = this.getConfig().getInt("requiredAnvilFallenDistance");
    getServer().getPluginManager().registerEvents(new AnvilFallingEvent(this, requiredAnvilFallenDistance), this);
  }
}
