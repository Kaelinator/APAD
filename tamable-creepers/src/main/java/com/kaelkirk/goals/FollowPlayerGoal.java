package com.kaelkirk.goals;

import java.util.EnumSet;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;

public class FollowPlayerGoal implements Goal<Mob> {

  private Plugin plugin;
  private Player player;
  private Mob mob;
  private GoalKey<Mob> key;

  public FollowPlayerGoal(Plugin plugin, Mob mob, Player player) {
    this.plugin = plugin;
    this.mob = mob;
    this.player = player;
  }

  @Override
  public EnumSet<GoalType> getTypes() {
    return EnumSet.of(GoalType.TARGET);
  }

  @Override
  public boolean shouldActivate() {
    return true;
  }

  @Override
  public void tick() {
    if (player == null) {
      return;
    }
    if (mob.getLocation().distance(player.getLocation()) < 2) {
      return;
    }
    mob.getPathfinder().moveTo(player);
  }
  
  @Override
  public GoalKey<Mob> getKey() {
    if (key == null) {
      key = GoalKey.of(Mob.class, new NamespacedKey(plugin, "pet"));
    }
    return key;
  }

}