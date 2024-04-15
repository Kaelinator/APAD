package com.kaelkirk;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.entity.Entity;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.structure.Structure;
import org.bukkit.structure.StructureManager;
import org.bukkit.util.BlockTransformer;
import org.bukkit.util.BlockVector;
import org.bukkit.util.EntityTransformer;
import org.jetbrains.annotations.NotNull;

public class TreeGenerator {

  private final int LEAF_HEIGHT = 7;
  private final int MAX_EFFECTIVE_HEIGHT = 32;
  private final int MAX_GROW_ATTEMPTS = 10;

  private Structure trunk;
  private Structure[] lowLimbs;
  private Structure[] midLimbs;
  private Structure[] highLimbs;
  private List<BlockTransformer> limbTransformer = 
      List.of(
        new BlockTransformer() {

          @Override
          public BlockState transform(LimitedRegion region,
            int x, int y, int z,
            BlockState current, TransformationState state) {

              BlockState originalState = state.getWorld();
              if (current.getType() == Material.AIR) {
                return originalState;
              }

              switch (originalState.getType()) {
                case AIR:
                case OAK_LEAVES:
                  return current;
                default:
                  return originalState;
              }

          }
        }
      );
  private List<EntityTransformer> noEntitiesTransformer =
        List.of(
          new EntityTransformer() {

            @Override
            public boolean transform(@NotNull LimitedRegion region,
              int x, int y, int z,
              @NotNull Entity arg4,
              boolean allowedToSpawn) {
              return false;
            };
          }
        );

  public TreeGenerator() throws IOException {
    StructureManager manager = Bukkit.getStructureManager();
    
    trunk = manager.loadStructure(getClass().getResourceAsStream("/trunk.nbt"));
    lowLimbs = new Structure[] {
      manager.loadStructure(getClass().getResourceAsStream("/limb_low_0.nbt")),
      manager.loadStructure(getClass().getResourceAsStream("/limb_low_1.nbt")),
      manager.loadStructure(getClass().getResourceAsStream("/limb_low_2.nbt")),
    };
    midLimbs = new Structure[] {
      manager.loadStructure(getClass().getResourceAsStream("/limb_mid_0.nbt")),
      manager.loadStructure(getClass().getResourceAsStream("/limb_mid_1.nbt")),
      manager.loadStructure(getClass().getResourceAsStream("/limb_mid_2.nbt")),
    };
    highLimbs = new Structure[] {
      manager.loadStructure(getClass().getResourceAsStream("/limb_high_0.nbt")),
      manager.loadStructure(getClass().getResourceAsStream("/limb_high_1.nbt")),
      manager.loadStructure(getClass().getResourceAsStream("/limb_high_2.nbt")),
    };
  }

