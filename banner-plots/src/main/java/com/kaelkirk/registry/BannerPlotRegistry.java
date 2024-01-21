package com.kaelkirk.registry;

import static java.util.Map.entry;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Banner;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import com.kaelkirk.BannerPlotPlugin;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion.CircularInheritanceException;
import com.sk89q.worldguard.protection.regions.RegionContainer;

public class BannerPlotRegistry {
  
  private NamespacedKey bannerPlotKey;
  private RegionContainer regionContainer;

  public BannerPlotRegistry(NamespacedKey bannerPlotKey) {
    this.bannerPlotKey = bannerPlotKey;

    regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
  }

  public boolean isChunkClaimed(Chunk chunk) {
    HashMap<Long, Pair<Location, UUID>> bannerPlots = readBannerPlots(chunk.getWorld());
    if (bannerPlots == null) {
      return false;
    }
    return bannerPlots.containsKey(chunk.getChunkKey());
  }

  public Location getBannerLocation(Chunk chunk) {
    HashMap<Long, Pair<Location, UUID>> bannerPlots = readBannerPlots(chunk.getWorld());
    if (bannerPlots == null || !bannerPlots.containsKey(chunk.getChunkKey())) {
      return null;
    }
    return bannerPlots.get(chunk.getChunkKey()).getLeft();
  }

  public UUID getBannerClaimedUUID(Chunk chunk) {
    HashMap<Long, Pair<Location, UUID>> bannerPlots = readBannerPlots(chunk.getWorld());
    if (bannerPlots == null || !bannerPlots.containsKey(chunk.getChunkKey())) {
      return null;
    }
    return bannerPlots.get(chunk.getChunkKey()).getRight();
  }

  public boolean claimChunk(Chunk chunk, Banner banner, UUID claimedBy) {
    HashMap<Long, Pair<Location, UUID>> bannerPlots = readBannerPlots(chunk.getWorld());

    if (bannerPlots == null) {
      return false;
    }

    bannerPlots.put(chunk.getChunkKey(), new ImmutablePair<Location, UUID>(banner.getLocation(), claimedBy));
    boolean successfullyWritten = writeBannerPlots(chunk.getWorld(), bannerPlots);

    ProtectedRegion parentRegion = initBannerPlotRegion(BukkitAdapter.adapt(chunk.getWorld()));

    String regionId = claimedBy + "/" + chunk.getChunkKey();

    BlockVector3 min = BlockVector3.at(chunk.getX() * 16, -64, chunk.getZ() * 16);
    BlockVector3 max = BlockVector3.at(chunk.getX() * 16 + 15, 320, chunk.getZ() * 16 + 15);
    ProtectedRegion region = new ProtectedCuboidRegion(regionId, min, max);

    DefaultDomain members = region.getMembers();
    members.addPlayer(claimedBy);
    region.setMembers(members);

    RegionManager regionManager = regionContainer.get(BukkitAdapter.adapt(chunk.getWorld()));
    regionManager.addRegion(region);

    try {
      region.setParent(parentRegion);
    } catch (CircularInheritanceException e) {
      e.printStackTrace();
      return false;
    }

    return successfullyWritten;
  }

  public boolean unclaimCunk(Chunk chunk) {

    HashMap<Long, Pair<Location, UUID>> bannerPlots = readBannerPlots(chunk.getWorld());
    if (bannerPlots == null) {
      return false;
    }

    long chunkKey = chunk.getChunkKey();

    if (!bannerPlots.containsKey(chunkKey)) {
      return false;
    }

    Pair<Location, UUID> removedPlot = bannerPlots.remove(chunkKey);
    World world = BukkitAdapter.adapt(chunk.getWorld());

    RegionManager regions = regionContainer.get(world);

    if (regions == null) {
      System.err.println("UNABLE TO REMOVE REGION: Failed to load regions for " + world.getName());
      return false;
    }

    String regionId = removedPlot.getRight() + "/" + chunkKey;
    Set<ProtectedRegion> removedRegions = regions.removeRegion(regionId);

    // verify region has been removed
    boolean removed = false;
    for (ProtectedRegion removedRegion : removedRegions) {
      if (removedRegion.getId().equals(regionId)) {
        removed = true;
      }
    }

    if (!removed) {
      System.err.println("UNABLE TO REMOVE REGION " + regionId);
    }

    return writeBannerPlots(chunk.getWorld(), bannerPlots);
  }

  @SuppressWarnings("unchecked")
  public HashMap<Long, Pair<Location, UUID>> readBannerPlots(org.bukkit.World world) {
    PersistentDataContainer container = world.getPersistentDataContainer();
    if (!container.has(bannerPlotKey)) {
      return new HashMap<Long, Pair<Location, UUID>>();
    }

    byte[] warpBuffer = container.get(bannerPlotKey, PersistentDataType.BYTE_ARRAY);

    try {
      ByteArrayInputStream stream = new ByteArrayInputStream(warpBuffer);
      BukkitObjectInputStream data = new BukkitObjectInputStream(stream);
      int bannerPlotCount = data.readInt();
      HashMap<Long, Pair<Location, UUID>> bannerPlots = new HashMap<Long, Pair<Location, UUID>>();
      for (int i = 0; i < bannerPlotCount; i++) {
        bannerPlots.put((Long) data.readObject(), (Pair<Location, UUID>) data.readObject());
      }
      data.close();
      return bannerPlots;
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
  private boolean writeBannerPlots(org.bukkit.World world, HashMap<Long, Pair<Location, UUID>> bannerPlots) {
    try {
      ByteArrayOutputStream str = new ByteArrayOutputStream();
      BukkitObjectOutputStream data = new BukkitObjectOutputStream(str);
      int bannerPlotsCount = bannerPlots.size();
      data.writeInt(bannerPlotsCount);
      for (Entry<Long, Pair<Location, UUID>> entry : bannerPlots.entrySet()) {
        data.writeObject(entry.getKey());
        data.writeObject(entry.getValue());
      }
      data.close();
      PersistentDataContainer container = world.getPersistentDataContainer();
      container.set(bannerPlotKey, PersistentDataType.BYTE_ARRAY, str.toByteArray());
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  private ProtectedRegion initBannerPlotRegion(World world) {
    RegionManager regions = regionContainer.get(world);

    if (regions == null) {
      System.err.println("Failed to load regions for " + world.getName());
      return null;
    }

    ProtectedRegion bannerPlotRegion = regions.getRegion(BannerPlotPlugin.BANNER_PLOT_REGION_ID);
    if (bannerPlotRegion != null) {
      return bannerPlotRegion;
    }

    bannerPlotRegion = new GlobalProtectedRegion(BannerPlotPlugin.BANNER_PLOT_REGION_ID);
    bannerPlotRegion.setFlags(Map.ofEntries(
      entry(Flags.BLOCK_BREAK.getRegionGroupFlag(), RegionGroup.MEMBERS),
      entry(Flags.BLOCK_PLACE.getRegionGroupFlag(), RegionGroup.MEMBERS),
      entry(Flags.PVP.getRegionGroupFlag(), RegionGroup.MEMBERS),
      entry(Flags.USE, StateFlag.State.ALLOW)
    ));

    regions.addRegion(bannerPlotRegion);
    return bannerPlotRegion;
  }
}
