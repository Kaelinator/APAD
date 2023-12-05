package com.kaelkirk.events;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.google.common.collect.Multimap;

public class OnPlayerEverbladeKill implements Listener {
  
  private NamespacedKey key;

  public OnPlayerEverbladeKill(NamespacedKey key) {
    this.key = key;
  }

  @EventHandler
  public void onPlayerEverbladeKill(EntityDamageByEntityEvent event) {
    Entity damagerEntity = event.getDamager();
    Entity damagedEntity = event.getEntity();

    if (!(damagerEntity instanceof Player)) {
      return;
    }

    if (!(damagedEntity instanceof LivingEntity)) {
      return;
    }

    LivingEntity damaged = (LivingEntity) damagedEntity;
    if (!(event.getFinalDamage() > damaged.getHealth())) {
      return;
    }

    if (event.getCause() != DamageCause.ENTITY_ATTACK && event.getCause() != DamageCause.ENTITY_SWEEP_ATTACK) {
      return;
    }

    Player damager = (Player) damagerEntity;
    ItemStack damagingItem = damager.getEquipment().getItemInMainHand();

    ItemMeta meta = damagingItem.getItemMeta();
    PersistentDataContainer container = meta.getPersistentDataContainer();

    if (!container.has(key)) {
      return;
    }

    int numberOfKills = container.get(key, PersistentDataType.INTEGER);
    numberOfKills += 100;
    container.set(key, PersistentDataType.INTEGER, numberOfKills);

    meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE);
    meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_SPEED);

    AttributeModifier damageModifier = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", calculateAttackDamage(numberOfKills), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
    AttributeModifier speedModifier = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", -2.4, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
    meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, damageModifier);
    meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, speedModifier);
    
    damagingItem.setItemMeta(meta);
    
    
    damager.sendMessage("Everblade kill number: " + numberOfKills);
  }


  private double calculateAttackDamage(int killCount) {
    return Math.log(((double) killCount + 1000.0d) / 1000.0d) / Math.log(1.1d) + 4;
  }
}
