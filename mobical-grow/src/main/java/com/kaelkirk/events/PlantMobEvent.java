package com.kaelkirk.events;

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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import org.bukkit.event.block.Action;

public class PlantMobEvent implements Listener {

  @EventHandler
  public void onPlayerPlantMobEvent(PlayerInteractEvent event) {
    Action action = event.getAction();
    EquipmentSlot hand = event.getHand();
    Player player = event.getPlayer();
    ItemStack item = player.getEquipment().getItem(EquipmentSlot.HAND);

    Block block = event.getClickedBlock();
    BlockFace blockFace = event.getBlockFace();
    
    if (blockFace == BlockFace.UP &&
        block.getType() == Material.FARMLAND && 
        action == Action.RIGHT_CLICK_BLOCK && 
        hand == EquipmentSlot.HAND && 
        (item == null || item.getType() == Material.AIR
        )) {
        LivingEntity mob = getALeashedYoungMob(player);

        if (mob == null) {
          return;
        }

        player.sendMessage("You right clicked a FARMLAND block with your HAND while leading a baby " + mob.getType());

        // turn off behavior
        mob.setAI(false);
        // placed into block
        Location location = block.getLocation().clone();
        location.set(location.x() + 0.5, location.y() + 0.5, location.z() + 0.5);
        mob.teleport(location);
        
        mob.setLeashHolder(null);
    }
  }


  private LivingEntity getALeashedYoungMob(Player player) {
    for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
      if (!(entity instanceof LivingEntity)) {
        continue;
      }

      LivingEntity mob = (LivingEntity) entity;
      if (mob.isLeashed() && mob.getLeashHolder().equals(player) && mob instanceof Ageable && !((Ageable) mob).isAdult()) {
        return mob;
      }
    }
    return null;
  }
}
