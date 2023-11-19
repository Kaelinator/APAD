package com.kaelkirk.gui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import net.minecraft.world.item.Item;

public class SuperBundle implements Listener {

  private Inventory bundleInventory;
  private ArrayList<ItemStack> contents;
  private ItemStack superBundle;
  private NBTItem nbtItem;
  private Plugin plugin;
  private HumanEntity player;
  private int page;
  
  public SuperBundle(ItemStack superBundle, Plugin plugin) {
    Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    this.plugin = plugin;

    this.superBundle = superBundle;
    bundleInventory = Bukkit.createInventory(null, 54, "Super Bundle");

    nbtItem = new NBTItem(superBundle);

    if (!nbtItem.hasTag("inventory")) {
      nbtItem.addCompound("inventory");
      NBTCompound itemsCompound = nbtItem.getCompound("inventory");
      ArrayList<ItemStack> list = new ArrayList<ItemStack>();
      addPage(list);
      itemsCompound.setString("data", contentsToString(list));
    }

    NBTCompound itemsCompound = nbtItem.getCompound("inventory");
    contents = (ArrayList<ItemStack>) stringToContents(itemsCompound.getString("data"));
    page = 0;
    populateInventory(bundleInventory, page, contents);
  }

  public Inventory getBundleInventory() {
    return bundleInventory;
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    Inventory inventory = event.getInventory();

    if (!inventory.equals(bundleInventory)) {
      return;
    }
    
    if ((event.getCurrentItem() != null && event.getCurrentItem().equals(superBundle))
      || (event.getCursor() != null && event.getCursor().equals(superBundle))) {
      event.setCancelled(true);
      return;
    }

    ItemStack currentStack = event.getCurrentItem();
    ItemStack previousStack = event.getCursor();

    player = event.getWhoClicked();

    if (currentStack != null && currentStack.getType() == Material.STONE_BUTTON) {
      NBTItem nbt = new NBTItem(currentStack);
      if (nbt.hasTag("navigate")) {
        page += nbt.getInteger("navigate");
        if (page < 0) {
          page = 0;
        } else if ((page + 1) * 45 >= contents.size()) {
          page = contents.size() / 45 - 1;
        }
        populateInventory(bundleInventory, page, contents);
        player.sendMessage("page " + page);
        event.setCancelled(true);
        return;
      }
    }

    if (previousStack != null && previousStack.getType() == Material.STONE_BUTTON) {
      NBTItem nbt = new NBTItem(previousStack);
      if (nbt.hasTag("navigate")) {
        page += nbt.getInteger("navigate");
        if (page < 0) {
          page = 0;
        } else if ((page + 1) * 45 >= contents.size()) {
          page = contents.size() / 45 - 1;
        }
        populateInventory(bundleInventory, page, contents);
        player.sendMessage("page " + page);
        event.setCancelled(true);
        return;
      }
    }

    bundleInventory = inventory;
    player.sendMessage(event.getAction() + " " +
      event.getClickedInventory() + " currentStack: " +
      (currentStack == null ? "" : currentStack.getType()) + " previousStack: " +
      (previousStack == null ? "" : previousStack.getType()) + " ");

    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
      public void run() {

        NBTCompound itemsCompound = nbtItem.getCompound("inventory");

        List<ItemStack> bundleContent = Arrays.asList(bundleInventory.getContents());
        if ((page + 1) * 45 > contents.size()) {
          addPage(contents);
        }

        for (int i = 0; i < bundleContent.size(); i++) {
          if (i == 45 || i == 53) {
            continue;
          }
          int contentSlot = bundleSlotToContentSlot(page, i);
          if (convertContentSlotToBundlePage(contentSlot) == page) {
            contents.set(contentSlot, bundleContent.get(i));
          } else {
            int j = 0;
            for (; j < contents.size(); j++) {
              if (contents.get(j) == null) {
                contents.set(j, bundleContent.get(i));
                break;
              }
            }
            if (j == contents.size()) {
              addPage(contents);
              contents.set(j, bundleContent.get(i));
            }
          }

        }
        populateInventory(bundleInventory, page, contents);

        int slot = player.getInventory().getHeldItemSlot();
        Inventory inventory = player.getInventory();
        if (inventory.getItem(slot) == null || !inventory.getItem(slot).equals(superBundle)) {
          ItemStack[] inventoryContent = inventory.getContents();
          for (slot = 0; slot < inventoryContent.length; slot++) {
            if (inventoryContent[slot] != null && inventoryContent[slot].equals(superBundle)) {
              break;
            }
          }
          if (slot == inventoryContent.length) {
            player.sendMessage("bundle no longer in inventory. Closing");
            bundleInventory.close();
            return;
          }
        }

        itemsCompound.clearNBT();
        itemsCompound.setString("data", contentsToString(contents));
        superBundle = nbtItem.getItem();

        player.sendMessage("bundle is at slot " + slot);
        inventory.setItem(slot, superBundle);
      }
    }, 0);
  }

  // Cancel dragging in our inventory
  @EventHandler
  public void onInventoryClick(InventoryDragEvent event) {
    if (event.getInventory().equals(bundleInventory)) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent event) {
    Inventory inventory = event.getInventory();

    if (!inventory.equals(bundleInventory)) {
      return;
    }

    InventoryCloseEvent.getHandlerList().unregister(this);
    InventoryClickEvent.getHandlerList().unregister(this);
  }
  
  private void populateInventory(Inventory toPopulate, int page, List<ItemStack> contents) {
    for (int i = 0; i < 45; i++) {
      int contentSlot = bundleSlotToContentSlot(page, i);
      ItemStack stack = contents.get(contentSlot);
      if (stack == null) {
        bundleInventory.setItem(i, new ItemStack(Material.AIR));
        continue;
      }
      bundleInventory.setItem(i, stack);
    }

    for (int i = 45; i < 54; i++) {
      bundleInventory.setItem(i, new ItemStack(Material.AIR));
    }

    ItemStack nextButton = new ItemStack(Material.STONE_BUTTON);
    NBTItem nbt1 = new NBTItem(nextButton);
    nbt1.setInteger("navigate", 1);
    nextButton = nbt1.getItem();

    ItemStack prevButton = new ItemStack(Material.STONE_BUTTON);
    NBTItem nbt2 = new NBTItem(prevButton);
    nbt2.setInteger("navigate", -1);
    prevButton = nbt2.getItem();

    bundleInventory.setItem(45, prevButton);
    bundleInventory.setItem(53, nextButton);
  }

  public String contentsToString(List<ItemStack> contents) {
    try {
      ByteArrayOutputStream str = new ByteArrayOutputStream();
      BukkitObjectOutputStream data = new BukkitObjectOutputStream(str);
      data.writeInt(contents.size());
      for (int i = 0; i < contents.size(); i++) {
        data.writeObject(contents.get(i));
      }
      data.close();
      return Base64.getEncoder().encodeToString(str.toByteArray());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
}

  public List<ItemStack> stringToContents(String inventoryData) {
    try {
      ByteArrayInputStream stream = new ByteArrayInputStream(Base64.getDecoder().decode(inventoryData));
      BukkitObjectInputStream data = new BukkitObjectInputStream(stream);
      int size = data.readInt();
      List<ItemStack> result = new ArrayList<ItemStack>(size);
      for (int i = 0; i < size; i++) {
        result.add((ItemStack) data.readObject());
      }
      data.close();
      return result;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private void addPage(ArrayList<ItemStack> list) {
    for (int i = 0; i < 45; i++) {
      list.add(null);
    }
  }

  private int convertContentSlotToBundleSlot(int contentSlot) {
    return contentSlot % 45;
  }

  private int convertContentSlotToBundlePage(int contentSlot) {
    return contentSlot / 45;
  }

  private int bundleSlotToContentSlot(int page, int bundleSlot) {
    return page * 45 + bundleSlot;
  }

}
