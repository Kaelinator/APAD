package com.kaelkirk.util;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class MobicalManager {

  private NamespacedKey key;

  public MobicalManager(NamespacedKey key) {
    this.key = key;
  }
  
  public boolean isMobical(LivingEntity mob) {
    PersistentDataContainer container = mob.getPersistentDataContainer();
    return container.has(key);
  }

  public void setMobical(LivingEntity mob) {
    PersistentDataContainer container = mob.getPersistentDataContainer();
    container.set(key, PersistentDataType.BOOLEAN, true);
  }

  public void unsetMobical(LivingEntity mob) {
    PersistentDataContainer container = mob.getPersistentDataContainer();
    container.remove(key);
  }
}
