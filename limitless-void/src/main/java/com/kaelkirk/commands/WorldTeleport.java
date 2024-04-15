package com.kaelkirk.commands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class WorldTeleport implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

    if (args.length < 1) {

      // list worlds
      List<World> worlds = Bukkit.getWorlds();
      sender.sendMessage(worlds.size() + " worlds:");

      for (World world : worlds) {
        sender.sendMessage(world.getName());
      }
    }

    return true;
  }
  
}
