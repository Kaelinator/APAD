package com.kaelkirk.managers;

import java.util.Random;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.kaelkirk.util.WeightedTreeNode;

@SuppressWarnings("deprecation")
public class RandomItemPicker {

  private Random random;
  private WeightedTreeNode<Material> picker;
  private final Set<Material> ILLEGAL_MATERIALS = Set.of(
    Material.BEDROCK,
    Material.STRUCTURE_BLOCK,
    Material.STRUCTURE_VOID,
    Material.COMMAND_BLOCK_MINECART,
    Material.REPEATING_COMMAND_BLOCK,
    Material.CHAIN_COMMAND_BLOCK,
    Material.COMMAND_BLOCK,
    Material.END_GATEWAY,
    Material.END_PORTAL,
    Material.END_PORTAL_FRAME,
    Material.JIGSAW,
    Material.FIRE,
    Material.SOUL_FIRE,
    Material.LIGHT,
    Material.DEBUG_STICK,
    Material.BARRIER,
    Material.ENDER_DRAGON_SPAWN_EGG,
    Material.KNOWLEDGE_BOOK,
    Material.POTION,
    Material.ENCHANTED_BOOK
  );
  
  public RandomItemPicker() {
    random = new Random();

    picker = new WeightedTreeNode<Material>("root", 1);
    picker.addChild(new WeightedTreeNode<>("common", 59));
    picker.addChild(new WeightedTreeNode<>("uncommon", 30));
    picker.addChild(new WeightedTreeNode<>("rare", 10));
    picker.addChild(new WeightedTreeNode<>("legendary", 1));

    WeightedTreeNode<Material> common = picker.getChild("common");
    common.addChild(new WeightedTreeNode<>("classed", 1));
    common.addChild(new WeightedTreeNode<>("misc", 1));

    WeightedTreeNode<Material> classed = common.getChild("classed");
    WeightedTreeNode<Material> misc = common.getChild("misc");

    WeightedTreeNode<Material> uncommon = picker.getChild("uncommon");
    uncommon.addChild(new WeightedTreeNode<>("SPAWN_EGG", 1));
    uncommon.addChild(new WeightedTreeNode<>("MUSIC_DISC", 1));
    uncommon.addChild(new WeightedTreeNode<>("POTTERY_SHERD", 1));
    uncommon.addChild(new WeightedTreeNode<>("SMITHING_TEMPLATE", 1));
    uncommon.addChild(new WeightedTreeNode<>("misc", 1));
    
    WeightedTreeNode<Material> rare = picker.getChild("rare");
    WeightedTreeNode<Material> legendary = picker.getChild("legendary");


    materialLoop: for (Material material : Material.values()) {

      if (ILLEGAL_MATERIALS.contains(material) || material.isAir()
        || (!material.isItem())) {
        continue;
      }

      switch (material) {

        case GOAT_HORN:
          uncommon.getChild("misc").addValue(material);
          continue;

        case HEART_OF_THE_SEA:
        case NAUTILUS_SHELL:
        case ANVIL:
        case END_CRYSTAL:
        case ENDER_CHEST:
          rare.addValue(material);
          continue;

        case ENCHANTED_GOLDEN_APPLE:
        case BEACON:
        case DRAGON_HEAD:
        case ELYTRA:
        case TRIDENT:
        case ANCIENT_DEBRIS:
        case NETHER_STAR:
        case SPAWNER:
        case ENCHANTING_TABLE:
        case BUNDLE:
          legendary.addValue(material);
          continue;

        default:
          break;
      }

      for (String key : new String[] { "SPAWN_EGG", "MUSIC_DISC", "POTTERY_SHERD", "SMITHING_TEMPLATE" }) {
        if (material.name().contains(key)) {
          uncommon.getChild(key).addValue(material);
          continue materialLoop;
        }
      }

      for (String key : new String[] { "IRON", "GOLD", "CHAINMAIL", "EMERALD" }) {
        if (material.name().contains(key)) {
          rare.addValue(material);
          continue materialLoop;
        }
      }

      for (String key : new String[] { "DIAMOND", "NETHERITE", "SHULKER_BOX" }) {
        if (material.name().contains(key)) {
          legendary.addValue(material);
          continue materialLoop;
        }
      }

      // removed items
      for (String key : new String[] {
        "_CONCRETE",
        "_STAINED_GLASS", // includes stained glass panes
        "_CARPET",
        "_TERRACOTTA", // includes glazed terracotta
        "_WOOL",
        "_WAXED",
        "_PLANKS",
        "_SIGN",
        "_SIGN",
        "_DOOR",
        "_TRAPDOOR",
        "_BED",
        "_CANDLE",
        "_SLAB",
        "_STAIRS",
        "_FENCE_GATE",
        "_PRESSURE_PLATE",
        "_WALL",
        "_LEAVES",
        "_FENCE",
        "_BANNER",
        "_CORAL", // includes coral fans
        "_BUTTON"
      }) {
        if (material.name().contains(key)) {
          continue materialLoop;
        }
      }

      if (material.data == null || material.data == MaterialData.class) {
        misc.addValue(material);
        continue;
      }

      String materialClass = material.data.toString();
      if (!classed.hasChild(materialClass)) {
        classed.addChild(new WeightedTreeNode<>(materialClass, 1));
      }

      classed.getChild(materialClass).addValue(material);
    }

    // System.out.println("Unclassed " + materials.size());
    // for (Material material : materials) {
    //   System.out.println(material.name());
    // }

    // System.out.println("This many materialClasses " + materialClasses.size());
    // for (Class<?> materialClass : materialClasses.keySet()) {

    //   System.out.println(materialClass.getName() + " " + materialClasses.get(materialClass).size());
    //   for (Material material : materialClasses.get(materialClass)) {
    //     System.out.println(material.name());
    //   }
    // }

    // picker.printAll(-1);
  }

  public ItemStack pickRandomItem() {
    return new ItemStack(picker.pickValueRandomly(random));
  }

}
