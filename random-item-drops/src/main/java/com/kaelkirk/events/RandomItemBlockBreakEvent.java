package com.kaelkirk.events;

import java.util.Collection;

import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.joml.Random;

import com.kaelkirk.managers.RandomItemPicker;

public class RandomItemBlockBreakEvent implements Listener {
  
  private Random random;
  private RandomItemPicker picker;
  private JavaPlugin plugin;

  public RandomItemBlockBreakEvent(JavaPlugin plugin) {
    random = new Random();
    picker = new RandomItemPicker();
    this.plugin = plugin;
  }

  // useless singular items = stack or just more?
  // lump useless singular items together

  @EventHandler
  public void onBlockBreakEvent(BlockBreakEvent event) {

    if (event.isCancelled()) {
      return;
    }

    Block block = event.getBlock();

    boolean isNotPlayerPlaced = block.getMetadata("isPlayerPlaced").isEmpty();

    if (!isNotPlayerPlaced) {
      return;
    }


    Player player = event.getPlayer();

    if (player.getGameMode() == GameMode.CREATIVE) {
      return;
    }
    
    Collection<ItemStack> drops = block.getDrops(player.getEquipment().getItemInMainHand(), player);

    if (drops.size() == 0) {
      return;
    }

    float breakSpeed = (float) Math.min(block.getBreakSpeed(player), 1.0);

    // f(0) = 1
    // f(0.05) = 4
    // f(1) = 20
    int chance = (int) Math.floor(Math.pow(1000 * breakSpeed, 0.33333d) + breakSpeed * 10);

    int randomNumber = random.nextInt(chance); 
    
    if (randomNumber != 0) {
      return;
    }

    ItemStack randomItem = picker.pickRandomItem();
    World world = block.getWorld();
    world.dropItemNaturally(block.getLocation(), randomItem );
  }

  @EventHandler
  public void onBlockPlaceEvent(BlockPlaceEvent event) {
    event.getBlock().setMetadata("isPlayerPlaced", new FixedMetadataValue(this.plugin, true));
  }

}