  public void generateTree(Location location) {
    Random random = new Random();

    int maxHeight = Math.min(
      Math.min(
        heightOfShortestOpaqueBlock(location.clone()),
        heightOfShortestOpaqueBlock(location.clone().add(1, 0, 0))
      ),
      Math.min(
        heightOfShortestOpaqueBlock(location.clone().add(0, 0, 1)),
        heightOfShortestOpaqueBlock(location.clone().add(1, 0, 1))
      )
    ) - LEAF_HEIGHT;


    int height = pickHeight(maxHeight, random, MAX_GROW_ATTEMPTS);
    System.out.println("Max height " + maxHeight + ", picked height " + height);
    if (height < 0) {
      // too short for tree :(
      return;
    }

    for (int i = 0; i < height; i++) {
      trunk.place(
        location.clone().add(0, i, 0),
        false, // entities?
        StructureRotation.NONE,
        Mirror.NONE,
        0,
        1f,
        random
      );
    }


    // low limbs
    placeLimb(lowLimbs[random.nextInt(lowLimbs.length)], location, height, random, StructureRotation.NONE, random.nextBoolean());
    placeLimb(lowLimbs[random.nextInt(lowLimbs.length)], location, height, random, StructureRotation.CLOCKWISE_90, random.nextBoolean());
    placeLimb(lowLimbs[random.nextInt(lowLimbs.length)], location, height, random, StructureRotation.CLOCKWISE_180, random.nextBoolean());
    placeLimb(lowLimbs[random.nextInt(lowLimbs.length)], location, height, random, StructureRotation.COUNTERCLOCKWISE_90, random.nextBoolean());

    // mid limbs
    placeLimb(midLimbs[random.nextInt(midLimbs.length)], location, height + 1, random, StructureRotation.NONE, random.nextBoolean());
    placeLimb(midLimbs[random.nextInt(midLimbs.length)], location, height + 1, random, StructureRotation.CLOCKWISE_90, random.nextBoolean());
    placeLimb(midLimbs[random.nextInt(midLimbs.length)], location, height + 1, random, StructureRotation.CLOCKWISE_180, random.nextBoolean());
    placeLimb(midLimbs[random.nextInt(midLimbs.length)], location, height + 1, random, StructureRotation.COUNTERCLOCKWISE_90, random.nextBoolean());

    // high limbs
    placeLimb(highLimbs[random.nextInt(highLimbs.length)], location, height + 2, random, StructureRotation.NONE, random.nextBoolean());
    placeLimb(highLimbs[random.nextInt(highLimbs.length)], location, height + 2, random, StructureRotation.CLOCKWISE_90, random.nextBoolean());
    placeLimb(highLimbs[random.nextInt(highLimbs.length)], location, height + 2, random, StructureRotation.CLOCKWISE_180, random.nextBoolean());
    placeLimb(highLimbs[random.nextInt(highLimbs.length)], location, height + 2, random, StructureRotation.COUNTERCLOCKWISE_90, random.nextBoolean());
  }

  private void placeLimb(Structure limb, Location center, int height, Random random, StructureRotation rotation, boolean mirrored) {
    BlockVector dimensions = limb.getSize();

    int xOffset = 0;
    int zOffset = 0;

    switch (rotation) {
      case NONE:
        if (mirrored) {
          zOffset = dimensions.getBlockX() - 1;
          xOffset = 1;
          rotation = StructureRotation.COUNTERCLOCKWISE_90;
        } else {
          xOffset = -dimensions.getBlockX() + 2;
        }
        break;

      case CLOCKWISE_90:
        if (mirrored) {
          xOffset = -dimensions.getBlockX() + 2;
          zOffset = 1;
          rotation = StructureRotation.NONE;
        } else {
          zOffset = -dimensions.getBlockX() + 2;
          xOffset = 1;
        }
        break;

      case CLOCKWISE_180:
        if (mirrored) {
          zOffset = -dimensions.getBlockX() + 2;
          rotation = StructureRotation.CLOCKWISE_90;
        } else {
          xOffset = dimensions.getBlockX() - 1;
          zOffset = 1;
        }
        break;

      case COUNTERCLOCKWISE_90:
        if (mirrored) {
          xOffset = dimensions.getBlockX() - 1;
          rotation = StructureRotation.CLOCKWISE_180;
        } else {
          zOffset = dimensions.getBlockX() - 1;
        }
        break;
      default:
        break;
    }

    limb.place(
      center.clone().add(xOffset, height, zOffset),
      false, // entities?
      rotation,
      mirrored ? Mirror.LEFT_RIGHT : Mirror.NONE,
      0,
      // random.nextFloat(0.75f, 1),
      1f,
      random,
      limbTransformer,
      noEntitiesTransformer
    );
  }
  
  private int pickHeight(int maxHeight, Random random, int attemptsLeft) {
    if (attemptsLeft < 0) {
      return -1;
    }

    int height = (int) Math.ceil(4 + random.nextGaussian());
    if (height > maxHeight) {
      return pickHeight(maxHeight, random, attemptsLeft - 1);
    }

    return height;
  }

  private int heightOfShortestOpaqueBlock(Location location) {
    World world = location.getWorld();

    int x = location.getBlockX();
    int y = location.getBlockY();
    int z = location.getBlockZ();

    if (location.getBlockY() == world.getHighestBlockYAt(location)) {
      return world.getMaxHeight() - y;
    }

    for (int height = 0; y + height < world.getMaxHeight() && height < MAX_EFFECTIVE_HEIGHT; height++) {
      if (world.getType(x, y + height, z).isSolid()) {
        return height;
      }
    }
    return world.getMaxHeight() - y;
  }
}
