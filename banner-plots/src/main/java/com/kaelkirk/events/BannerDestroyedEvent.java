package com.kaelkirk.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.Plugin;

import com.kaelkirk.BannerPlotPlugin;
import com.kaelkirk.registry.BannerPlotRegistry;

public class BannerDestroyedEvent implements Listener {

  private Plugin plugin;
  private BannerPlotRegistry registry;

  public BannerDestroyedEvent(Plugin plugin, BannerPlotRegistry registry) {
    this.plugin = plugin;
    this.registry = registry;
  }

  @EventHandler
  public void onBannerDestroyedByPlayer(BlockBreakEvent event) {
    if (event.isCancelled()) {
      return;
    }

    Block block = event.getBlock();
    if (!BannerPlotPlugin.BANNERS.contains(block.getType())) {
      return;
    }

    if (!wasBannerClaimingChunk(block)) {
      return;
    }

    if (!registry.unclaimCunk(block.getChunk())) {
      System.err.println("FAILED TO UNCLAIM CHUNK");
      return;
    }

    System.out.println(block.getType() + " broken by player at " + block.getLocation());
    playBrokenSound(block.getLocation());
  }

  @EventHandler
  public void onBannerDestroyedByExplosion(BlockExplodeEvent event) {

    if (event.isCancelled()) {
      return;
    }

    for (Block block : event.blockList()) {
      if (!BannerPlotPlugin.BANNERS.contains(block.getType())) {
        continue;
      }

      if (!wasBannerClaimingChunk(block)) {
        continue;
      }

      if (!registry.unclaimCunk(block.getChunk())) {
        System.err.println("FAILED TO UNCLAIM CHUNK");
        continue;
      }

      System.out.println(block.getType() + " broken by unknown explosion at " + block.getLocation());
      playBrokenSound(block.getLocation());
    }
  }

  @EventHandler
  public void onBannerDestroyedByExplosion(EntityExplodeEvent event) {

    if (event.isCancelled()) {
      return;
    }

    for (Block block : event.blockList()) {
      if (!BannerPlotPlugin.BANNERS.contains(block.getType())) {
        continue;
      }

      if (!wasBannerClaimingChunk(block)) {
        continue;
      }

      if (!registry.unclaimCunk(block.getChunk())) {
        System.err.println("FAILED TO UNCLAIM CHUNK");
        continue;
      }


      System.out.println(block.getType() + " broken by explosion at " + block.getLocation());
      playBrokenSound(block.getLocation());
    }
  }

  @EventHandler
  public void onBannerDestroyedByPhysics(BlockPhysicsEvent event) {
    if (event.isCancelled()) {
      return;
    }

    Block block = event.getBlock();

    if (!BannerPlotPlugin.BANNERS.contains(block.getType())) {
      return;
    }

    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
      @Override
      public void run() {
        if (BannerPlotPlugin.BANNERS.contains(block.getType())) {
          return;
        }

        if (!wasBannerClaimingChunk(block)) {
          return;
        }

        if (!registry.unclaimCunk(block.getChunk())) {
          System.err.println("FAILED TO UNCLAIM CHUNK");
          return;
        }

        System.out.println(block.getType() + " broken by physics at " + block.getLocation());
        playBrokenSound(block.getLocation());
      }
      
    });
  }

  private boolean wasBannerClaimingChunk(Block block) {

    Location bannerLocation = registry.getBannerLocation(block.getChunk());

    if (bannerLocation == null) {
      return false;
    }

    if (!bannerLocation.equals(block.getLocation())) {
      return false;
    }

    return true;
  }

  private void playBrokenSound(Location location) {
    location.getWorld().playSound(location, Sound.BLOCK_NOTE_BLOCK_BANJO, SoundCategory.AMBIENT, 1.0f, 1.0f);
    location.getWorld().playSound(location, Sound.BLOCK_NOTE_BLOCK_BANJO, SoundCategory.AMBIENT, 1.0f, 1.2f);
    location.getWorld().playSound(location, Sound.BLOCK_NOTE_BLOCK_BANJO, SoundCategory.AMBIENT, 1.0f, 0.5f);
    location.getWorld().playSound(location, Sound.BLOCK_NOTE_BLOCK_BANJO, SoundCategory.AMBIENT, 1.0f, 0.7f);
  }
}
