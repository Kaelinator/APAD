package com.kaelkirk.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.kaelkirk.BannerPlotPlugin;
import com.kaelkirk.registry.BannerPlotRegistry;

public class PlayerCreatePlotBanner implements Listener {
  
  private BannerPlotRegistry registry;

  private DyeColor[] colors = {
    DyeColor.WHITE,
    DyeColor.ORANGE,
    DyeColor.MAGENTA,
    DyeColor.LIGHT_BLUE,
    DyeColor.YELLOW,
    DyeColor.LIME,
    DyeColor.PINK,
    DyeColor.GRAY,
    DyeColor.LIGHT_GRAY,
    DyeColor.CYAN,
    DyeColor.PURPLE,
    DyeColor.BLUE,
    DyeColor.BROWN,
    DyeColor.GREEN,
    DyeColor.RED,
    DyeColor.BLACK
  };

  private PatternType[] patternTypes = {
    PatternType.BASE,
    PatternType.SQUARE_BOTTOM_LEFT,
    PatternType.SQUARE_BOTTOM_RIGHT,
    PatternType.SQUARE_TOP_LEFT,
    PatternType.SQUARE_TOP_RIGHT,
    PatternType.STRIPE_BOTTOM,
    PatternType.STRIPE_TOP,
    PatternType.STRIPE_LEFT,
    PatternType.STRIPE_RIGHT,
    PatternType.STRIPE_CENTER,
    PatternType.STRIPE_MIDDLE,
    PatternType.STRIPE_DOWNRIGHT,
    PatternType.STRIPE_DOWNLEFT,
    PatternType.SMALL_STRIPES,
    PatternType.CROSS,
    PatternType.STRAIGHT_CROSS,
    PatternType.TRIANGLE_BOTTOM,
    PatternType.TRIANGLE_TOP,
    PatternType.TRIANGLES_BOTTOM,
    PatternType.TRIANGLES_TOP,
    PatternType.DIAGONAL_LEFT,
    PatternType.DIAGONAL_RIGHT,
    PatternType.DIAGONAL_UP_LEFT,
    PatternType.DIAGONAL_UP_RIGHT,
    PatternType.CIRCLE,
    PatternType.RHOMBUS,
    PatternType.HALF_VERTICAL,
    PatternType.HALF_HORIZONTAL,
    PatternType.HALF_VERTICAL_RIGHT,
    PatternType.HALF_HORIZONTAL_BOTTOM,
    PatternType.BORDER,
    PatternType.CURLY_BORDER,
    PatternType.CREEPER,
    PatternType.GRADIENT,
    PatternType.GRADIENT_UP,
    PatternType.BRICKS,
    PatternType.SKULL,
    PatternType.FLOWER,
    PatternType.MOJANG,
    PatternType.GLOBE,
    PatternType.PIGLIN
  };

  public PlayerCreatePlotBanner(BannerPlotRegistry registry) {
    this.registry = registry;
  }

