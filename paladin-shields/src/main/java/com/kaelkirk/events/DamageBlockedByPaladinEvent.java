package com.kaelkirk.events;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class DamageBlockedByPaladinEvent implements Listener {


  private NamespacedKey key;

  public DamageBlockedByPaladinEvent(NamespacedKey key) {
    this.key = key;
  }

  @EventHandler
  public void onDamageBlockedByPaladinEvent(EntityDamageEvent event) {
    if (event.getEntityType() != EntityType.PLAYER) {
      return;
    }

    Player player = (Player) event.getEntity();

    if (!player.isBlocking()) {
      return;
    }

    ItemStack shield = player.getEquipment().getItem(player.getHandRaised());

    ItemMeta meta = shield.getItemMeta();
    PersistentDataContainer container = meta.getPersistentDataContainer();

    if (!container.has(key)) {
      return;
    }

    if (event.getFinalDamage() != 0) {
      return;
    }

    double blockedDamage = event.getDamage();
    double totalBlockedDamage = container.get(key, PersistentDataType.DOUBLE);
    totalBlockedDamage += blockedDamage;
    container.set(key, PersistentDataType.DOUBLE, totalBlockedDamage);
    shield.setItemMeta(meta);


    // player.sendMessage("total blocked damage: " + totalBlockedDamage);
    
  }

}
