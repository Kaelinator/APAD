package com.kaelkirk.registry;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class WarpRegistry {
  
  private NamespacedKey warpKey;

  public WarpRegistry(NamespacedKey warpKey) {
    this.warpKey = warpKey;
  }

  /**
   * 
   * @param name
   * @param location
   * @return whether warp was successfully added
   */
  public boolean addWarp(String name, Location location) {

    World world = location.getWorld();
    HashMap<String, Location> warps = readWarps(world);

    if (warps == null) {
      return false;
    }

    warps.put(name, location);

    return writeWarps(world, warps);
  }

  public void removeWarp(World world, String name) {
    HashMap<String, Location> warps = readWarps(world);

    warps.remove(name);

    writeWarps(world, warps);
  }

  public boolean hasWarp(World world, String name) {
    HashMap<String, Location> warps = readWarps(world);
    return warps.containsKey(name);
  }

  public Location getWarpLocation(World world, String name) {
    HashMap<String, Location> warps = readWarps(world);
    return warps.get(name);
  }

  public HashMap<String, Location> readWarps(World world) {
    PersistentDataContainer container = world.getPersistentDataContainer();
    if (!container.has(warpKey)) {
      return new HashMap<String, Location>();
    }

    byte[] warpBuffer = container.get(warpKey, PersistentDataType.BYTE_ARRAY);

    try {
      ByteArrayInputStream stream = new ByteArrayInputStream(warpBuffer);
      BukkitObjectInputStream data = new BukkitObjectInputStream(stream);
      int warpCount = data.readInt();
      HashMap<String, Location> warps = new HashMap<String, Location>();
      for (int i = 0; i < warpCount; i++) {
        warps.put((String) data.readObject(), (Location) data.readObject());
      }
      data.close();
      return warps;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * 
   * @param world
   * @param warps
   * @return whether warps were sucessfully written
   */
  private boolean writeWarps(World world, HashMap<String, Location> warps) {
    try {
      ByteArrayOutputStream str = new ByteArrayOutputStream();
      BukkitObjectOutputStream data = new BukkitObjectOutputStream(str);
      int warpCount = warps.size();
      data.writeInt(warpCount);
      for (Entry<String, Location> entry : warps.entrySet()) {
        data.writeObject(entry.getKey());
        data.writeObject(entry.getValue());
      }
      data.close();
      PersistentDataContainer container = world.getPersistentDataContainer();
      container.set(warpKey, PersistentDataType.BYTE_ARRAY, str.toByteArray());
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }


}
