package com.kaelkirk.trackers;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R2.CraftServer;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Pair;

import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

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

  public PlayerBackflipTracker(Player player) {
    this.player = player;
    taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 1);
    Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    currentlyBackflipping = false;
  }

  public static void register(Plugin plugin) {
    PlayerBackflipTracker.plugin = plugin;
  }

  public void stop() {
    Bukkit.getScheduler().cancelTask(taskId);
    PlayerToggleSneakEvent.getHandlerList().unregister(this);

    if (!currentlyBackflipping) {
      return;
    }

    player.setGameMode(player.getPreviousGameMode());
    player.teleport(location);
    player.setVelocity(initialVelocity);

    if (armorStand != null) {
      armorStand.remove();
    }

    if (serverPlayer != null) {
      for (Player p : player.getWorld().getPlayers()) {
        ServerGamePacketListenerImpl connection = ((CraftPlayer) p).getHandle().connection;
        connection.send(new ClientboundRemoveEntitiesPacket(serverPlayer.getId()));
      }
    }
  }

  @Override
  public void run() {
    /* this will run 20x a second (every tick) */

    yVelocity = player.getVelocity().getY();
    Entity playerEntity = (Entity) player;

    if (playerEntity.isOnGround()) {
      stop();
    }

    if (!currentlyBackflipping) {
      return;
    }

    Location l = armorStand.getLocation();

    if (Math.abs(l.getPitch() - 360) < 20) {
      stop();
      return;
    }

    l = l.add(initialVelocity);
    l.setPitch(l.getPitch() + 30f);

    Player fakePlayer = (Player) serverPlayer.getBukkitEntity();
    fakePlayer.setGliding(true);
    ((CraftEntity) armorStand).getHandle().moveTo(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
    float clampedPitch = Math.min(l.getPitch(), 127);
    float yOffset = (float) Math.sin(Math.toRadians((clampedPitch + 90) / 2)) * 2;
    player.setSpectatorTarget(armorStand);

    serverPlayer.moveTo(l.getX(), l.getY() + yOffset, l.getZ(), l.getYaw(), clampedPitch);
    for (Player p : player.getWorld().getPlayers()) {
      ServerGamePacketListenerImpl connection = ((CraftPlayer) p).getHandle().connection;
      connection.send(new ClientboundTeleportEntityPacket(serverPlayer));
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
    player.setGameMode(GameMode.SPECTATOR);
    spawnPlayer(world);

  }

  private void spawnPlayer(World world) {
    MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
    ServerLevel serverLevel = ((CraftWorld) location.getWorld()).getHandle();
    UUID uuid = UUID.randomUUID();
    GameProfile gameProfile = new GameProfile(uuid, player.getName());
    setSkin(player.getName(), gameProfile);
    serverPlayer = new ServerPlayer(minecraftServer, serverLevel, gameProfile, ClientInformation.createDefault());
    setValueUsingReflection(serverPlayer, "c", ((CraftPlayer) player).getHandle().connection);
    Player fakePlayer = (Player) serverPlayer.getBukkitEntity();
    fakePlayer.setGliding(true);

    EntityEquipment equipment = player.getEquipment();
    serverPlayer.moveTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    List<SynchedEntityData.DataValue<?>> data = serverPlayer.getEntityData().packDirty();
    for (Player p : world.getPlayers()) {
      ServerGamePacketListenerImpl connection = ((CraftPlayer) p).getHandle().connection;
      connection.send(
          new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, serverPlayer));
      connection.send(serverPlayer.getAddEntityPacket());
      if (data == null) {
        continue;
      }
      connection.send(new ClientboundSetEntityDataPacket(serverPlayer.getId(), data));
      connection.send(new ClientboundMoveEntityPacket.PosRot(
          serverPlayer.getId(), (short) 0, (short) 0, (short) 0, (byte) ((location.getYaw()) * 255F / 360F), (byte) 127,
          false));
      
      List<Pair<EquipmentSlot, ItemStack>> newEquipment = List.of(
        new Pair<EquipmentSlot, ItemStack>(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(equipment.getHelmet())),
        new Pair<EquipmentSlot, ItemStack>(EquipmentSlot.CHEST, CraftItemStack.asNMSCopy(equipment.getChestplate())),
        new Pair<EquipmentSlot, ItemStack>(EquipmentSlot.LEGS, CraftItemStack.asNMSCopy(equipment.getLeggings())),
        new Pair<EquipmentSlot, ItemStack>(EquipmentSlot.FEET, CraftItemStack.asNMSCopy(equipment.getBoots())),
        new Pair<EquipmentSlot, ItemStack>(EquipmentSlot.MAINHAND, CraftItemStack.asNMSCopy(equipment.getItemInMainHand())),
        new Pair<EquipmentSlot, ItemStack>(EquipmentSlot.OFFHAND, CraftItemStack.asNMSCopy(equipment.getItemInOffHand()))
      );
      connection.send(new ClientboundSetEquipmentPacket(serverPlayer.getId(), newEquipment));
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

  public void setSkin(String name, GameProfile gameProfile) {
    Gson gson = new Gson();
    String url = "https://api.mojang.com/users/profiles/minecraft/" + name;
    String json = getStringFromURL(url);
    String uuid = gson.fromJson(json, JsonObject.class).get("id").getAsString();

    url = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false";
    json = getStringFromURL(url);
    JsonObject mainObject = gson.fromJson(json, JsonObject.class);
    JsonObject jsonObject = mainObject.get("properties").getAsJsonArray().get(0).getAsJsonObject();
    String value = jsonObject.get("value").getAsString();
    String signature = jsonObject.get("signature").getAsString();
    PropertyMap propertyMap = gameProfile.getProperties();
    propertyMap.put("name", new Property("name", name));
    propertyMap.put("textures", new Property("textures", value, signature));
  }

  public void changeSkin(String value, String signature, GameProfile gameProfile) {
    gameProfile.getProperties().put("textures", new Property("textures", value, signature));
  }

  private String getStringFromURL(String url) {
    StringBuilder text = new StringBuilder();
    try {
      Scanner scanner = new Scanner(new URL(url).openStream());
      while (scanner.hasNext()) {
        String line = scanner.nextLine();
        while (line.startsWith(" ")) {
          line = line.substring(1);
        }
        text.append(line);
      }
      scanner.close();
    } catch (IOException exception) {
      exception.printStackTrace();
    }
    return text.toString();
  }

}
