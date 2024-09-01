package com.kaelkirk.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;

import it.unimi.dsi.fastutil.ints.IntArrayList;

public class XRayManager {

  private static HashMap<Integer, XRayManager> managers;

  private Player player;
  private UUID playerId;
  private HashMap<Block, Entity> shulkers;
  private Material toSee;
  private int timeLeft; // ticks
  private int reSearchTask;

  private final int SEARCH_DELAY = 4 * 20; // 4 seconds
  private final int RE_SEARCH_DELAY = 10;  // 0.5 seconds
  private final int DURATION = 120 * 20;   // 120 seconds
  private final int GLOWING_BLOCK_LIMIT = 2048;
  private final int SEARCH_LIMIT = 131_072; // 2 ^ 17 aka side length 64 (radius ~ 45)
  
  private final PotionEffect GLOW_EFFECT = new PotionEffect(PotionEffectType.GLOWING, DURATION, 0, true, false);
  private final PotionEffect INVISIBLE_EFFECT = new PotionEffect(PotionEffectType.INVISIBILITY, DURATION, 0, true, false);
  private final NamespacedKey key;

  public XRayManager(Plugin plugin, NamespacedKey key, Player player, Material toSee) {
    this.player = player;
    this.toSee = toSee;
    this.key = key;
    this.playerId = player.getUniqueId();
    reSearchTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new BlockSearcher(), 0, RE_SEARCH_DELAY);
    if (managers == null) {
      managers = new HashMap<Integer, XRayManager>();
    }
    managers.put(reSearchTask, this);
    timeLeft = DURATION;
    shulkers = new HashMap<Block, Entity>();
  }

  public static void stopAllXRayEffects() {
    Iterator<XRayManager> itr = managers.values().iterator();
    while (itr.hasNext()) {
      itr.next().stopXRayEffect();
    }
  }
  
  private void stopXRayEffect() {
    System.out.println("Stopping xray effect");
    Bukkit.getScheduler().cancelTask(reSearchTask);
    for (Entity shulker : shulkers.values()) {
      shulker.remove();
    }
    shulkers.clear();
    managers.remove(reSearchTask); // garbage collect?
  }

  /**
   * Looks through a radius of blocks for a specific block
   */
  class BlockSearcher implements Runnable {
    private int timeUntilSearch = 0;
    private ArrayList<Block> highlightedBlocks;

    @Override
    public void run() {
      /* apply the effect for players with x-ray effect */


      if (timeLeft <= 0) {
        stopXRayEffect();
        return;
      }

      if (player == null) {
        return;
      }

      timeLeft -= RE_SEARCH_DELAY;
      timeUntilSearch -= RE_SEARCH_DELAY;

      if (timeUntilSearch <= 0) {
        /* search blocks entirely */

        // System.out.println("deleting " + shulkers.values().size());
        for (Entity shulker : shulkers.values()) {
          shulker.remove();
        }
        shulkers.clear();
        timeUntilSearch = SEARCH_DELAY;

        highlightedBlocks = getBlocksWithLimits(player.getEyeLocation(), toSee, SEARCH_LIMIT, GLOWING_BLOCK_LIMIT);
        // System.out.println("Found " + highlightedBlocks.size());

        for (Block block : highlightedBlocks) {
          shulkers.put(block, spawnShulker(block));
        }

        return;
      }

      // re search blocks
      Iterator<Block> itr = highlightedBlocks.iterator();
      while (itr.hasNext()) {
        Block block = itr.next();
        if (block.getType() == toSee) {
          continue;
        }

        // block no longer material to see
        shulkers.get(block).remove();
        shulkers.remove(block);
        itr.remove();
      }
    }
  }

  /**
   * Spirals outward searchLimit number of blocks or until highlightLimit
   * number of blocks have been found
   * @param location
   * @param searchLimit
   * @param highlightLimit
   * @return
   */
  private ArrayList<Block> getBlocksWithLimits(Location location, Material toSee, int searchLimit, int highlightLimit) {
    World world = location.getWorld();
    ArrayList<Block> blocks = new ArrayList<Block>();

    int startingX = location.getBlockX();
    int startingY = location.getBlockY();
    int startingZ = location.getBlockZ();

    int radius = 1;
    Block block;
    while (searchLimit > 0 && highlightLimit > 0) {
      for (int i = -radius; i <= radius; i++) {
        for (int j = -radius + Math.abs(i); j <= radius - Math.abs(i); j++) {
          int k = radius - Math.abs(i) - Math.abs(j);
          block = world.getBlockAt(startingX + i, startingY + j, startingZ + k);
          searchLimit--;
          if (block.getType() == toSee) {
            highlightLimit--;
            blocks.add(block);
          }
          if (searchLimit == 0 || highlightLimit == 0) break;
          if (k == 0) continue;
          block = world.getBlockAt(startingX + i, startingY + j, startingZ - k);
          searchLimit--;
          if (block.getType() == toSee) {
            highlightLimit--;
            blocks.add(block);
          }
          if (searchLimit == 0 || highlightLimit == 0) break;
        }
      }
      radius++;
    }

    return blocks;
  }

  private Entity spawnShulker(Block block) {
    World world = block.getWorld();

    LivingEntity shulker = (LivingEntity) world.spawnEntity(block.getLocation(), EntityType.SHULKER);
    shulker.setAI(false);
    shulker.setInvulnerable(true);
    shulker.setInvisible(true);
    shulker.setSilent(true);
    shulker.setGlowing(true);
    PersistentDataContainer container = shulker.getPersistentDataContainer();
    container.set(key, PersistentDataType.STRING, playerId.toString());

    PacketContainer destroyPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
    destroyPacket.getModifier().write(0, new IntArrayList(new int[] { shulker.getEntityId() }));
    for (Player notThePlayer : world.getPlayers()) {
      if (notThePlayer.getUniqueId() != playerId) {
        ProtocolLibrary.getProtocolManager().sendServerPacket(notThePlayer, destroyPacket);
      }
    }

    return shulker;
  }
}
