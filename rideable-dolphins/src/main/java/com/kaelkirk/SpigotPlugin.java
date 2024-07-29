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
  public void onDisable() { }

  @Override
  public void onEnable() {
    ownerKey = new NamespacedKey(this, "Owner");
    protocolManager = ProtocolLibrary.getProtocolManager();
    getServer().getPluginManager().registerEvents(new TameDolphinEvent(ownerKey), this);
    RideDolphinEvent rideDolphinManager = new RideDolphinEvent(ownerKey, this);
    getServer().getPluginManager().registerEvents(rideDolphinManager, this);
    protocolManager.addPacketListener(rideDolphinManager);
  }
}
