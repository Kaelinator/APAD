package com.kaelkirk.event;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.kaelkirk.XRayPlugin;

import de.tr7zw.nbtapi.NBTEntity;

public class ShulkerLoadInEvent implements PacketListener {

  private Plugin plugin;

  public ShulkerLoadInEvent(Plugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public Plugin getPlugin() {
    return this.plugin;
  }

  @Override
  public ListeningWhitelist getReceivingWhitelist() {
    return ListeningWhitelist.EMPTY_WHITELIST;
  }

  @Override
  public ListeningWhitelist getSendingWhitelist() {
    return ListeningWhitelist.newBuilder()
      .priority(ListenerPriority.NORMAL)
      .types(PacketType.Play.Server.SPAWN_ENTITY)
      .build();
  }

  @Override
  public void onPacketReceiving(PacketEvent arg0) { /* no-op */ }

  @Override
  public void onPacketSending(PacketEvent event) {
    PacketContainer container = event.getPacket();
    EntityType type = container.getEntityTypeModifier().read(0);

    if (type != EntityType.SHULKER) {
      return;
    }

    Entity entity = container.getEntityModifier(event).read(0);


    NBTEntity shulkerNbt = new NBTEntity(entity);
    String owner = shulkerNbt.getString(XRayPlugin.X_RAY_SHULKER_OWNER_KEY);
    if (owner.length() == 0) {
      return;
    }
    
    String ownerId = owner.substring(9, 36);
    if (!event.getPlayer().getUniqueId().toString().equals(ownerId)) {
      event.setCancelled(true);
    }
  }
  
}
