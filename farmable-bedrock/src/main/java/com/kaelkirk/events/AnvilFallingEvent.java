package com.kaelkirk.events;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class AnvilFallingEvent implements Listener {

  private HashMap<UUID, Location> fallingMap;
  private Plugin plugin;
  private int requiredAnvilFallenDistance;

  public AnvilFallingEvent(Plugin plugin, int requiredAnvilFallenDistance) {
    fallingMap = new HashMap<UUID, Location>();
    this.plugin = plugin;
    this.requiredAnvilFallenDistance = requiredAnvilFallenDistance;
  }

  @EventHandler
  public void onFallBlock(final EntityChangeBlockEvent event) {
    Entity s = event.getEntity();

    if (!(s instanceof FallingBlock)) {
      return;
    }

    FallingBlock block = (FallingBlock) s;

    Material type = block.getBlockData().getMaterial();
    if (type != Material.ANVIL && type != Material.CHIPPED_ANVIL && type != Material.DAMAGED_ANVIL) {
      return;
    }

    UUID uuid = s.getUniqueId();

    if (event.getTo() == Material.AIR) {
      // block has begun falling
      System.out.println("Begun falling, entity changed to " + event.getTo());
      fallingMap.put(uuid, s.getLocation());
      return;
    }

    // block has stopped falling
    // how far has the entity fallen?
    Location initialLocation = fallingMap.get(uuid);
    Location finalLocation = s.getLocation();
    double distanceFallen = initialLocation.getY() - finalLocation.getY();


    // what type of block does it land on?
    Block landedOn = finalLocation.add(new Vector(0, -1, 0)).getBlock();
    if (landedOn.getType() != Material.OBSIDIAN) {
      return;
    }

    List<MetadataValue> anvilFallenDistance = landedOn.getMetadata("AnvilFallenDistance");
    
    if (!anvilFallenDistance.isEmpty()) {
      distanceFallen += anvilFallenDistance.get(0).asDouble();
    }

    if (distanceFallen > requiredAnvilFallenDistance) {
      landedOn.setType(Material.BEDROCK);
      landedOn.removeMetadata("AnvilFallenDistance", plugin);
      return;
    }

    landedOn.setMetadata("AnvilFallenDistance", new FixedMetadataValue(plugin, distanceFallen));
  }
}
