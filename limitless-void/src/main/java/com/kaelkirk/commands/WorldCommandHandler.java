package com.kaelkirk.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class WorldCommandHandler implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

    if (args.length < 1) {
      return false;
    }

    switch (args[0]) {
      case "list":
        return listWorlds(sender);

      case "tp":
      case "teleport":
        return teleport(sender, args);

      case "unload":
        return unload(sender, args);

      default:
        return false;
    }

  }

  private boolean unload(CommandSender sender, String[] args) {
    if (args.length < 2) {
      return false;
    }

    String worldName = args[1];
    World world = Bukkit.getWorld(worldName);

    if (world == null) {
      sender.sendMessage(ChatColor.RED + "Couldn't find world with name " + worldName);
      return false;
    }

    boolean successful = Bukkit.unloadWorld(world, true);

    if (!successful) {
      sender.sendMessage(ChatColor.RED + "Unload unsuccessful");
      return false;
    }

    sender.sendMessage("Unloaded " + worldName);
    return true;
  }

  private boolean teleport(CommandSender sender, String[] args) {
    if (args.length < 3) {
      return false;
    }

    String playerName = args[1];
    String worldName = args[2];
    Player player = Bukkit.getPlayerExact(playerName);
    World world = Bukkit.getWorld(worldName);

    if (player == null) {
      sender.sendMessage(ChatColor.RED + "Couldn't find player with name " + playerName);
      return false;
    }

    if (world == null) {
      sender.sendMessage(ChatColor.RED + "Couldn't find world with name " + worldName);
      return false;
    }

    if (player.getLocation().getWorld().equals(world)) {
      sender.sendMessage(ChatColor.RED + "Player " + playerName + " is already in world " + worldName);
      return false;
    }

    Location location = world.getSpawnLocation();
    boolean successful = player.teleport(world.getSpawnLocation());

    if (!successful) {
      sender.sendMessage(ChatColor.RED + "Teleport unsuccessful");
      return false;
    }

    sender.sendMessage("Teleported " + playerName + " to " + worldName + " " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());

    return true;
  }

  private boolean listWorlds(CommandSender sender) {

    // list worlds
    List<World> worlds = Bukkit.getWorlds();
    sender.sendMessage(worlds.size() + " worlds:");

    for (World world : worlds) {
      sender.sendMessage(world.getName());
    }

    return true;
  }
  
}
