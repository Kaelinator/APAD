package com.kaelkirk.events;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class OnPlayerEnchantEverblade implements Listener {

  private Random random;
  private NamespacedKey key;

  public OnPlayerEnchantEverblade(NamespacedKey key) {
    random = new Random();
    this.key = key;
  }
  
  @EventHandler
  public void onPlayerEnchant(EnchantItemEvent event) {
    Player player = event.getEnchanter();
    ItemStack item = event.getItem();

    if (event.isCancelled()) {
      return;
    }

    if (item.getType() != Material.GOLDEN_SWORD) {
      return;
    }

    ItemMeta meta = item.getItemMeta();
    PersistentDataContainer container = meta.getPersistentDataContainer();
    container.set(key, PersistentDataType.INTEGER, 0);

    List<Component> lore = Arrays.asList(new Component[] {
      Component.text("Everblade").decoration(TextDecoration.ITALIC, false).color(TextColor.color(Color.PURPLE.asRGB())),
    });
    meta.lore(lore);

    item.setItemMeta(meta);


    // container.

    // int cost = event.getExpLevelCost();
    // if (cost < 27) {
    //   return;
    // }

    player.sendMessage("you enchanted a golden sword");
  }
}
