package com.kaelkirk.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kaelkirk.registry.BannerPlotRegistry;

import net.md_5.bungee.api.ChatColor;

public class ListBannerPlots implements CommandExecutor {

  private BannerPlotRegistry registry;
  private final int PAGE_SIZE = 10;

  public ListBannerPlots(BannerPlotRegistry registry) {
    this.registry = registry;
  }
  
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

    if (args.length > 1) {
      return false;
    }

    if (sender instanceof Player) {
      Player player = (Player) sender;

      int page = 0;
      if (args.length > 0) {
        try {
          page = Integer.parseInt(args[0]) - 1;
          if (page < 0) {
            return false;
          }
        } catch (NumberFormatException e) {
          return false;
        }
      }

      sendPlotInformation(sender, player.getWorld(), PAGE_SIZE, PAGE_SIZE * page);
      return true;
    }

    for (World world: Bukkit.getWorlds()) {
      sendPlotInformation(sender, world);
    }

    return true;
  }

  private void sendPlotInformation(CommandSender sender, World world) {

    HashMap<Long, Pair<Location, UUID>> plots = registry.readBannerPlots(world);

    sender.sendMessage(ChatColor.LIGHT_PURPLE + Integer.toString(plots.size()) + " plot(s) in " + world.getName());

    for (Entry<Long, Pair<Location, UUID>> plot : plots.entrySet()) {
      long chunkKey = plot.getKey();
      Location location = plot.getValue().getLeft();
      UUID ownerId = plot.getValue().getRight();
      String ownerName = Bukkit.getOfflinePlayer(ownerId).getName();

      sender.sendMessage(ownerName + " owns " + ownerId + "/" + chunkKey + " at "
        + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
    }
  }

  private void sendPlotInformation(CommandSender sender, World world, int count, int offset) {

    List<Entry<Long, Pair<Location, UUID>>> plots = new ArrayList<Entry<Long, Pair<Location, UUID>>>();
    plots.addAll(registry.readBannerPlots(world).entrySet());

    sender.sendMessage(ChatColor.LIGHT_PURPLE + Integer.toString(plots.size()) + " plot(s) in " + world.getName() + " page " + (offset / count + 1));

    int stopIndex = Math.min(count + offset, plots.size());
    for (int i = offset; i < stopIndex; i++) {
      Entry<Long, Pair<Location, UUID>> plot = plots.get(i);
      long chunkKey = plot.getKey();
      Location location = plot.getValue().getLeft();
      UUID ownerId = plot.getValue().getRight();
      String ownerName = Bukkit.getOfflinePlayer(ownerId).getName();

      sender.sendMessage(ownerName + " owns " + ownerId + "/" + chunkKey + " at "
        + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
    }
  }
}
