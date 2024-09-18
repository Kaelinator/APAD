package com.kaelkirk.events;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.Lootable;

import com.kaelkirk.quests.QuestGiver;

public class PlayerInteractWithQuestGiver implements Listener {

  @EventHandler
  public void onPlayerInteractWithQuestGiver(PlayerInteractEntityEvent event) {
    // runs when player interacts with a mob that has a quest defined
    Entity questGiverEntity = event.getRightClicked();

    if (!(questGiverEntity instanceof Lootable)) {
      return;
    }

    Player questTaker = event.getPlayer();
    ItemStack itemClickedWith = questTaker.getEquipment().getItem(event.getHand());

    if (itemClickedWith.getType() != Material.WHEAT) {
      return;
    }

    QuestGiver questGiver = QuestGiver.fromEntity(questGiverEntity);
    

    itemClickedWith.subtract();
    questTaker.giveExp(5);
  }
}
