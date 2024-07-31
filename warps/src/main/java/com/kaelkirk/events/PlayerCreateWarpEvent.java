package com.kaelkirk.events;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import com.kaelkirk.registry.WarpRegistry;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class PlayerCreateWarpEvent implements Listener {

  private Plugin plugin;
  private WarpRegistry warpRegistry;

  public PlayerCreateWarpEvent(Plugin plugin, WarpRegistry warpRegistry) {
    this.plugin = plugin;
    this.warpRegistry = warpRegistry;
  }
  
  @EventHandler
  public void onPlayerCreateWarp(PlayerInteractEntityEvent event) {
    Player player = event.getPlayer();
    Entity entity = event.getRightClicked();

    if (entity.getType() != EntityType.ITEM_FRAME) {
      return;
    }
    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
      @Override
      public void run() {
        World world = player.getWorld();
        Pair<String, Location> warp = checkForWarp(world, entity.getLocation().add(0, 1, 0));
        if (warp == null) {
          return;
        }
        String name = warp.getLeft();
        Location location = warp.getRight();
        boolean successfullyAddedWarp = warpRegistry.addWarp(name, location);
        System.out.println(successfullyAddedWarp);
        if (successfullyAddedWarp) {
          location.getWorld().playSound(location, Sound.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.AMBIENT, 1.0f, 1.0f);
        }
      }
    });
  }

  @EventHandler
  public void onPlayerCreateWarp(EntityPlaceEvent event) {
    Player player = event.getPlayer();

    if (event.getEntityType() != EntityType.END_CRYSTAL) {
      return;
    }

    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
      @Override
      public void run() {
        World world = player.getWorld();
        Pair<String, Location> warp = checkForWarp(world, event.getBlock().getLocation());
        if (warp == null) {
          return;
        }
        String name = warp.getLeft();
        Location location = warp.getRight();
        boolean successfullyAddedWarp = warpRegistry.addWarp(name, location);
        System.out.println(successfullyAddedWarp);
        if (successfullyAddedWarp) {
          location.getWorld().playSound(location, Sound.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.AMBIENT, 1.0f, 1.0f);
        }
      }
    });
  }

  /**
   * @param world
   * @param location
   * @return the name of the warp that was created, null if invalid warp
   */
  public static Pair<String, Location> checkForWarp(World world, Location location) {

    Block block = world.getBlockAt(location);

    if (block.getType() != Material.BEDROCK) {
      return null;
    }

    boolean hasValidItemFrame = false;
    boolean hasValidEnderCrystal = false;
    String warpName = null;
    Location bedrockLocation = block.getLocation();
    for (Entity entity : block.getChunk().getEntities()) {
      EntityType type = entity.getType();
      if (type != EntityType.ITEM_FRAME && type != EntityType.END_CRYSTAL) {
        continue;
      }

      Location entityLocation = entity.getLocation();
      if (type == EntityType.ITEM_FRAME) {

        ItemFrame itemFrame = (ItemFrame) entity;
        ItemStack itemInFrame = itemFrame.getItem();

        if (itemInFrame.getType() != Material.ENDER_PEARL) {
          continue;
        }

        ItemMeta meta = itemInFrame.getItemMeta();

        if (!meta.hasDisplayName()) {
          continue;
        }

        if (isSameLocation(bedrockLocation.clone().add(0, -1, 0), entityLocation)) {
          hasValidItemFrame = true;
          warpName = PlainTextComponentSerializer.plainText().serialize(meta.displayName());
        }
        continue;
      }

      if (type == EntityType.END_CRYSTAL) {

        if (isSameLocation(bedrockLocation.clone().add(0, 1, 0), entityLocation)) {
          hasValidEnderCrystal = true;
        }
        continue;
      }
    }

    return (hasValidEnderCrystal && hasValidItemFrame)
      ? new ImmutablePair<String, Location>(warpName, bedrockLocation.clone().add(0, 1, 0))
      : null;
  }

  private static boolean isSameLocation(Location location0, Location location1) {
    return (int) location0.getX() == (int) location1.getX()
      && (int) location0.getY() == (int) location1.getY()
      && (int) location0.getZ() == (int) location1.getZ();
  }
}
