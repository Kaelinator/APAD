package com.kaelkirk.event;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.kaelkirk.brew.BaseXRayBrew;
import com.kaelkirk.brew.BlockXRayBrew;
import com.kaelkirk.manager.XRayManager;

import de.tr7zw.nbtapi.NBTItem;

public class PlayerDrinkXRayPotion implements Listener {

  private Plugin plugin;

  public PlayerDrinkXRayPotion(Plugin plugin) {
    this.plugin = plugin;
  }
  
  @EventHandler
  public void onPlayerDrinkPotionEvent(PlayerItemConsumeEvent event) {
    ItemStack itemConsumed = event.getItem();
    
    if (itemConsumed.getType() != Material.POTION) {
      return;
    }

    NBTItem item = new NBTItem(itemConsumed);

    if (!item.hasTag(BaseXRayBrew.KEY) || !item.getString(BaseXRayBrew.KEY).equals(BaseXRayBrew.ID)) {
      return;
    }

    if (!item.hasTag(BlockXRayBrew.KEY)) {
      return;
    }

    Material toSee = Material.getMaterial(item.getString(BlockXRayBrew.KEY));
    Player player = event.getPlayer();

    if (toSee == null) {
      player.sendMessage("Invalid brew " + item.getString(BlockXRayBrew.KEY));
      return;
    }

    System.out.println("Player " + player.getName() + " sees " + toSee);
    new XRayManager(plugin, player, toSee);
  }


}
