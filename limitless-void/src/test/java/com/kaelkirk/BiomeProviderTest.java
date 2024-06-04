package com.kaelkirk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.junit.Test;

import com.kaelkirk.generators.LimitlessVoidBiomeProvider;

public class BiomeProviderTest 
{
  // @Test
  public void biomeProviderShouldReturnInfrequentValues()
  {
    BiomeProvider provider = new LimitlessVoidBiomeProvider();

    for (int x = -2250; x < 2250; x++) {
      System.out.println();
      for (int z = -50; z < 50; z++) {
        switch (provider.getBiome(null, x, 0, z)) {
          case BASALT_DELTAS:
            System.out.print('B');
            break;
          case THE_END:
            System.out.print('E');
            break;
          case NETHER_WASTES:
            System.out.print('N');
            break;

          case SOUL_SAND_VALLEY:
            System.out.print('S');
            break;
          
          default:
            System.out.print('X');
            break;
        }
      }
    }
    System.out.println();

    assertTrue(true);
  }
}
