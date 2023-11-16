package com.kaelkirk.events;

import java.util.Collection;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.joml.Random;

public class RandomItemBlockBreakEvent implements Listener {
  
  private Random random;
  private Material[] allMaterials;
  private JavaPlugin plugin;

  public RandomItemBlockBreakEvent(JavaPlugin plugin) {
    random = new Random();
    allMaterials = Material.values();
    this.plugin = plugin;
  }

  @EventHandler
  public void onBlockBreakEvent(BlockBreakEvent event) {

    boolean isNotPlayerPlaced = event.getBlock().getMetadata("isPlayerPlaced").isEmpty();
    if (!isNotPlayerPlaced) {
      return;
    }

    event.setDropItems(false);
    Collection<ItemStack> itemStacks = event.getBlock().getDrops(event.getPlayer().getInventory().getItemInMainHand());

    if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
      return;
    }

    if (itemStacks.size() == 0) {
      return;
    }

    Material randomMaterial = generateRandomMaterial();
    itemStacks.add(new ItemStack(randomMaterial));
    World world = event.getBlock().getWorld();

    for (ItemStack stack : itemStacks) {
      world.dropItemNaturally(event.getBlock().getLocation(), stack);
    }
  }

  @EventHandler
  public void onBlockPlaceEvent(BlockPlaceEvent event) {
    event.getBlock().setMetadata("isPlayerPlaced", new FixedMetadataValue(this.plugin, true));
  }

  public Material generateRandomMaterial() {
    int randomItemOrdinal = random.nextInt(allMaterials.length); // pick random item using random index

    Material result = allMaterials[randomItemOrdinal];

    if (result == Material.BEDROCK ||
        result == Material.STRUCTURE_BLOCK ||
        result == Material.STRUCTURE_VOID ||
        result == Material.COMMAND_BLOCK_MINECART ||
        result == Material.REPEATING_COMMAND_BLOCK ||
        result == Material.CHAIN_COMMAND_BLOCK ||
        result == Material.COMMAND_BLOCK ||
        result == Material.END_GATEWAY ||
        result == Material.END_PORTAL ||
        result == Material.END_PORTAL_FRAME ||
        result == Material.JIGSAW ||
        result == Material.FIRE ||
        result == Material.SOUL_FIRE ||
        result == Material.LIGHT ||
        result.isAir() ||
        (!result.isBlock() && !result.isItem())
    ) {
      return generateRandomMaterial();
    }

    return result;
  }
}
