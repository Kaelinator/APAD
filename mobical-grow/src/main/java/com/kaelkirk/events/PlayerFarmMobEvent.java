package com.kaelkirk.events;

import java.util.Collection;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;

import org.bukkit.loot.Lootable;
import org.bukkit.loot.LootContext;

public class PlayerFarmMobEvent implements Listener {

  @EventHandler
  public void onPlayerFarmsMobEvent(PlayerInteractEntityEvent event) {

    if(!(event.getRightClicked() instanceof LivingEntity)) {
      return;
    }

    LivingEntity mob = (LivingEntity)event.getRightClicked();

    // check NBT for this later
    if(mob.hasAI()){
        return;
    }

    // check if baby
    if(!(mob instanceof Ageable)) {
        return;
    }

    Ageable mobAge = (Ageable)mob;
    if(!mobAge.isAdult()){
        return;
    }

    mobAge.setAge(-1200);

    LootTable lootTable = ((Lootable)mob).getLootTable();
    Collection<ItemStack> itemStacks = lootTable.populateLoot(new Random(), new LootContext.Builder(mob.getLocation()).killer(event.getPlayer()).lootedEntity(mob).luck(10).build());

    for(ItemStack item : itemStacks){
        mob.getWorld().dropItemNaturally(mob.getLocation().clone().add(0, 2, 0), item);
    }
  }
}