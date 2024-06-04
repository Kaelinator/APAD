package com.kaelkirk.generators;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Directional;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;

public class CrystalPopulator extends BlockPopulator {

  private int minCrystalHeight;
  private int maxCrystalHeight;
  private float rate;
  private float sizeFactor;

  public CrystalPopulator(int minCrystalHeight, int maxCrystalHeight, float rate, float sizeFactor) {
    this.minCrystalHeight = minCrystalHeight;
    this.maxCrystalHeight = maxCrystalHeight;
    this.rate = rate;
    this.sizeFactor = sizeFactor;
  }
  
  @Override
  public void populate(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, LimitedRegion limitedRegion) {
    if (random.nextFloat() > rate) {
      return;
    }

    int centerX = limitedRegion.getCenterBlockX();
    int centerY = random.nextInt(minCrystalHeight, maxCrystalHeight);
    int centerZ = limitedRegion.getCenterBlockZ();

    int size = (int) Math.ceil(Math.abs(random.nextGaussian()) * sizeFactor);


    /* generate crystal */
    int minX = centerX - size / 2 - 1;
    int maxX = centerX + size / 2 + (size % 2 == 0 ? 0 : 1);
    int minZ = centerZ - size / 2 - 1;
    int maxZ = centerZ + size / 2 + (size % 2 == 0 ? 0 : 1);

    // platform
    for (int x = minX; x <= maxX; x++) {
      for (int z = minZ; z <= maxZ; z++) {

        if (x == minX || x == maxX || z == minZ || z == maxZ) {
          // edges
          limitedRegion.setType(x, centerY, z, Material.BLACKSTONE);
        } else {
          limitedRegion.setType(x, centerY, z, Material.PEARLESCENT_FROGLIGHT);
        }
      }
    }

    // stalactites
    for (int x = minX; x <= maxX; x++) {
      for (int z = minZ; z <= maxZ; z++) {
        int distanceFromEdge = Math.min(
          Math.min(x - minX, maxX - x),
          Math.min(z - minZ, maxZ - z)
        );

        int droopY = centerY - (int) Math.ceil(random.nextGaussian() + (distanceFromEdge + 1) * 2);

        if (droopY >= centerY) {
          continue;
        }

        int thinY = random.nextInt(droopY - 1, centerY - 1); // change to walls
        for (int y = centerY - 1; y >= droopY; y--) {
          if (y > thinY) {
            limitedRegion.setType(x, y, z, Material.BLACKSTONE);
          } else {
            limitedRegion.setType(x, y, z, Material.BLACKSTONE_WALL);
          }
        }
      }
    }

    // crystal
    for (int x = minX + 1; x < maxX; x++) {
      for (int z = minZ + 1; z < maxZ; z++) {

        int distanceFromEdge = Math.min(
          Math.min(x - minX, maxX - x),
          Math.min(z - minZ, maxZ - z)
        );
        int height = centerY + distanceFromEdge;

        if (size == 1) {
          limitedRegion.setType(x, height, z, Material.AMETHYST_CLUSTER);
          continue;
        }

        if ((x + z - minX - minZ) % 2 == 1) {
          for (int y = centerY + 1; y <= height; y++) {
            limitedRegion.setType(x, y, z, Material.AMETHYST_CLUSTER);
            BlockState state = limitedRegion.getBlockState(x, y, z);
            Directional facing = (Directional) state.getBlockData();
            facing.setFacing((x >= centerX) ? BlockFace.EAST : BlockFace.WEST);
            limitedRegion.setBlockData(x, y, z, facing);
          }
        } else {
          for (int y = centerY + 1; y <= height; y++) {
            limitedRegion.setType(x, y, z, Material.BUDDING_AMETHYST);
          }
          limitedRegion.setType(x, height + 1, z, Material.AMETHYST_CLUSTER);
        }

      }
    }
  }
}
