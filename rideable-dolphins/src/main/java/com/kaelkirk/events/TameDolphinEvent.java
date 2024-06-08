package com.kaelkirk.events;

import java.util.Random;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.World;
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

public class TameDolphinEvent implements Listener {

  private Random random;
  private NamespacedKey ownerKey;

  public TameDolphinEvent(NamespacedKey ownerKey) {
    random = new Random();
    this.ownerKey = ownerKey;
  }
  
  @EventHandler
  public void onTameDolphinEvent(PlayerInteractAtEntityEvent event) {
    Player player = event.getPlayer();
    Entity entity = event.getRightClicked();

    if (entity.getType() != EntityType.DOLPHIN) {
      return;
    }

    EquipmentSlot slot = event.getHand();
    EntityEquipment equipment = player.getEquipment();
    ItemStack item = equipment.getItem(slot);
    Material itemType = item.getType();
    World world = player.getWorld();

    if (itemType != Material.INK_SAC) {
      return;
    }

    PersistentDataContainer container = entity.getPersistentDataContainer();

    if (container.has(ownerKey)) {
      world.spawnParticle(Particle.SMOKE_NORMAL, entity.getLocation().add(0, 1, 0), 10, 0.25, 0.25, 0.25, 0);
      return;
    }

    event.setCancelled(true);
    if (!player.getGameMode().equals(GameMode.CREATIVE)) {
      item.subtract(1);
    }

    if (random.nextInt(10) != 0) {
      world.spawnParticle(Particle.SMOKE_NORMAL, entity.getLocation().add(0, 1, 0), 10, 0.25, 0.25, 0.25, 0);
      return;
    }

    /* you tamed the dolphin */
    container.set(ownerKey, PersistentDataType.STRING, player.getUniqueId().toString());

    world.spawnParticle(Particle.HEART, entity.getLocation().add(0, 1, 0), 10, 0.25, 0.25, 0.25);

  }
}
