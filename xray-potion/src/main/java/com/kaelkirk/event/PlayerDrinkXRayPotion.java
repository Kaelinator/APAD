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
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.reflect.accessors.FieldAccessor;
import com.kaelkirk.brew.BaseXRayBrew;
import com.kaelkirk.brew.BlockXRayBrew;

import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTItem;

public class PlayerDrinkXRayPotion implements Listener, Runnable, PacketListener {

  private Plugin plugin;
  private HashMap<UUID, Integer> timeLeft;
  private HashMap<UUID, Material> material;
  private HashMap<UUID, List<Entity>> shulkers;
  private int xRayEffectTask;
  private final int refreshDelay = 4 * 20;
  private final int duration = 120 * 20;

  public PlayerDrinkXRayPotion(Plugin plugin) {
    this.plugin = plugin;
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
      // ArrayList<Block> blocksToSee = new ArrayList<Block>();

      theseShulkers = new ArrayList<Entity>();
      for (Block block : allBlocksWithinRadius) {
        if (block.getType() == toSee) {
          // blocksToSee.add(block);
          Entity shulker = block.getWorld().spawnEntity(block.getLocation(), EntityType.SHULKER);
          NBTEntity shulkerNbt = new NBTEntity(shulker);
          shulkerNbt.setString("ExclusiveToPlayer", playerId.toString());
          shulkerNbt.setBoolean("NoAI", true);
          shulkerNbt.setBoolean("Invulnerable", true);
          glow.apply((LivingEntity) shulker);
          invisible.apply((LivingEntity) shulker);
          theseShulkers.add(shulker);
        }
      }
      shulkers.put(playerId, theseShulkers);
    }
  }
    private ArrayList<Block> getBlocksAtRadius(Location location, double radius) {
    World world = location.getWorld();
    // location = location.toCenterLocation();
    ArrayList<Block> blocksAtRadius = new ArrayList<Block>();
    double startingX = location.getX();
    double startingY = location.getY();
    double startingZ = location.getZ();
    for (double x = startingX - radius; x <= startingX + radius; x++) {
      for (double y = startingY - radius; y <= startingY + radius; y++) {
        for (double z = startingZ - radius; z <= startingZ + radius; z++) {
          Location locationAtRadius = new Location(world, x, y, z);
          // double distance = locationAtRadius.distance(location);
          Block b = world.getBlockAt(locationAtRadius);
          // if (distance <= radius) {
          blocksAtRadius.add(b);
          // }
        }
      }
    }
    return blocksAtRadius;
  }

    @Override
    public Plugin getPlugin() {
      return this.plugin;
    }

    @Override
    public ListeningWhitelist getReceivingWhitelist() {
      return ListeningWhitelist.EMPTY_WHITELIST;
    }

    @Override
    public ListeningWhitelist getSendingWhitelist() {
      return ListeningWhitelist.newBuilder()
        .priority(ListenerPriority.NORMAL)
        .types(PacketType.Play.Server.SPAWN_ENTITY)
        .build();
    }

    @Override
    public void onPacketReceiving(PacketEvent arg0) { }

    @Override
    public void onPacketSending(PacketEvent event) {
      Player player = event.getPlayer();
      player.sendMessage("Spawn entity");
      
      if (event.getPacketType() != PacketType.Play.Server.SPAWN_ENTITY) {
        return;
      }

      PacketContainer packet = event.getPacket();
      int id = packet.getIntegers().read(0);

    }

}
