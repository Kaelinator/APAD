package com.kaelkirk.generators;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.LootTables;
import org.bukkit.plugin.Plugin;

import com.kaelkirk.loottables.EndlessVoidChestLootTable;

public class TrappedChestPopulator extends BlockPopulator {
    
  private float rate;
  private float stdDeviationInRate;
  private int chestHeight;
  private LootTable lootTable;

  public TrappedChestPopulator(Plugin plugin, float rate, float stdDeviationInRate, int chestHeight, World world) {
    this.rate = rate;
    this.chestHeight = chestHeight;
    this.stdDeviationInRate = stdDeviationInRate;
    lootTable = new EndlessVoidChestLootTable(plugin) ;
  }

  @Override
  public void populate(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, LimitedRegion limitedRegion) {

    try {
      int chestCount = (int) Math.round(random.nextGaussian(rate, stdDeviationInRate));

      for (int i = 0; i < chestCount; i++) {
        Location chestLocation = findChestLocation(random, limitedRegion, 10);

        if (chestLocation == null) {
          return;
        }

        limitedRegion.setType(chestLocation, Material.CHEST);
        Chest chest = (Chest) limitedRegion.getBlockState(chestLocation);
        // chest.setLootTable(lootTable, random.nextLong()); // doesn't work
        chest.setLootTable(LootTables.ANCIENT_CITY.getLootTable(), random.nextLong()); // works
        chest.update();
        limitedRegion.setBlockState(chestLocation.toVector(), chest);
      }

    } catch (Exception e) {
      System.err.println("Error in TrappedChestPopulator\n" + e.getMessage());
      e.printStackTrace();
    }
  }

  private Location findChestLocation(Random random, LimitedRegion limitedRegion, int take) {
    if (take < 0) {
      return null;
    }
    int buffer = limitedRegion.getBuffer();
    int size = buffer * 2;
    int minCornerX = limitedRegion.getCenterBlockX() - buffer;
    int minCornerZ = limitedRegion.getCenterBlockZ() - buffer;
    
    int x = random.nextInt(size) + minCornerX;
    int z = random.nextInt(size) + minCornerZ;

    Material chestLocationMaterial = limitedRegion.getBlockData(x, chestHeight, z).getMaterial();
    Material dispenserLocationMaterial = limitedRegion.getBlockData(x, chestHeight - 1, z).getMaterial();
    if (chestLocationMaterial != Material.AIR
      || dispenserLocationMaterial == Material.AIR
      || !limitedRegion.isInRegion(x, chestHeight, z)) {
      return findChestLocation(random, limitedRegion, take - 1);
    }

    return new Location(limitedRegion.getWorld(), x, chestHeight, z);
  }

}
