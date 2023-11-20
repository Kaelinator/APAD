package com.kaelkirk.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.kaelkirk.brew.BaseXRayBrew;
import com.kaelkirk.brew.BlockXRayBrew;

import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTItem;
import it.unimi.dsi.fastutil.ints.IntArrayList;

public class PlayerDrinkXRayPotion implements Listener, Runnable {

  private HashMap<UUID, Integer> timeLeft;
  private HashMap<UUID, Material> material;
  private HashMap<UUID, List<Entity>> shulkers;
  private int xRayEffectTask;
  private final int refreshDelay = 4 * 20;
  private final int duration = 120 * 20;

  public PlayerDrinkXRayPotion(Plugin plugin) {
    timeLeft = new HashMap<UUID, Integer>();
    material = new HashMap<UUID, Material>();
    shulkers = new HashMap<UUID, List<Entity>>();
    xRayEffectTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, refreshDelay);
  }
  
  @EventHandler
  public void onPlayerDrinkPotionEvent(PlayerItemConsumeEvent event) {
    ItemStack itemConsumed = event.getItem();
    
    if (itemConsumed.getType() != Material.POTION) {
      return;
    }

    NBTItem item = new NBTItem(itemConsumed);

    if (!item.hasTag(BaseXRayBrew.KEY) || !item.getString(BaseXRayBrew.KEY).equals(BaseXRayBrew.ID)) {
      return;
    }

    if (!item.hasTag(BlockXRayBrew.KEY)) {
      return;
    }

    Material toSee = Material.getMaterial(item.getString(BlockXRayBrew.KEY));
    Player player = event.getPlayer();

    if (toSee == null) {
      player.sendMessage("Invalid brew " + item.getString(BlockXRayBrew.KEY));
      return;
    }

    UUID playerId = player.getUniqueId();
    timeLeft.put(playerId, duration);
    material.put(playerId, toSee);
    player.sendMessage("you drank " + toSee);
  }

  public void stopXRayEffect() {
    Bukkit.getScheduler().cancelTask(xRayEffectTask);
    for (List<Entity> shulks : shulkers.values()) {
      for (Entity shulker : shulks) {
        shulker.remove();
      }
    }
  }

  @Override
  public void run() {
    /* apply the effect for players with x-ray effect */

    PotionEffect glow = new PotionEffect(PotionEffectType.GLOWING, duration, 0, true, false);
    PotionEffect invisible = new PotionEffect(PotionEffectType.INVISIBILITY, duration, 0, true, false);
    for (UUID playerId : timeLeft.keySet()) {
      int remaining = timeLeft.get(playerId);
      List<Entity> theseShulkers = shulkers.get(playerId);
      if (theseShulkers != null) {
        for (Entity shulker : theseShulkers) {
          shulker.remove();
        }
      }
      if (remaining < 0) {
        timeLeft.remove(playerId);
        material.remove(playerId);
        continue;
      }
      remaining -= refreshDelay;
      timeLeft.put(playerId, remaining);
      Material toSee = material.get(playerId);

      Location location = Bukkit.getPlayer(playerId).getEyeLocation();

      ArrayList<Block> allBlocksWithinRadius = getBlocksAtRadius(location, 50);
      Player player = Bukkit.getPlayer(playerId);
      World world = player.getWorld();

      theseShulkers = new ArrayList<Entity>();
      for (Block block : allBlocksWithinRadius) {
        if (block.getType() == toSee) {
          Entity shulker = block.getWorld().spawnEntity(block.getLocation(), EntityType.SHULKER);
          NBTEntity shulkerNbt = new NBTEntity(shulker);
          shulkerNbt.setBoolean("NoAI", true);
          shulkerNbt.setBoolean("Invulnerable", true);
          shulkerNbt.setBoolean("Silent", true);
          glow.apply((LivingEntity) shulker);
          invisible.apply((LivingEntity) shulker);
          theseShulkers.add(shulker);
          PacketContainer destroyPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
          destroyPacket.getModifier().write(0, new IntArrayList(new int[] { shulker.getEntityId() }));
          for (Player notThePlayer : world.getPlayers()) {
            if (notThePlayer.getUniqueId() != playerId) {
              ProtocolLibrary.getProtocolManager().sendServerPacket(notThePlayer, destroyPacket);
            }
          }
        }
      }
      shulkers.put(playerId, theseShulkers);
    }
  }
    private ArrayList<Block> getBlocksAtRadius(Location location, double radius) {
    World world = location.getWorld();
    ArrayList<Block> blocksAtRadius = new ArrayList<Block>();
    double startingX = location.getX();
    double startingY = location.getY();
    double startingZ = location.getZ();
    for (double x = startingX - radius; x <= startingX + radius; x++) {
      for (double y = startingY - radius; y <= startingY + radius; y++) {
        for (double z = startingZ - radius; z <= startingZ + radius; z++) {
          Location locationAtRadius = new Location(world, x, y, z);
          Block b = world.getBlockAt(locationAtRadius);
          blocksAtRadius.add(b);
        }
      }
    }
    return blocksAtRadius;
  }

}
