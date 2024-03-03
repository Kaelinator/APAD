package com.kaelkirk.events;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import com.kaelkirk.SpigotPlugin;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

public class PlayerUseTelekinesis implements Listener, Runnable {

  private HashMap<UUID, Integer> playerSneakTime;

  public PlayerUseTelekinesis(Plugin plugin) {
    playerSneakTime = new HashMap<UUID, Integer>();
    Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 1);
  }

  @EventHandler
  public void onPlayerUseTelekinesis(BlockBrokenByTelekinesisEvent event) {
    LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(event.getPlayer());
    Block block = event.getBrokenBlock();
    World world = BukkitAdapter.adapt(event.getWorld());
    com.sk89q.worldedit.util.Location loc = new com.sk89q.worldedit.util.Location(world, block.getX(), block.getY(), block.getZ());
    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    RegionQuery query = container.createQuery();

    boolean canDoIt = query.testState(loc, localPlayer, SpigotPlugin.TELEKINESIS);

    if (canDoIt) {
      return;
    }

    event.setCancelled(true);
  }
  
  @EventHandler
  public void onPlayerAttemptBlockBreak(PlayerInteractEvent event) {
    Player player = event.getPlayer();

    if (event.getAction() != Action.LEFT_CLICK_AIR) {
      return;
    }

    if (!this.playerSneakTime.containsKey(player.getUniqueId())) {
      return;
    }

    RayTraceResult rayTrace = player.rayTraceBlocks(64);

    if (rayTrace == null) {
      return;
    }

    Block block = rayTrace.getHitBlock();

    if (!playerCanBreak(player, rayTrace.getHitBlock())) {
      return;
    }

    BlockBrokenByTelekinesisEvent resultingEvent = new BlockBrokenByTelekinesisEvent(
      player,
      block
    );
    Bukkit.getPluginManager().callEvent(resultingEvent);

    if (resultingEvent.isCancelled()) {
      return;
    }

    ItemStack itemToBreakBlock = player.getInventory().getItemInMainHand();
   
    if (itemToBreakBlock.getItemMeta() instanceof Damageable) {
      itemToBreakBlock.damage(1, player);
    }

    block.breakNaturally();
    this.playerSneakTime.put(player.getUniqueId(), 0);
  }

  @EventHandler
  public void onPlayerInitiateTelekinesisEvent(PlayerToggleSneakEvent event) {
    Player player = event.getPlayer();

    if(event.isSneaking() && !player.isInsideVehicle()) {
      playerSneakTime.put(player.getUniqueId(), 0);
    } else {
      if (playerSneakTime.containsKey(player.getUniqueId())) {
        playerSneakTime.remove(player.getUniqueId());
      }
    }
  }

  @Override
  public void run() {
    for (UUID playerId : playerSneakTime.keySet()) {
      Player player = Bukkit.getPlayer(playerId);

      ItemStack helmet = player.getEquipment().getHelmet();
      if (helmet == null || !helmet.getType().equals(Material.CHAINMAIL_HELMET)) {
        playerSneakTime.put(playerId, 0);
        continue;
      }

      int time = playerSneakTime.get(playerId) + 1;
      playerSneakTime.put(playerId, time);
      drawParticles(player, time);
    }
  }

  private void drawParticles(Player player, int sneakTime) {

    RayTraceResult rayTrace = player.rayTraceBlocks(64);

    if (rayTrace == null) {
      return;
    }

    if (!playerCanBreak(player, rayTrace.getHitBlock())) {
      return;
    }

    Vector from = player.getEyeLocation().toVector();
    Vector to = rayTrace.getHitPosition();
    Vector unitLine = to.subtract(from).normalize();
    double distance = rayTrace.getHitBlock().getLocation().toVector().distance(from);

    Vector line = from.clone();
    for (double r = 0; r < distance; r += 1.0) {
      line = line.add(unitLine);
      player.getWorld().spawnParticle(Particle.END_ROD, new Location(player.getWorld(), line.getX(), line.getY(), line.getZ()), 1, 0, 0, 0, 0);
    }
  }

  private boolean playerCanBreak(Player player, Block block) {

    float breakTime = block.getDestroySpeed(player.getInventory().getItemInMainHand(), true);
    if (!block.isValidTool(player.getInventory().getItemInMainHand())) {
      return false;
    }
    double distance = block.getLocation().distance(player.getLocation());

    int sneakTime = this.playerSneakTime.get(player.getUniqueId());
    return sneakTime > distance * 40.0 / breakTime;
  }
}
