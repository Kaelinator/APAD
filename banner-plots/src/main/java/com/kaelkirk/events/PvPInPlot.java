package com.kaelkirk.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.protection.events.DisallowedPVPEvent;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

public class PvPInPlot implements Listener {
  
  @EventHandler
  public void onPlotOwnerDamaged(DisallowedPVPEvent event) {
    LocalPlayer attackedPlayer = WorldGuardPlugin.inst().wrapPlayer(event.getDefender());
    LocalPlayer attackingPlayer = WorldGuardPlugin.inst().wrapPlayer(event.getAttacker());

    Location loc = attackedPlayer.getLocation();
    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    RegionQuery query = container.createQuery();
    ApplicableRegionSet set = query.getApplicableRegions(loc);
    
    // check if attacked player isn't in a plot
    // can happen when attacking player IS in a plot
    if (set.size() == 0) {
      event.setCancelled(true);
      return;
    }

    boolean allowPvp = false;
    for (ProtectedRegion region : set.getRegions()) {
      if (!region.isPhysicalArea()) {
        continue;
      }
      if (region.isMember(attackingPlayer)) {
        allowPvp = true;
      }
    }
    event.setCancelled(allowPvp);
  }
}
