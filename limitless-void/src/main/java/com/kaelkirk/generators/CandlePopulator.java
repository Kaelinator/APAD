package com.kaelkirk.generators;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.data.Lightable;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;

public class CandlePopulator extends BlockPopulator {

  private float rate;
  private float stdDeviationInRate;
  private int candleHeight;

  public CandlePopulator(float rate, float stdDeviationInRate, int candleHeight) {
    this.rate = rate;
    this.candleHeight = candleHeight;
    this.stdDeviationInRate = stdDeviationInRate;
  }

  @Override
  public void populate(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, LimitedRegion limitedRegion) {
    try {
      int candleCount = (int) Math.round(random.nextGaussian(rate, stdDeviationInRate));

      for (int i = 0; i < candleCount; i++) {
        Location candleLocation = findCandleLocation(random, limitedRegion, 10);

        if (candleLocation == null) {
          return;
        }

        limitedRegion.setType(candleLocation, Material.GREEN_CANDLE);
        Lightable candleData = (Lightable) limitedRegion.getBlockData(candleLocation);
        candleData.setLit(true);
        limitedRegion.setBlockData(candleLocation, candleData);

        Location spawnerLocation = candleLocation.clone().add(0, -3, 0);

        // // clear out area for spawner
        for (int x = -1; x <= 1; x++)
          for (int y = 0; y <= 1; y++)
            for (int z = -1; z <= 1; z++)
              limitedRegion.setType(spawnerLocation.clone().add(x, y, z), Material.AIR);

        limitedRegion.setType(spawnerLocation, Material.SPAWNER);
        CreatureSpawner spawnerState = (CreatureSpawner) limitedRegion.getBlockState(spawnerLocation);
        spawnerState.setDelay(0);
        spawnerState.setMinSpawnDelay(0);
        spawnerState.setMaxSpawnDelay(1);
        spawnerState.setRequiredPlayerRange(3);
        spawnerState.setSpawnedType(EntityType.TNT);
        spawnerState.setSpawnCount(50);
        spawnerState.update();
        limitedRegion.setBlockState(spawnerLocation.toVector(), spawnerState);

      }
    } catch (Exception e) {
      System.err.println("Error in CandlePopulator\n" + e.getMessage());
      e.printStackTrace();
    }
  }

  private Location findCandleLocation(Random random, LimitedRegion limitedRegion, int take) {
    if (take < 0) {
      return null;
    }
    int buffer = limitedRegion.getBuffer();
    int size = buffer * 2;
    int minCornerX = limitedRegion.getCenterBlockX() - buffer;
    int minCornerZ = limitedRegion.getCenterBlockZ() - buffer;
    
    int x = random.nextInt(size) + minCornerX;
    int z = random.nextInt(size) + minCornerZ;

    Material candleLocationMaterial = limitedRegion.getBlockData(x, candleHeight, z).getMaterial();
    Material spawnerLocationMaterial = limitedRegion.getBlockData(x, candleHeight - 2, z).getMaterial();
    if (candleLocationMaterial != Material.AIR
      || spawnerLocationMaterial == Material.AIR
      || !limitedRegion.isInRegion(x + 1, candleHeight, z + 1)
      || !limitedRegion.isInRegion(x - 1, candleHeight, z + 1)
      || !limitedRegion.isInRegion(x + 1, candleHeight, z - 1)
      || !limitedRegion.isInRegion(x - 1, candleHeight, z - 1)) {
      return findCandleLocation(random, limitedRegion, take - 1);
    }

    return new Location(limitedRegion.getWorld(), x, candleHeight, z);
  }

}
