package com.kaelkirk.events;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BlockBrokenByTelekinesisEvent extends Event implements Cancellable {

  private static final HandlerList HANDLERS = new HandlerList();
  private Player player;
  private Block block;
  private World world;
  private boolean isCancelled;

  public BlockBrokenByTelekinesisEvent(Player player, Block block) {
    this.player = player;
    this.block = block;
    this.world = block.getWorld();
    this.isCancelled = false;
  }

  public Player getPlayer() {
    return player;
  }

  public Block getBrokenBlock() {
    return block;
  }

  public World getWorld() {
    return world;
  }

  @Override
  public boolean isCancelled() {
    return isCancelled;
  }

  @Override
  public void setCancelled(boolean cancel) {
    isCancelled = cancel;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

}
