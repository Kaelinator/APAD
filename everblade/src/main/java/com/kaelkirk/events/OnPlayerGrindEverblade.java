package com.kaelkirk.events;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

public class OnPlayerGrindEverblade implements Listener {
  
  private NamespacedKey key;
  public OnPlayerGrindEverblade(NamespacedKey key){
    this.key = key;
  }
  @EventHandler
  public void onPlayerGrindEverbladeEvent(PrepareGrindstoneEvent event) {
    GrindstoneInventory inventory = event.getInventory();

    if (!isEverblade(inventory.getUpperItem()) && !isEverblade(inventory.getLowerItem())) {
      return;
    }

    ItemStack result = event.getResult();
    ItemMeta meta = result.getItemMeta();
    PersistentDataContainer container = meta.getPersistentDataContainer();
    container.remove(key);
    meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE);
    meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_SPEED);
    meta.lore(null);
    result.setItemMeta(meta);
    
  }

  // private void sendMessage(PrepareGrindstoneEvent event, String message) {
  //   for (HumanEntity human : event.getViewers()) {
  //     human.sendMessage(message);
  //   }
  // }

  private boolean isEverblade(ItemStack item) {

    if (item == null) {
      return false;
    }

    ItemMeta meta = item.getItemMeta();
    PersistentDataContainer container = meta.getPersistentDataContainer();

    return container.has(key);
  }
}
