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

  private List<Pattern> generateUniquePattern(Random random) {
    int numberOfPatterns = random.nextInt(3, 5);
    List<Pattern> patterns = new ArrayList<Pattern>();

    for (int i = 0; i < numberOfPatterns; i++) {
      DyeColor color = DyeColor.values()[random.nextInt(DyeColor.values().length)];
      PatternType type = PatternType.values()[random.nextInt(PatternType.values().length)];
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
