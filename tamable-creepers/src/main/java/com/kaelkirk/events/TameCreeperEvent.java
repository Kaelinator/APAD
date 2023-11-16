package com.kaelkirk.events;

import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalType;
import com.kaelkirk.goals.FollowPlayerGoal;

public class TameCreeperEvent implements Listener {

  private Random random;
  private Plugin plugin;
  private NamespacedKey ownerKey;

  public TameCreeperEvent(Plugin plugin, NamespacedKey ownerKey) {
    random = new Random();
    this.plugin = plugin;
    this.ownerKey = ownerKey;
  }
  
  @EventHandler
  public void rightClickOnAnimalEvent(PlayerInteractAtEntityEvent event) {

    Entity tamee = event.getRightClicked();
    if (!tamee.getType().equals(EntityType.CREEPER)) {
      return;
    }

    // get the item that the player used
    EquipmentSlot slot = event.getHand();
    Player tamer = event.getPlayer();
    EntityEquipment equipment = tamer.getEquipment();
    ItemStack item = equipment.getItem(slot);

    PersistentDataContainer container = tamee.getPersistentDataContainer();
    World world = tamer.getWorld();

    if (item.getType() != Material.SAND) {
      return;
    }

    if (container.has(ownerKey, PersistentDataType.STRING)) {
      world.spawnParticle(Particle.SMOKE_NORMAL, tamee.getLocation().add(0, 1, 0), 10, 0.25, 0.25, 0.25, 0);
      return;
    }

    if (!tamer.getGameMode().equals(GameMode.CREATIVE)) {
      item.subtract(1);
    }

    if (random.nextInt(10) != 0) {
      world.spawnParticle(Particle.SMOKE_NORMAL, tamee.getLocation().add(0, 1, 0), 10, 0.25, 0.25, 0.25, 0);
      return;
    }

    /* you tamed the creeper */
    Mob m = (Mob) tamee;
    Bukkit.getMobGoals().removeAllGoals(m, GoalType.TARGET);
    Bukkit.getMobGoals().addGoal(m, 0, new FollowPlayerGoal(plugin, m, tamer));
    container.set(ownerKey, PersistentDataType.STRING, UUID.randomUUID().toString());

    world.spawnParticle(Particle.HEART, tamee.getLocation().add(0, 1, 0), 10, 0.25, 0.25, 0.25);
  }

}
