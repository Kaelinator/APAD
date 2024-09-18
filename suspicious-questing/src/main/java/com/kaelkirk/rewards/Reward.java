package com.kaelkirk.rewards;

import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Reward {
  private RewardType type;
  private int amount;
  private ItemStack item;

  public void applyReward(Player player) {
    switch (type) {
      case ATTRIBUTE:
        throw new NotImplementedException();

      case EFFECT:
        throw new NotImplementedException();

      case ITEM:
        player.getWorld().dropItemNaturally(player.getLocation(), item);
        break;

      case LEVEL:
        player.setLevel(amount);
        break;

      case XP:
        player.giveExp(amount);
        break;

      default:
        break;
    }
  }

  public RewardType getType() {
    return type;
  }

  public int getAmount() {
    return amount;
  }

  public ItemStack getItem() {
    return item;
  }

}