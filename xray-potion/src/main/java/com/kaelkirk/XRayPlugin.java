package com.kaelkirk;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.kaelkirk.brew.BaseXRayBrew;
import com.kaelkirk.brew.BlockXRayBrew;
import com.kaelkirk.brew.BrewingRecipe;
import com.kaelkirk.event.PlayerDrinkXRayPotion;
import com.kaelkirk.event.PotionEvent;
import com.kaelkirk.event.ShulkerLoadInEvent;
import com.kaelkirk.manager.XRayManager;

public class XRayPlugin extends JavaPlugin {

  public static final String X_RAY_SHULKER_OWNER_KEY = "CustomName";
  private static final Set<Material> FORBIDDEN = Set.of();
  private ProtocolManager protocolManager;

  @Override
  public void onDisable() {
    XRayManager.stopAllXRayEffects();
  }

  @Override
  public void onEnable() {
    BrewingRecipe.setPlugin(this);
    new BrewingRecipe(Material.GLASS, new BaseXRayBrew());

    for (Material type : Material.values()) {
      if (type.isBlock() && type.isSolid() && !type.isAir() && !FORBIDDEN.contains(type) && type.asItemType() != null) {
        new BrewingRecipe(type, new BlockXRayBrew());
      }
    }

    getServer().getPluginManager().registerEvents(new PotionEvent(), this);
    getServer().getPluginManager().registerEvents(new PlayerDrinkXRayPotion(this), this);
      protocolManager = ProtocolLibrary.getProtocolManager();
    protocolManager.addPacketListener(new ShulkerLoadInEvent(this));
  }
}
