package com.kaelkirk.events;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.kaelkirk.gui.SuperBundle;

public class OnPlayerOpenSuperBundle implements Listener {

  private Plugin plugin;
  private NamespacedKey bundleKey;
  private NamespacedKey buttonKey;
  private HashMap<ItemStack, SuperBundle> bundleMapping;

  public OnPlayerOpenSuperBundle(Plugin plugin, NamespacedKey bundleKey, NamespacedKey buttonKey) {
    this.plugin = plugin;
    bundleMapping = new HashMap<ItemStack, SuperBundle>();
    this.bundleKey = bundleKey;
    this.buttonKey = buttonKey;
  }

  @EventHandler
  public void onPlayerOpenSuperBundle(PlayerInteractEvent event) {
    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    if (event.getItem() == null || event.getItem().getType() != Material.BUNDLE) {
      return;
    }

    event.setCancelled(true);

    SuperBundle superBundle;
    if (bundleMapping.containsKey(event.getItem())) {
      superBundle = bundleMapping.get(event.getItem());
    } else {
      superBundle = new SuperBundle(event.getItem(), this.plugin, bundleKey, buttonKey);
    }


    Player player = event.getPlayer();
    player.openInventory(superBundle.getBundleInventory());
    
  }

}