  @EventHandler
  public void onPlayerCreatePlotBanner(PlayerInteractEvent event) {
    Block clickedBlock = event.getClickedBlock();
    ItemStack clickedItem = event.getItem();

    if (clickedBlock == null & clickedItem == null) {
      return;
    }

    if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    if (!BannerPlotPlugin.BANNERS.contains(clickedBlock.getType())) {
      return;
    }

    if (clickedItem.getType() != Material.PLAYER_HEAD) {
      return;
    }

    Player player = event.getPlayer();
    Chunk chunk = clickedBlock.getChunk();

    event.setCancelled(true);

    Banner banner = (Banner) clickedBlock.getState();

    if (registry.isChunkClaimed(chunk)) {
      player.getWorld().playSound(banner.getLocation(),
        Sound.BLOCK_NOTE_BLOCK_BANJO,
        SoundCategory.AMBIENT, 1.0f, 1.0f
      );
      player.getWorld().playSound(banner.getLocation(),
        Sound.BLOCK_NOTE_BLOCK_BANJO,
        SoundCategory.AMBIENT, 1.0f, 1.2f
      );
      player.getWorld().playSound(banner.getLocation(),
        Sound.BLOCK_NOTE_BLOCK_BANJO,
        SoundCategory.AMBIENT, 1.0f, 0.5f
      );
      player.getWorld().playSound(banner.getLocation(),
        Sound.BLOCK_NOTE_BLOCK_BANJO,
        SoundCategory.AMBIENT, 1.0f, 0.7f
      );
      return;
    }

    SkullMeta meta = (SkullMeta) clickedItem.getItemMeta();
    UUID skullId = meta.getOwningPlayer().getUniqueId();

    if (!registry.claimChunk(chunk, banner, skullId)) {
      // chunk claimation unsuccessful
      return;
    }
    
    Random seededRandom = new Random(skullId.hashCode());

    banner.setPatterns(generateUniquePattern(seededRandom));
    banner.update();

    player.getWorld().playSound(banner.getLocation(),
      HORNS[seededRandom.nextInt(HORNS.length)],
      SoundCategory.AMBIENT, 1.0f, 1.0f
    );

    clickedItem.subtract();

  }

  /*

[00:56:13 INFO]: [BannerPlots] [STDOUT] 12, 5
[00:56:13 INFO]: [BannerPlots] [STDOUT] 11, 16
[00:56:13 INFO]: [BannerPlots] [STDOUT] 11, 40


[01:03:03 INFO]: WHITE
[01:03:03 INFO]: ORANGE
[01:03:03 INFO]: MAGENTA
[01:03:03 INFO]: LIGHT_BLUE
[01:03:03 INFO]: YELLOW
[01:03:03 INFO]: LIME
[01:03:03 INFO]: PINK
[01:03:03 INFO]: GRAY
[01:03:03 INFO]: LIGHT_GRAY
[01:03:03 INFO]: CYAN
[01:03:03 INFO]: PURPLE
[01:03:03 INFO]: BLUE
[01:03:03 INFO]: BROWN
[01:03:03 INFO]: GREEN
[01:03:03 INFO]: RED
[01:03:03 INFO]: BLACK


01:00:06 INFO]: [BannerPlots] [STDOUT] No. of patterns 3
[01:00:06 INFO]: [BannerPlots] [STDOUT] 12, 3
[01:00:06 INFO]: [BannerPlots] [STDOUT] 11, 32
[01:00:06 INFO]: [BannerPlots] [STDOUT] 11, 5
   */

  private List<Pattern> generateUniquePattern(Random random) {
    int numberOfPatterns = random.nextInt(3, 5);
    List<Pattern> patterns = new ArrayList<Pattern>();
    // System.out.println("No. of patterns " + numberOfPatterns);

    // for (DyeColor color : DyeColor.values()) {
    //   System.out.println(color);
    // }

    // for (PatternType type : PatternType.values()) {
    //   System.out.println(type);
    // }


    for (int i = 0; i < numberOfPatterns; i++) {
      int colorIndex = random.nextInt(colors.length);
      int patternIndex = random.nextInt(patternTypes.length);
      System.out.println(colorIndex + ", " + patternIndex);
      DyeColor color = colors[colorIndex];
      PatternType type = patternTypes[patternIndex];
      Pattern pattern = new Pattern(color, type);
      patterns.add(pattern);
    }

    return patterns;
  }

  private static final Sound[] HORNS = new Sound[] {
    Sound.ITEM_GOAT_HORN_PLAY,
    Sound.ITEM_GOAT_HORN_SOUND_0,
    Sound.ITEM_GOAT_HORN_SOUND_1,
    Sound.ITEM_GOAT_HORN_SOUND_2,
    Sound.ITEM_GOAT_HORN_SOUND_3,
    Sound.ITEM_GOAT_HORN_SOUND_4,
    Sound.ITEM_GOAT_HORN_SOUND_5,
    Sound.ITEM_GOAT_HORN_SOUND_6,
    Sound.ITEM_GOAT_HORN_SOUND_7
  };

}
