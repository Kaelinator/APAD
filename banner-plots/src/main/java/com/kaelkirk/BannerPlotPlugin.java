package com.kaelkirk;


import java.util.Set;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import com.kaelkirk.commands.ListBannerPlots;
import com.kaelkirk.events.BannerDestroyedEvent;
import com.kaelkirk.events.DropSkullEvent;
import com.kaelkirk.events.PlayerCreatePlotBanner;
import com.kaelkirk.events.PvPInPlot;
import com.kaelkirk.registry.BannerPlotRegistry;

public class BannerPlotPlugin extends JavaPlugin {

  public static final String BANNER_PLOT_REGION_ID = "BANNER_PLOT";
  private NamespacedKey key;
  private BannerPlotRegistry registry;

  public static final Set<Material> BANNERS = Set.of(
    Material.RED_BANNER,
    Material.BLUE_BANNER,
    Material.CYAN_BANNER,
    Material.GRAY_BANNER,
    Material.LIME_BANNER,
    Material.WHITE_BANNER,
    Material.ORANGE_BANNER,
    Material.MAGENTA_BANNER,
    Material.LIGHT_BLUE_BANNER,
    Material.YELLOW_BANNER,
    Material.PINK_BANNER,
    Material.LIGHT_GRAY_BANNER,
    Material.PURPLE_BANNER,
    Material.BROWN_BANNER,
    Material.GREEN_BANNER,
    Material.BLACK_BANNER,
    Material.RED_WALL_BANNER,
    Material.BLUE_WALL_BANNER,
    Material.CYAN_WALL_BANNER,
    Material.GRAY_WALL_BANNER,
    Material.LIME_WALL_BANNER,
    Material.WHITE_WALL_BANNER,
    Material.ORANGE_WALL_BANNER,
    Material.MAGENTA_WALL_BANNER,
    Material.LIGHT_BLUE_WALL_BANNER,
    Material.YELLOW_WALL_BANNER,
    Material.PINK_WALL_BANNER,
    Material.LIGHT_GRAY_WALL_BANNER,
    Material.PURPLE_WALL_BANNER,
    Material.BROWN_WALL_BANNER,
    Material.GREEN_WALL_BANNER,
    Material.BLACK_WALL_BANNER
  );

  @Override
  public void onDisable() { }

  @Override
  public void onEnable() {
    key = new NamespacedKey(this, "BANNER_PLOT");
    registry = new BannerPlotRegistry(key);
    getServer().getPluginManager().registerEvents(new DropSkullEvent(), this);
    getServer().getPluginManager().registerEvents(new PlayerCreatePlotBanner(registry), this);
    getServer().getPluginManager().registerEvents(new PvPInPlot(), this);
    getServer().getPluginManager().registerEvents(new BannerDestroyedEvent(this, registry), this);
    getCommand("listbannerplots").setExecutor(new ListBannerPlots(registry));
  }

}
