package com.kaelkirk.brew;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class BrewingRecipe {

  private final ItemStack ingredient;
  private final ItemStack fuel;

  private int fuelSet;
  private int fuelCharge;

  private BrewAction action;
  private BrewClock clock;

  private boolean perfect;
  private static ArrayList<BrewingRecipe> recipes;
  private static Plugin plugin;

  public static void setPlugin(Plugin plugin) {
    BrewingRecipe.plugin = plugin;
  }

  public BrewingRecipe(ItemStack ingredient, ItemStack fuel, BrewAction action, boolean perfect, int fuelSet,
      int fuelCharge) {
    this.ingredient = ingredient;
    this.setFuelSet(fuelSet);
    this.fuel = fuel;
    this.setFuelCharge(fuelCharge);
    this.action = action;
    this.perfect = perfect;
    if (BrewingRecipe.recipes == null) {
      BrewingRecipe.recipes = new ArrayList<BrewingRecipe>();
    }
    BrewingRecipe.recipes.add(this);
  }

  public BrewingRecipe(Material ingredient, BrewAction action) {
    this(new ItemStack(ingredient), null, action, true, 40, 0);
  }

  public ItemStack getIngredient() {
    return ingredient;
  }

  public ItemStack getFuel() {
    return fuel;
  }

  public BrewAction getAction() {
    return action;
  }

  public void setAction(BrewAction action) {
    this.action = action;
  }

  public BrewClock getClock() {
    return clock;
  }

  public void setClock(BrewClock clock) {
    this.clock = clock;
  }

  public boolean requiresPerfection() {
    return perfect;
  }

  public void setPerfect(boolean perfect) {
    this.perfect = perfect;
  }

  public static BrewingRecipe getRecipe(BrewerInventory inventory) {
    for (BrewingRecipe recipe : BrewingRecipe.recipes) {

      boolean fuelSpecified = recipe.fuel != null;
      boolean ingredientMatches =
        inventory.getIngredient().isSimilar(recipe.getIngredient()) // ingredient is perfect
        || (!recipe.requiresPerfection() && inventory.getIngredient().getType() == recipe.getIngredient().getType()); // vs type only matches

      if (!fuelSpecified) {
        if (ingredientMatches) {
          return recipe;
        } 
        continue;
      }

      // fuel was specified
      if (inventory.getFuel() == null) {
        if (recipe.getFuel().getType() == Material.AIR) {
          return recipe;
        }
        continue;
      }

      boolean fuelMatches =
        inventory.getFuel().isSimilar(recipe.getFuel()) // fuel is perfect
        || (!recipe.requiresPerfection() && inventory.getFuel().getType() == recipe.getFuel().getType()); // vs type only matches

      if (fuelMatches && ingredientMatches) {
        return recipe;
      }

    }
    return null;
  }

  public void startBrewing(BrewerInventory inventory) {
    clock = new BrewClock(this, inventory, 400);
  }

  public int getFuelSet() {
    return fuelSet;
  }

  public void setFuelSet(int fuelSet) {
    this.fuelSet = fuelSet;
  }

  public int getFuelCharge() {
    return fuelCharge;
  }

  public void setFuelCharge(int fuelCharge) {
    this.fuelCharge = fuelCharge;
  }

  /*
   * Slot 0: 3 Potion Slot Far Left
   * Slot 1: 3 Potion Slot Middle
   * Slot 2: 3 Potion Slot Far Right
   * Slot 3: Ingredient Slot 4: Fuel
   */
  public class BrewClock implements Runnable {
    private BrewerInventory inventory;
    private BrewingRecipe recipe;
    private ItemStack[] before;
    private BrewingStand stand;
    private int current;
    private int thisTask;

    public BrewClock(BrewingRecipe recipe, BrewerInventory inventory, int time) {
        this.recipe = recipe;
        this.inventory = inventory;
        this.stand = inventory.getHolder();
        this.before = inventory.getContents();
        this.current = time;
        // runTaskTimer(Brewery.getInstance(), 0 L, 1L);
        thisTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0L, 1L);
    }

    @Override
    public void run() {
      if (current == 0) {
        // Set ingredient to 1 less than the current. Otherwise set to air
        if (inventory.getIngredient() != null && inventory.getIngredient().getAmount() > 1) {
          ItemStack is = inventory.getIngredient();
          is.setAmount(inventory.getIngredient().getAmount() - 1);
          inventory.setIngredient(is);
        } else {
          inventory.setIngredient(new ItemStack(Material.AIR));
        }
        // Check the fuel in the recipe is more than 0, and exists
        ItemStack newFuel = recipe.getFuel();
        if (recipe.getFuel() != null && recipe.getFuel().getType() != Material.AIR &&
            recipe.getFuel().getAmount() > 0) {
          /*
           * We count how much fuel should be taken away in order to fill
           * the whole fuel bar
           */
          int count = 0;
          while (inventory.getFuel().getAmount() > 0 && stand.getFuelLevel() + recipe.fuelCharge < 100) {
            stand.setFuelLevel(stand.getFuelLevel() + recipe.fuelSet);
            count++;
          }
          // If the fuel in the inventory is 0, set it to air.
          if (inventory.getFuel().getAmount() == 0) {
            newFuel = new ItemStack(Material.AIR);
          } else {
            /*
             * Otherwise, set the percent of fuel level to 100 and update the
             * count of the fuel
             */
            stand.setFuelLevel(100);
            newFuel.setAmount(inventory.getFuel().getAmount() - count);
          }
        } else {
          newFuel = new ItemStack(Material.AIR);
        }
        inventory.setFuel(newFuel);
        // Brew recipe for each item put in
        for (int i = 0; i < 3; i++) {
          if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR) {
            continue;
          }
          recipe.getAction().brew(inventory, inventory.getItem(i), ingredient);
        }
        // Set the fuel level
        stand.setFuelLevel(stand.getFuelLevel() - recipe.fuelCharge);
        Bukkit.getScheduler().cancelTask(thisTask);
        return;
      }
      // If a player drags an item, fuel, or any contents, reset it
      if (searchChanged(before, inventory.getContents(), perfect)) {
        Bukkit.getScheduler().cancelTask(thisTask);
        return;
      }
      // Decrement, set the brewing time, and update the stand
      current--;
      stand.setBrewingTime(current);
      stand.update(true);
    }

    // Check if any slots were changed
    public boolean searchChanged(ItemStack[] before, ItemStack[] after, boolean mode) {
      for (int i = 0; i < before.length; i++) {
        if (before[i] == null && after[i] == null) {
          return false;
        }
        if ((before[i] != null && after[i] == null) || (before[i] == null && after[i] != null)) {
          return true;
        }
        if (mode && !before[i].isSimilar(after[i])) {
          return false;
        }
        if (!mode && !(before[i].getType() == after[i].getType())) {
          return false;
        }
      }
      return true;
    }
  }

}