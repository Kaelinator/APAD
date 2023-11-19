package com.kaelkirk;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import com.kaelkirk.brew.BaseXRayBrew;
import com.kaelkirk.brew.BrewingRecipe;
import com.kaelkirk.event.PotionEvent;

public class SpigotPlugin extends JavaPlugin {

  // private NamespacedKey ownerKey;

  @Override
  public void onDisable() {
    // Don't log disabling, Spigot does that for you automatically!
  }

  @Override
  public void onEnable() {
    // Don't log enabling, Spigot does that for you automatically!

    // Commands enabled with following method must have entries in plugin.yml
    // getCommand("example").setExecutor(new ExampleCommand(this));
    System.out.println("Hello world from X-Ray Potions");
    // ownerKey = new NamespacedKey(this, "Owner");
    BrewingRecipe.setPlugin(this);
    new BrewingRecipe(Material.GLASS, new BaseXRayBrew());
    getServer().getPluginManager().registerEvents(new PotionEvent(), this);
  }
}
