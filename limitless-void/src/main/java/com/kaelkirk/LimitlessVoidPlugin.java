package com.kaelkirk;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import com.kaelkirk.commands.WorldCommandHandler;
import com.kaelkirk.events.BlockMobSpawn;
import com.kaelkirk.events.PlayerFallIntoVoid;
import com.kaelkirk.generators.CandlePopulator;
import com.kaelkirk.generators.CrystalPopulator;
import com.kaelkirk.generators.LimitlessVoidBiomeProvider;
import com.kaelkirk.generators.LimitlessVoidGenerator;
import com.kaelkirk.generators.TrappedChestPopulator;

import net.kyori.adventure.util.TriState;

public class LimitlessVoidPlugin extends JavaPlugin {

  private World limitlessVoidWorld;
  private World overworld;

  @Override
  public void onDisable() { }

  @Override
  public void onEnable() {
    this.saveDefaultConfig();
    getCommand("world").setExecutor(new WorldCommandHandler());

    overworld = Bukkit.getWorlds().get(0);

    limitlessVoidWorld = new WorldCreator(this.getConfig().getString("limitlessVoidLevelName"))
      .generator(new LimitlessVoidGenerator())
      .biomeProvider(new LimitlessVoidBiomeProvider())
      .environment(Environment.NORMAL)
      .keepSpawnLoaded(TriState.FALSE)
      .createWorld();
    
    limitlessVoidWorld.getPopulators().clear();
    limitlessVoidWorld.getPopulators().addAll(Arrays.asList(
      new CandlePopulator(0.5f, 0.5f, 0),
      new TrappedChestPopulator(this, 0f, 0.5f, 0, limitlessVoidWorld),
      new CrystalPopulator(10, 100, 0.1f, 1.5f)
    ));

    List<String> extraWorlds = this.getConfig().getStringList("extraWorlds");
    for (String worldName : extraWorlds) {
      new WorldCreator(worldName).createWorld();
    }

    getLogger().log(Level.INFO, "Loaded " + extraWorlds.size() + " extra world" + (extraWorlds.size() == 1 ? "!" : "s!"));
    getServer().getPluginManager().registerEvents(new PlayerFallIntoVoid(limitlessVoidWorld, overworld), this);
    getServer().getPluginManager().registerEvents(new BlockMobSpawn(limitlessVoidWorld), this);
  }

  @Override
  public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
    return new LimitlessVoidGenerator(); // Return an instance of the chunk generator we want to use.
  }
}
