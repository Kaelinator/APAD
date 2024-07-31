package com.kaelkirk.events;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import com.kaelkirk.registry.WarpRegistry;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class PlayerEatPearlEvent implements Listener {

  private HashMap<UUID, Integer> timeEaten;
  private HashMap<UUID, Long> lastTimeEaten;
  private WarpRegistry warpRegistry;
  private Plugin plugin;

  public PlayerEatPearlEvent(Plugin plugin, WarpRegistry warpRegistry) {
    timeEaten = new HashMap<UUID, Integer>();
    lastTimeEaten = new HashMap<UUID, Long>();
    this.warpRegistry = warpRegistry;
    this.plugin = plugin;
  }
  
  @EventHandler
  public void onPlayerEatPearl(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    ItemStack item = event.getItem();

    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    if (item == null) {
      return;
    }

    if (item.getType() != Material.ENDER_PEARL) {
      return;
    }

    ItemMeta meta = item.getItemMeta();

    if (!meta.hasDisplayName()) {
      return;
    }

    event.setCancelled(true);

    UUID playerId = player.getUniqueId();
    World world = player.getWorld();
    long currentTime = world.getFullTime();

    int eatTime = 0;
    if (timeEaten.containsKey(playerId)) {
      if (currentTime - lastTimeEaten.get(playerId) <= 4) {
        eatTime = timeEaten.get(playerId);
      }
    }
    eatTime++;
    timeEaten.put(playerId, eatTime);
    lastTimeEaten.put(playerId, currentTime);
    // player.sendMessage("lastEatTime: " + currentTime);

    if (eatTime < 2) {
      return;
    }

    Location playerLocation = player.getEyeLocation();
    Location particleLocation = playerLocation.add(playerLocation.getDirection().multiply(0.4));

    world.spawnParticle(Particle.ITEM, particleLocation, 5, 0.1, -1.0, 0.1, 0, new ItemStack(Material.ENDER_PEARL));
    world.playSound(player, Sound.ENTITY_GENERIC_EAT, SoundCategory.PLAYERS, 1.0f, 1.0f);

    if (eatTime < 8) {
      return;
    }

    timeEaten.remove(playerId);
    world.playSound(player, Sound.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 1.0f, 1.0f);
    player.setFoodLevel(player.getFoodLevel() + 5);
    item.subtract(1);


    String warpName = PlainTextComponentSerializer.plainText().serialize(meta.displayName());
    if (!warpRegistry.hasWarp(world, warpName)) {
      return;
    }

    Location warpLocation = warpRegistry.getWarpLocation(world, warpName);
    if (PlayerCreateWarpEvent.checkForWarp(world, warpLocation.clone().add(0, -1, 0)) == null) {
      warpRegistry.removeWarp(world, warpName);
      return;
    }
    world.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0f, 1.0f);
    player.teleport(warpLocation.clone().add(0.5, 0, 0.5));
    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
      @Override
      public void run() {
        world.playSound(warpLocation, Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0f, 1.0f);
      }
    });

  }
}
