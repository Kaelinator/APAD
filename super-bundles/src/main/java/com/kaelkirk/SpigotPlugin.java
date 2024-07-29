package com.kaelkirk;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import com.kaelkirk.events.OnPlayerOpenSuperBundle;

public class SpigotPlugin extends JavaPlugin {


  private NamespacedKey superBundleKey;
  private NamespacedKey buttonKey;

  @Override
  public void onDisable() { }

  @Override
  public void onEnable() {
    superBundleKey = new NamespacedKey(this, "SUPER_BUNDLE");
    buttonKey = new NamespacedKey(this, "SUPER_BUNDLE_KEY");
    getServer().getPluginManager().registerEvents(new OnPlayerOpenSuperBundle(this, superBundleKey, buttonKey), this);
  }
}
