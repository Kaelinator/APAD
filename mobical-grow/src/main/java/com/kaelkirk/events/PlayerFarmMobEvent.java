package com.kaelkirk.events;

import java.util.Collection;
import java.util.Random;

import org.bukkit.entity.Ageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;

import com.kaelkirk.util.MobicalManager;

public class PlayerFarmMobEvent implements Listener {

  private MobicalManager manager;
  private Random random;

  public PlayerFarmMobEvent(MobicalManager manager) {
    this.manager = manager;
    random = new Random();
  }

  @EventHandler
  public void onPlayerFarmsMobEvent(PlayerInteractEntityEvent event) {

    if (!(event.getRightClicked() instanceof LivingEntity)) {
      return;
    }

    LivingEntity mob = (LivingEntity) event.getRightClicked();

    if (!manager.isMobical(mob)) {
      return;
    }

    // check if baby
    if (!(mob instanceof Ageable)) {
      return;
    }

    Ageable mobAge = (Ageable)mob;
    if (!mobAge.isAdult()) {
      return;
    }

    mobAge.setAge(-random.nextInt(12000, 48000));

    LootTable lootTable = ((Lootable) mob).getLootTable();
    Collection<ItemStack> itemStacks = lootTable.populateLoot(new Random(), new LootContext.Builder(mob.getLocation()).killer(event.getPlayer()).lootedEntity(mob).luck(10).build());

    for (ItemStack item : itemStacks) {
      mob.getWorld().dropItemNaturally(mob.getLocation().add(0, 0.5, 0), item);
    }
  }
}