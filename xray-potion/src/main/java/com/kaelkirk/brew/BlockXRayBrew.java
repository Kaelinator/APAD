package com.kaelkirk.brew;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import de.tr7zw.nbtapi.NBTItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class BlockXRayBrew extends BrewAction {

  public static final String KEY = "X_RAY";

  @Override
  public void brew(BrewerInventory inventory, ItemStack brewedItem, ItemStack ingredient) {
    ItemMeta meta = brewedItem.getItemMeta();
    if (!(meta instanceof PotionMeta)) {
      return;
    }
    PotionMeta potionMeta = (PotionMeta) meta;
    PotionType brewedItemPotionType = potionMeta.getBasePotionType();
    NBTItem brewedItemNbt = new NBTItem(brewedItem);

    if (brewedItemPotionType != PotionType.AWKWARD) {
      return;
    }

    if (!brewedItemNbt.hasTag(BaseXRayBrew.KEY) && !brewedItemNbt.getString(BaseXRayBrew.KEY).equals(BaseXRayBrew.ID)) {
      return;
    }

    if (brewedItemNbt.hasTag(KEY)) {
      return;
    }
    
    for (HumanEntity h : inventory.getViewers()) {
      h.sendMessage("you brewed an xray potion");
    }


    potionMeta.setColor(Color.GRAY);
    Component name = Component.text("Potion of X-Ray Vision").decoration(TextDecoration.ITALIC, false);
    potionMeta.displayName(name);

    List<Component> lore = Arrays.asList(new Component[] {
      Component.text("Food & Drinks").decoration(TextDecoration.ITALIC, false).color(TextColor.color(Color.GREEN.asRGB())),
      Component.text("X-Ray Vision (02:00)").decoration(TextDecoration.ITALIC, false).color(TextColor.color(Color.GREEN.asRGB())),
      Component.text(""),
      Component.text("When applied:").decoration(TextDecoration.ITALIC, false).color(TextColor.color(Color.PURPLE.asRGB())),
      Component.text("See " + ingredient.getType()).decoration(TextDecoration.ITALIC, false).color(TextColor.color(Color.GREEN.asRGB())),
    });
    potionMeta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
    potionMeta.lore(lore);
    ItemStack[] contents = inventory.getContents();
    for (int i = 0; i < contents.length; i++) {
      if (contents[i] != null && contents[i].equals(brewedItem)) {
        brewedItem.setItemMeta(potionMeta);
        NBTItem newPotionNbt = new NBTItem(brewedItem);
        newPotionNbt.setString(KEY, ingredient.getType().toString());
        inventory.setItem(i, newPotionNbt.getItem());
      }
    }


  }
  
}
