package com.kaelkirk.events;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

public class PaladinReleaseEvent implements Listener {
  
  private NamespacedKey key;
  private Random random;
  public PaladinReleaseEvent(NamespacedKey key) {
    this.key = key;
    random = new Random();
  }

  @EventHandler
  public void onPaladinRelease(EntityDamageByEntityEvent event) {

    Entity damager = event.getDamager();
    if (!(damager instanceof Player)) {
      return;
    }

    if (event.getCause() != DamageCause.ENTITY_ATTACK) {
      return;
    }

    Player player = (Player) damager;

    ItemStack itemUsed = player.getEquipment().getItemInMainHand();

    handleInteraction(player, itemUsed);
  }

  @EventHandler
  public void onPaladinRelease(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    if (event.getHand() == null) {
      return;
    }
    ItemStack itemUsed = player.getEquipment().getItem(event.getHand());

    if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) {
      return;
    }
    handleInteraction(player, itemUsed);
  }

  public void handleInteraction(Player player, ItemStack shield) {

    if (shield == null || shield.getType() != Material.SHIELD) {
      return;
    }

    ItemMeta meta = shield.getItemMeta();
    PersistentDataContainer container = meta.getPersistentDataContainer();

    if (!container.has(key)) {
      return;
    }

    double totalBlockedDamage = container.get(key, PersistentDataType.DOUBLE);

    if (totalBlockedDamage <= 0) {
      return; 
    }

    Location playerLocation = player.getLocation();
    Location playerEyeLocation = player.getEyeLocation();
    Vector playerDirection = playerLocation.getDirection().normalize();
    Vector playerEyeVector = playerEyeLocation.toVector();
    World world = playerLocation.getWorld();
    world.playSound(playerLocation, Sound.ENTITY_RAVAGER_ATTACK, SoundCategory.PLAYERS, 1.0f, 2.0f);
    world.playSound(playerLocation, Sound.ITEM_SHIELD_BLOCK, 1.0f, 1.0f);

    for (int i = 0; i < totalBlockedDamage / 2; i++) {
      world.spawnParticle(
        Particle.CRIT,
        playerEyeLocation.clone().add(playerDirection),
        0, 
        playerDirection.getX() + random.nextDouble(-0.5, 0.5),
        playerDirection.getY() + random.nextDouble(-0.5, 0.5),
        playerDirection.getZ() + random.nextDouble(-0.5, 0.5),
        2.0
      );
    }

    // player.sendMessage("releasing " + totalBlockedDamage + " damage");

    container.set(key, PersistentDataType.DOUBLE, 0d);
    shield.setItemMeta(meta);

    Vector knockbackDirection = playerDirection.clone().multiply(-1);
    for (Entity nearbyEntity : player.getNearbyEntities(4, 4, 4)) {
      if (!(nearbyEntity instanceof LivingEntity)) {
        continue;
      }
      LivingEntity entity = (LivingEntity) nearbyEntity;
      if (isPointWithinCone(entity.getLocation().toVector(), playerEyeVector, playerDirection, 4, 2)
        || isPointWithinCone(entity.getBoundingBox().getCenter(), playerEyeVector, playerDirection, 4, 2)
        || isPointWithinCone(entity.getEyeLocation().toVector(), playerEyeVector, playerDirection, 4, 2)) {

        entity.knockback(totalBlockedDamage / 10, knockbackDirection.getX(), knockbackDirection.getZ());
        entity.damage(totalBlockedDamage, player);
      }
    }

  }

  // https://stackoverflow.com/a/12826333
  private boolean isPointWithinCone(Vector location, Vector coneTip, Vector normalizedDirection, double coneHeight, double coneRadius) {

    double distanceAlongCone = location.clone().subtract(coneTip).dot(normalizedDirection);
    double coneRadiusAtDistance = distanceAlongCone / coneHeight * coneRadius;
    double orthogonalDistanceFromConeAxis = location.clone().subtract(coneTip).subtract(normalizedDirection.clone().multiply(distanceAlongCone)).length();

    return orthogonalDistanceFromConeAxis < coneRadiusAtDistance;
  }
}
