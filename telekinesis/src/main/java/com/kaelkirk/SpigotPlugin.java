package com.kaelkirk;

import org.bukkit.plugin.java.JavaPlugin;

import com.kaelkirk.events.PlayerUseTelekinesis;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

public class SpigotPlugin extends JavaPlugin {

  public static StateFlag TELEKINESIS;

  @Override
  public void onDisable() {
    // Don't log disabling, Spigot does that for you automatically!
  }

  @Override
  public void onLoad() {

    FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
    try {
      StateFlag flag = new StateFlag("telekinesis", true);
      registry.register(flag);
      TELEKINESIS = flag;
    } catch (FlagConflictException e) {
      Flag<?> existing = registry.get("telekinesis");
      if (existing instanceof StateFlag) {
        TELEKINESIS = (StateFlag) existing;
      } else {
        System.err.println("CONFLICTING telekinesis FLAG!");
      }
    }
  }

  @Override
  public void onEnable() {
    // Don't log enabling, Spigot does that for you automatically!

    // Commands enabled with following method must have entries in plugin.yml
    // getCommand("example").setExecutor(new ExampleCommand(this));
    System.out.println("Hello world from Telekinesis");
    getServer().getPluginManager().registerEvents(new PlayerUseTelekinesis(this), this);
  }
}
