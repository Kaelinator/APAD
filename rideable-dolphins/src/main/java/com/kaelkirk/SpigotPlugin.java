package com.kaelkirk;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.kaelkirk.events.RideDolphinEvent;
import com.kaelkirk.events.TameDolphinEvent;

public class SpigotPlugin extends JavaPlugin {

  private NamespacedKey ownerKey;
  private ProtocolManager protocolManager;

  @Override
  public void onDisable() {
    // Don't log disabling, Spigot does that for you automatically!
  }

  @Override
  public void onEnable() {
    // Don't log enabling, Spigot does that for you automatically!

    // Commands enabled with following method must have entries in plugin.yml
    // getCommand("example").setExecutor(new ExampleCommand(this));
    System.out.println("Hello world from RideableDolphins");
    ownerKey = new NamespacedKey(this, "Owner");
    protocolManager = ProtocolLibrary.getProtocolManager();
    getServer().getPluginManager().registerEvents(new TameDolphinEvent(ownerKey), this);
    RideDolphinEvent rideDolphinManager = new RideDolphinEvent(ownerKey, this);
    getServer().getPluginManager().registerEvents(rideDolphinManager, this);
    protocolManager.addPacketListener(rideDolphinManager);
    // getServer().getPluginManager().registerEvents(new TamedCreeperLoadsEvent(this, ownerKey), this);
  }
}
