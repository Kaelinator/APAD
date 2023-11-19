package com.kaelkirk.brew;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeWrapper;
import org.bukkit.potion.PotionType;

public class BaseXRayBrew extends BrewAction {

  @Override
  public void brew(BrewerInventory inventory, ItemStack brewedItem, ItemStack ingredient) {

    ItemMeta meta = brewedItem.getItemMeta();
    if (!(meta instanceof PotionMeta)) {
      return;
    }
    PotionMeta potionMeta = (PotionMeta) meta;
    PotionType brewedItemPotionType = potionMeta.getBasePotionType();

    if (brewedItemPotionType != PotionType.AWKWARD) {
      return;
    }

    if (ingredient.getType() != Material.GLASS) {
      return;
    }

    // NBTItem potion = new NBTItem(brewedItem);
    // NBTCompoundList compound = potion.getCompoundList("tag");

    for (HumanEntity h : inventory.getViewers()) {
      h.sendMessage("you brewed an xray potion");
    }
    // potion.get

    potionMeta.setColor(Color.GRAY);
    PotionEffectTypeWrapper potionEffectType = new PotionEffectTypeWrapper2(26, "x_ray_vision");
    PotionEffectType.registerPotionEffectType(potionEffectType);
    potionMeta.addCustomEffect(new PotionEffect(potionEffectType, 10, 0), false);
    brewedItem.setItemMeta(potionMeta);

  }
  
}
