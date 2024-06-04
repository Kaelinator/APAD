package com.kaelkirk.loottables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class EndlessVoidChestLootTable implements LootTable {

  private NamespacedKey key;

  private final Material[] ITEMS = {
    Material.DIAMOND,
    Material.AMETHYST_BLOCK,
    Material.GREEN_CANDLE,
    Material.COAL,
    Material.TORCH,
    Material.SOUL_TORCH,
    Material.BUNDLE,
    Material.BEETROOT,
    Material.BOOK,
    Material.COBWEB
  };

  public EndlessVoidChestLootTable(Plugin plugin) {
    this.key = new NamespacedKey(plugin, "ENDLESS_VOID_CHEST_LOOT_TABLE");
  }

  @Override
  public @NotNull NamespacedKey getKey() {
    return key;
  }

  @Override
  public void fillInventory(Inventory inventory, Random random, LootContext context) {
    System.out.println("FILLING INVENTORY");
    for (int i = 0; i < inventory.getSize(); i++) {
      // if (random.nextFloat() < 0.25) {
        inventory.setItem(i, new ItemStack(ITEMS[random.nextInt(ITEMS.length)], random.nextInt(1, 4)));
      // }
    }
  }

  @Override
  public Collection<ItemStack> populateLoot(Random random, LootContext context) {
    System.out.println("POPULATING LOOT");
    int stackCount = random.nextInt(8, 12);
    Collection<ItemStack> loot = new ArrayList<ItemStack>();

    for (int i = 0; i < stackCount; i++) {
      loot.add(new ItemStack(ITEMS[random.nextInt(ITEMS.length)], random.nextInt(1, 4)));
    }

    return loot;
  }
  
}
