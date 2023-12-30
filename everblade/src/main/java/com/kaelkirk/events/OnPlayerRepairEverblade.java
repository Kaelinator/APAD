package com.kaelkirk.events;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.persistence.PersistentDataContainer;

public class OnPlayerRepairEverblade implements Listener {

  private NamespacedKey key;

  public OnPlayerRepairEverblade(NamespacedKey key) {
    this.key = key;
  }
  
  @EventHandler
  public void onPlayerRepairEverblade(PrepareAnvilEvent event) {
    AnvilInventory inventory = event.getInventory();

    ItemStack firstItem = inventory.getFirstItem();
    ItemStack secondItem = inventory.getSecondItem();

    if (firstItem == null || secondItem == null) {
      return;
    }

    if (!isEverblade(firstItem) && !isEverblade(secondItem)) {
      return;
    }

    ItemMeta firstMeta = firstItem.getItemMeta();
    ItemMeta secondMeta = secondItem.getItemMeta();
    /* check if trying to combine with mending book */
    if (isEverblade(firstItem) && secondItem.getType() == Material.ENCHANTED_BOOK) {

      if (!(secondMeta instanceof EnchantmentStorageMeta)) {
        return;
      }

      EnchantmentStorageMeta enchantMeta = (EnchantmentStorageMeta) secondMeta;
      if (enchantMeta.hasStoredEnchant(Enchantment.MENDING)) {
        event.setResult(null);
        return;
      }
    }

    /* check if trying to combine with mending sword */
    if (firstItem.getType() == Material.GOLDEN_SWORD && firstItem.getType() == Material.GOLDEN_SWORD) {

      if (firstMeta.hasEnchant(Enchantment.MENDING) || secondMeta.hasEnchant(Enchantment.MENDING)) {
        event.setResult(null);
        return;
      }
    }

    /* reset damage value */
    ItemStack result = event.getResult();
    if (!isEverblade(result)) {
      return;
    }

    Repairable repairableMeta = (Repairable) result.getItemMeta();
    /* infinitely repairable */
    repairableMeta.setRepairCost(Math.min(5, repairableMeta.getRepairCost()));
    result.setItemMeta(repairableMeta);
  }

  private boolean isEverblade(ItemStack item) {

    if (item == null) {
      return false;
    }

    ItemMeta meta = item.getItemMeta();
    PersistentDataContainer container = meta.getPersistentDataContainer();

    return container.has(key);
  }
  
  // private void sendMessage(PrepareAnvilEvent event, String message) {
  //   for (HumanEntity human : event.getViewers()) {
  //     human.sendMessage(message);
  //   }
  // }
}
