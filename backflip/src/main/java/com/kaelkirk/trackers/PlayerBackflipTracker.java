package com.kaelkirk.trackers;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class PlayerBackflipTracker implements Runnable, Listener {

  private final double BACKFLIP_THRESHOLD = 0.1;
  private double yVelocity;
  private static Plugin plugin;
  private Player player;
  private int taskId;
  private ArmorStand armorStand;
  private boolean currentlyBackflipping;
  private Location location;
  private Vector initialVelocity;

  public PlayerBackflipTracker(Player player) {
    this.player = player;
    taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 1);
    Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    currentlyBackflipping = false;
  }

  public static void register(Plugin plugin) {
    PlayerBackflipTracker.plugin = plugin;
  }

  public void close() {
    Bukkit.getScheduler().cancelTask(taskId);
    PlayerToggleSneakEvent.getHandlerList().unregister(this);
  }

  @Override
  public void run() {
    /* this will run 20x a second (every tick) */

    yVelocity = player.getVelocity().getY();
    Entity playerEntity = (Entity) player;

    if (playerEntity.isOnGround()) {
      close();
    }

    if (!currentlyBackflipping) {
      return;
    }

    Location l = armorStand.getLocation();

    if (Math.abs(l.getPitch() + 360) < 20) {
      player.setGameMode(player.getPreviousGameMode());
      player.teleport(location);
      close();
      armorStand.remove();
      player.setVelocity(initialVelocity);
      return;
    }

    l.add(initialVelocity);
    l.setPitch(l.getPitch() - 30f);
    ((CraftEntity) armorStand).getHandle().b(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
    player.setSpectatorTarget(armorStand);
  }

  @EventHandler
  public void onPlayerCrouch(PlayerToggleSneakEvent event) {

    if (event.getPlayer() != player) {
      return;
    }

    if (!event.isSneaking()) {
      return;
    }
    
    if (yVelocity < 0 || yVelocity > BACKFLIP_THRESHOLD) {
      return;
    }

    if (currentlyBackflipping) {
      return;
    }

    currentlyBackflipping = true;

    World world = player.getWorld();
    location = player.getLocation();
    initialVelocity = player.getVelocity();
    Vector direction = location.getDirection();

    armorStand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
    armorStand.setHeadPose(new EulerAngle(direction.getX(), direction.getY(), direction.getZ()));
    armorStand.setVisible(false);
    armorStand.setInvulnerable(true);
    armorStand.setSilent(true);
    armorStand.setAI(false);
    // player.sendMessage(angle.getX() + " " + angle.getY() + " " + angle.getZ());
    player.setGameMode(GameMode.SPECTATOR);

  }

  
}
