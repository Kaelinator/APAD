package com.kaelkirk;

import org.bukkit.plugin.java.JavaPlugin;

import com.kaelkirk.commands.WorldTeleport;

public class LimitlessVoidPlugin extends JavaPlugin {

  @Override
  public void onDisable() {
    // Don't log disabling, Spigot does that for you automatically!
  }

  @Override
  public void onEnable() {
    // Don't log enabling, Spigot does that for you automatically!

    // Commands enabled with following method must have entries in plugin.yml
    getCommand("wtp").setExecutor(new WorldTeleport());
    System.out.println("LimitlessVoid enabled!");
    // getServer().getPluginManager().registerEvents(new OnPlayerEnchantEverblade(key), this);
  }
}
