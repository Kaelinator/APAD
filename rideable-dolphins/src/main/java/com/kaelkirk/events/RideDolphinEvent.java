package com.kaelkirk.events;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.reflect.FieldAccessException;

public class RideDolphinEvent implements Listener, PacketListener {

  private NamespacedKey ownerKey;
  Set<Entity> riddenDolphins;
  private Plugin plugin;

  public RideDolphinEvent(NamespacedKey ownerKey, Plugin plugin) {
    this.ownerKey = ownerKey;
    riddenDolphins = new HashSet<Entity>();
    this.plugin = plugin;
  }

  @EventHandler
  public void onRideDolphinEvent(PlayerInteractAtEntityEvent event) {
    Player player = event.getPlayer();
    Entity entity = event.getRightClicked();

    if (entity.getType() != EntityType.DOLPHIN) {
      return;
    }
    
    EquipmentSlot slot = event.getHand();
    EntityEquipment equipment = player.getEquipment();
    ItemStack item = equipment.getItem(slot);
    Material itemType = item.getType();

    if (itemType == Material.LEAD) {
      return;
    }

    PersistentDataContainer container = entity.getPersistentDataContainer();

    if (!container.has(ownerKey)) {
      return;
    }

    UUID ownerId = UUID.fromString(container.get(ownerKey, PersistentDataType.STRING));
    UUID playerId = player.getUniqueId();
    if (!ownerId.equals(playerId)) {
      return;
    }

    entity.addPassenger(player);

  }

  @Override
  public Plugin getPlugin() {
    return this.plugin;
  }

  @Override
  public ListeningWhitelist getReceivingWhitelist() {
    return ListeningWhitelist.newBuilder().priority(ListenerPriority.NORMAL).types(PacketType.Play.Client.STEER_VEHICLE)
        .build();
  }

  @Override
  public ListeningWhitelist getSendingWhitelist() {
    return ListeningWhitelist.EMPTY_WHITELIST;
  }

  @Override
  public void onPacketReceiving(PacketEvent event) {
    if (event.isCancelled()) {
      return;
    }
    Entity vehicle = event.getPlayer().getVehicle();

    if (vehicle == null || vehicle.getType() != EntityType.DOLPHIN) {
      return;
    }

    Block block = vehicle.getLocation().getBlock();
    Material vehicleMedium = block.getType();

    boolean isInWaterLoggedBlock = false;
    if (block.getBlockData() instanceof Waterlogged) {
      isInWaterLoggedBlock = ((Waterlogged) block.getBlockData()).isWaterlogged();
    }

    Player player = event.getPlayer();
    Location playerLocation = player.getEyeLocation();
    vehicle.setRotation(playerLocation.getYaw(), playerLocation.getPitch());

    if (vehicleMedium != Material.WATER && vehicleMedium != Material.BUBBLE_COLUMN && !isInWaterLoggedBlock) {
      return;
    }

    float sidewaysSpeed, forwardSpeed;
    boolean jumping;
    try {
      sidewaysSpeed = event.getPacket().getFloat().read(0);
      forwardSpeed = event.getPacket().getFloat().read(1);
      jumping = event.getPacket().getBooleans().read(0);
    } catch (FieldAccessException ex) {
      ex.printStackTrace();
      return;
    }

    Vector forward = playerLocation.getDirection();
    Vector sideways = forward.clone().crossProduct(new Vector(0,-1,0)); 
    Vector total = forward.multiply(forwardSpeed).add(sideways.multiply(sidewaysSpeed));

    Vector result = vehicle.getVelocity().subtract(total).multiply(-1);
    if (result.length() < 0.1) {
      return;
    }
    if (jumping) {
      result.setY(1.0);
    }

    vehicle.setVelocity(result);
  }

  @Override
  public void onPacketSending(PacketEvent arg0) {
  }

}
