package com.kaelkirk.event;

import java.util.stream.Stream;

import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;

import com.kaelkirk.TreeGenerator;

public class MegaOakGrowEvent implements Listener {

  private TreeGenerator generator;

  public MegaOakGrowEvent(TreeGenerator generator) {
    this.generator = generator;
  }

  @EventHandler
  public void onMegaOakGrowEvent(StructureGrowEvent event) {
    if (event.getSpecies() != TreeType.TREE && event.getSpecies() != TreeType.BIG_TREE) {
      return;
    }

    Location treeCenter = event.getLocation();
    
    Block centerBlock = treeCenter.getBlock();
    Boolean[] isSapling = Stream.of(
      // counterclockwise
      // 0 7 6
      // 1   5
      // 2 3 4
      centerBlock.getRelative(1, 0, 1),
      centerBlock.getRelative(1, 0, 0),
      centerBlock.getRelative(1, 0, -1),
      centerBlock.getRelative(0, 0, -1),
      centerBlock.getRelative(-1, 0, -1),
      centerBlock.getRelative(-1, 0, 0),
      centerBlock.getRelative(-1, 0, 1),
      centerBlock.getRelative(0, 0, 1)
    )
      .map(b -> b.getType() == centerBlock.getType())
      .toArray(Boolean[]::new);
    
    // x x 0
    // x x 0
    // 0 0 0
    if (isSapling[0] && isSapling[1] && isSapling[7]) {
      event.setCancelled(true);
      System.out.println("0");
      generator.generateTree(treeCenter.clone());
      return;
    }
    
    // 0 0 0
    // x x 0
    // x x 0
    if (isSapling[1] && isSapling[2] && isSapling[3]) {
      event.setCancelled(true);
      System.out.println("1");
      generator.generateTree(treeCenter.clone().add(0, 0, -1));
      return;
    }

    // 0 0 0
    // 0 x x
    // 0 x x
    if (isSapling[3] && isSapling[4] && isSapling[5]) {
      event.setCancelled(true);
      System.out.println("2");
      generator.generateTree(treeCenter.clone().add(-1, 0, -1));
      return;
    }

    // 0 x x
    // 0 x x
    // 0 0 0
    if (isSapling[5] && isSapling[6] && isSapling[7]) {
      event.setCancelled(true);
      System.out.println("3");
      generator.generateTree(treeCenter.clone().add(-1, 0, 0));
      return;
    }
  }
  
}
