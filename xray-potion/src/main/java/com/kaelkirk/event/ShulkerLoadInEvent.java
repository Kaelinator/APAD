package com.kaelkirk.event;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;

public class ShulkerLoadInEvent implements PacketListener {

  private Plugin plugin;
  private final NamespacedKey key;

  public ShulkerLoadInEvent(Plugin plugin, NamespacedKey key) {
    this.plugin = plugin;
    this.key = key;
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
    PacketContainer packet = event.getPacket();
    EntityType type = packet.getEntityTypeModifier().read(0);

    if (type != EntityType.SHULKER) {
      return;
    }

    Entity entity = packet.getEntityModifier(event).read(0);



    PersistentDataContainer container = entity.getPersistentDataContainer();
    String ownerId = container.get(key, PersistentDataType.STRING);

    if (ownerId == null || ownerId.length() == 0) {
      return;
    }

    if (!event.getPlayer().getUniqueId().toString().equals(ownerId)) {
      event.setCancelled(true);
    }
  }
  
}
