package com.kaelkirk.brew;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class BlockXRayBrew extends BrewAction {

  private final NamespacedKey key;

  public BlockXRayBrew(NamespacedKey key) {
    this.key = key;
  }

  @Override
  public void brew(BrewerInventory inventory, ItemStack brewedItem, ItemStack ingredient) {
    ItemMeta meta = brewedItem.getItemMeta();
    if (!(meta instanceof PotionMeta)) {
      return;
    }
    PotionMeta potionMeta = (PotionMeta) meta;
    PotionType brewedItemPotionType = potionMeta.getBasePotionType();
    PersistentDataContainer container = potionMeta.getPersistentDataContainer();

    if (brewedItemPotionType != PotionType.AWKWARD) {
      return;
    }

    if (!container.has(key) && !container.get(key, PersistentDataType.STRING).equals(BaseXRayBrew.ID)) {
      return;
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
        container.set(key, PersistentDataType.STRING, ingredient.getType().toString());
        brewedItem.setItemMeta(potionMeta);
        inventory.setItem(i, brewedItem);

        for (HumanEntity h : inventory.getViewers()) {
          h.sendMessage("you brewed an xray potion for " + inventory.getItem(i).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
        }
      }
    }


  }
  
}
