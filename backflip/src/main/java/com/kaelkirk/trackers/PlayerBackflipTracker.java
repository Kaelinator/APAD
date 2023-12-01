package com.kaelkirk.trackers;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R2.CraftServer;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
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

import com.mojang.authlib.GameProfile;

import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

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
  private ServerPlayer serverPlayer;
  private float pitch;

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
      for (Player p : player.getWorld().getPlayers()) {
        ServerGamePacketListenerImpl connection = ((CraftPlayer) p).getHandle().connection;
        connection.send(new ClientboundRemoveEntitiesPacket(serverPlayer.getId()));
      }
      return;
    }

    l = l.add(initialVelocity);
    l.setPitch(l.getPitch() - 30f);
    ((CraftEntity) armorStand).getHandle().moveTo(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
    player.setSpectatorTarget(armorStand);
    Player fakePlayer = (Player) serverPlayer.getBukkitEntity();
    fakePlayer.setGliding(true);
    // serverPlayer.moveTo(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
    
    // serverPlayer.
    List<SynchedEntityData.DataValue<?>> data = serverPlayer.getEntityData().packDirty();
    for (Player p : player.getWorld().getPlayers()) {
      ServerGamePacketListenerImpl connection = ((CraftPlayer) p).getHandle().connection;
      if (((int) pitch / 10 + 1) % 3 == 0) {
        connection.send(new ClientboundMoveEntityPacket.PosRot(
          serverPlayer.getId(), (short) 0, (short) 0, (short) 0, (byte) 0, (byte) ((int) 120), true));
      }
      if (data == null) {
        continue;
      }
      connection.send(new ClientboundSetEntityDataPacket(serverPlayer.getId(), data));
    }
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
    spawnPlayer(world);

  }

  private void spawnPlayer(World world) {
    MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
    ServerLevel serverLevel = ((CraftWorld) location.getWorld()).getHandle();
    UUID uuid = UUID.randomUUID();
    GameProfile gameProfile = new GameProfile(uuid, player.getName());
    serverPlayer = new ServerPlayer(minecraftServer, serverLevel, gameProfile, ClientInformation.createDefault());
    setValueUsingReflection(serverPlayer, "c", ((CraftPlayer) player).getHandle().connection);
    serverPlayer.moveTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    pitch = location.getPitch();
    for (Player p : world.getPlayers()) {
      ServerGamePacketListenerImpl connection = ((CraftPlayer) p).getHandle().connection;
      connection.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, serverPlayer));
      connection.send(serverPlayer.getAddEntityPacket());
    }

  }

  public void setValueUsingReflection(Object obj, String fieldName, Object value) {
    try {
      Field field = obj.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(obj, value);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  
}
