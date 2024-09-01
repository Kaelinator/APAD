package com.kaelkirk.event;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import com.kaelkirk.brew.BaseXRayBrew;
import com.kaelkirk.manager.XRayManager;

public class PlayerDrinkXRayPotion implements Listener {

  private Plugin plugin;
  private final NamespacedKey key;

  public PlayerDrinkXRayPotion(Plugin plugin, NamespacedKey key) {
    this.plugin = plugin;
    this.key = key;
  }
  
  @EventHandler
  public void onPlayerDrinkPotionEvent(PlayerItemConsumeEvent event) {
    ItemStack item = event.getItem();
    
    if (item.getType() != Material.POTION) {
      return;
    }

    PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();

    if (!container.has(key)) {
      return;
    }

    String materialName = container.get(key, PersistentDataType.STRING);

    if (materialName.equals(BaseXRayBrew.ID)) {
      return;
    }

    Material toSee = Material.getMaterial(materialName);
    Player player = event.getPlayer();

    if (toSee == null) {
      System.err.println("Invalid brew " + materialName);
      return;
    }

    System.out.println("Player " + player.getName() + " sees " + toSee);
    new XRayManager(plugin, key, player, toSee);
  }


}
