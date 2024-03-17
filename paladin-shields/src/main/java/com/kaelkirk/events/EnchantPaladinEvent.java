package com.kaelkirk.events;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class EnchantPaladinEvent implements Listener {

  private Random random;
  private NamespacedKey key;
  
  public EnchantPaladinEvent(NamespacedKey key) {
    this.key = key;
  }

  @EventHandler
  public void onPrepareEnchantPaladin(PrepareItemEnchantEvent event) {

    ItemStack item = event.getItem();
    if (item.getType() != Material.SHIELD) {
      return;
    }

    ItemMeta meta = item.getItemMeta();

    if (meta.hasEnchants()) {
      return;
    }

    Player enchanter = event.getEnchanter();
    System.out.println("player enchantment seed" + enchanter.getEnchantmentSeed());
    random = new Random(enchanter.getEnchantmentSeed());
    setOffers(random, event.getEnchantmentBonus(), event.getOffers());
  }

  @EventHandler
  public void onEnchantPaladin(EnchantItemEvent event) {
    ItemStack item = event.getItem();

    if (event.isCancelled()) {
      return;
    }

    if (item.getType() != Material.SHIELD) {
      return;
    }
    Player player = event.getEnchanter();

    ItemMeta meta = item.getItemMeta();

    meta.addEnchant(event.getEnchantmentHint(), event.getLevelHint(), false);

    Block enchantTable = event.getEnchantBlock();
    enchantTable.getWorld().playSound(enchantTable.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.0f);
    int cost = event.getExpLevelCost();
    player.setLevel(player.getLevel() - event.getLevelHint());
    int chance = - cost + 40;
    EnchantingInventory inventory = (EnchantingInventory) event.getInventory();
    inventory.getSecondary().subtract(event.getLevelHint());
    int chosen = random.nextInt(chance);

    player.setEnchantmentSeed(random.nextInt());
    System.out.println("Attempted paladin at cost " + cost + " chose " + chosen + " new seed: " + player.getEnchantmentSeed());

    if (cost < 27 || chosen != 0) {
      item.setItemMeta(meta);
      return;
    }

    PersistentDataContainer container = meta.getPersistentDataContainer();
    container.set(key, PersistentDataType.DOUBLE, 0d);

    List<Component> lore = Arrays.asList(new Component[] {
      Component.text("Paladin").decoration(TextDecoration.ITALIC, false).color(TextColor.color(Color.PURPLE.asRGB())),
    });
    meta.lore(lore);

    item.setItemMeta(meta);
  }

  private void setOffers(Random random, int bonus, EnchantmentOffer[] offers) {

    int b = Math.min(bonus, 15);

    int base = random.nextInt(1, 9) + (int) Math.floor(b / 2) + random.nextInt(0, b);


    // top enchantment offer
    int topCost = (int) Math.floor(Math.max(base / 3, 1));
    offers[0] = new EnchantmentOffer(Enchantment.DURABILITY, 1, topCost);

    // middle enchantment offer
    int middleCost = (int) Math.floor(base * 2 / 3 + 1);
    offers[1] = new EnchantmentOffer(Enchantment.DURABILITY, middleCost >= 10 ? 2 : 1, middleCost);

    // bottom enchantment offer
    int bottomCost = (int) Math.floor(Math.max(base, b * 2));
    offers[2] = new EnchantmentOffer(Enchantment.DURABILITY,
      bottomCost >= 10
        ? bottomCost >= 22 ? 3 : 2
        : 1, bottomCost);
  }

}
