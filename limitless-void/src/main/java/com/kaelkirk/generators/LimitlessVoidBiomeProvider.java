package com.kaelkirk.generators;

import java.util.List;

import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.noise.NoiseGenerator;
import org.bukkit.util.noise.PerlinNoiseGenerator;
import org.jetbrains.annotations.NotNull;

public class LimitlessVoidBiomeProvider extends BiomeProvider {

  private static final List<Biome> BIOMES = List.of(
    Biome.THE_END,
    Biome.BASALT_DELTAS,
    Biome.NETHER_WASTES,
    Biome.SOUL_SAND_VALLEY
  );

  private PerlinNoiseGenerator noise;

  public LimitlessVoidBiomeProvider() {
    noise = new PerlinNoiseGenerator(0);
  }

  @Override
  public Biome getBiome(WorldInfo worldInfo, int x, int y, int z) {
    int index = NoiseGenerator.floor(noise.noise((double) x / 128, (double) z / 128, 1, 1, 1, true) * 2 + 2);
    return BIOMES.get(index);
  }

  @Override
  public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
    return BIOMES;
  }

}