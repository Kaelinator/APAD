package com.kaelkirk;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import com.kaelkirk.brew.BaseXRayBrew;
import com.kaelkirk.brew.BlockXRayBrew;
import com.kaelkirk.brew.BrewingRecipe;
import com.kaelkirk.event.PlayerDrinkXRayPotion;
import com.kaelkirk.event.PotionEvent;

public class SpigotPlugin extends JavaPlugin {

  private PlayerDrinkXRayPotion xRayPotionListener;

  @Override
  public void onDisable() {
    // Don't log disabling, Spigot does that for you automatically!
    xRayPotionListener.stopXRayEffect();
  }

  @Override
  public void onEnable() {
    // Don't log enabling, Spigot does that for you automatically!

    // Commands enabled with following method must have entries in plugin.yml
    // getCommand("example").setExecutor(new ExampleCommand(this));
    System.out.println("Hello world from X-Ray Potions");
    BrewingRecipe.setPlugin(this);
    new BrewingRecipe(Material.GLASS, new BaseXRayBrew());

    for (Material type : Material.values()) {
      if (type.isBlock() && type.isSolid() && !type.isAir()) {
        // System.out.println("registering " + type);
        new BrewingRecipe(type, new BlockXRayBrew());
      }
    }

    getServer().getPluginManager().registerEvents(new PotionEvent(), this);
    getServer().getPluginManager().registerEvents(xRayPotionListener = new PlayerDrinkXRayPotion(this), this);
  }
}
