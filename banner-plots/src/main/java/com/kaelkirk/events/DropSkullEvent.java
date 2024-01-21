package com.kaelkirk.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class DropSkullEvent implements Listener {
  
  @EventHandler
  public void onPlayerDropSkull(PlayerDeathEvent event) {

    if (event.isCancelled()) {
      return;
    }

    Player player = event.getPlayer();

    ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);

    SkullMeta meta = (SkullMeta) skull.getItemMeta();

    meta.setOwningPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));

    skull.setItemMeta(meta);

    event.getDrops().add(skull);
    player.sendMessage("your skull has been dropped");
  }
}
