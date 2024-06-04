package com.kaelkirk.generators;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

public class LimitlessVoidGenerator extends ChunkGenerator {

  private final int WATER_LEVEL = 0;

  @Override
  public void generateSurface(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
    generateCrust(chunkData, WATER_LEVEL);

    generateMaze(chunkData, random, WATER_LEVEL, WATER_LEVEL + 5);
    generateCeiling(chunkData, random, WATER_LEVEL + 128, 2, 4);
  }

  private void generateCeiling(ChunkData chunkData, Random random, int ceiling, int droopMin, int droopScale) {
    for (int x = 0; x < 16; x++) {
      for (int z = 0; z < 16; z++) {
        if (chunkData.getBiome(x, 0, z) != Biome.THE_END) {
          chunkData.setRegion(x, ceiling - (int) (Math.abs(random.nextGaussian()) * droopScale + droopMin), z,
            x+1, ceiling + 1, z + 1,
            Material.BLACKSTONE);
          chunkData.setBlock(x, ceiling + 1, z, Material.BEDROCK);
        }
      }
    }
  }

  private void generateMaze(ChunkData chunkData, Random random, int minY, int maxY) {
    // grid of 16 nodes
    // each node every four blocks
    // randomize whether to add a wall to the right or beneath the node
    // also randomize whether to add a chasm
    for (int x = 0; x < 16; x += 4) {
      for (int z = 0; z < 16; z += 4) {
        boolean rightWall = random.nextBoolean();
        boolean bottomWall = random.nextBoolean();
        boolean chasm = random.nextFloat() < 1f / 1024f;

        if (rightWall) {
          chunkData.setRegion(x, minY, z, x + 5, maxY, z + 1, Material.COBBLED_DEEPSLATE);
        }

        if (bottomWall) {
          chunkData.setRegion(x, minY, z, x + 1, maxY, z + 5, Material.COBBLED_DEEPSLATE);
        }

        if (chasm) {
          chunkData.setRegion(x + 1, -64, z + 1, x + 5, minY, z + 4, Material.AIR);
        }
      }
    }
  }

  private void generateCrust(ChunkData chunkData, int maxY) {
    chunkData.setRegion(0, chunkData.getMinHeight(), 0, 16, maxY, 16, Material.DEEPSLATE);
    chunkData.setRegion(0, chunkData.getMinHeight(), 0, 16, chunkData.getMinHeight() + 1, 16, Material.BEDROCK);
  }


  
}
