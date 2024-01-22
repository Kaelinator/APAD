package com.kaelkirk;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.bukkit.plugin.java.JavaPlugin;

import com.kaelkirk.lib.HeartbeatHandler;
import com.kaelkirk.lib.OAuthClient;
import com.kaelkirk.lib.TokenManager;
import com.kaelkirk.lib.TwitchChatClient;
import com.sun.net.httpserver.HttpServer;

public class TwitchIntegrationPlugin extends JavaPlugin {

  private HttpServer endpoint;
  private TwitchChatClient chatClient; 

  @Override
  public void onDisable() {
    chatClient.close(0, "Plugin disabled");
    if (endpoint != null) {
      endpoint.stop(0);
    }
  }

  @Override
  public void onEnable() {
    String clientId = System.getenv("TWITCH_CLIENT_ID");
    String clientSecret = System.getenv("TWITCH_CLIENT_SECRET");

    this.saveDefaultConfig();
    String twitchChannelNameToJoin = this.getConfig().getString("twitchChannelNameToJoin");
    String twitchBotName = this.getConfig().getString("twitchBotName");

    TokenManager manager = new TokenManager();
    chatClient = new TwitchChatClient(twitchChannelNameToJoin);

    if (manager.hasAccessToken()) {
      try {
        manager.refreshTokens(clientId, clientSecret);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    if (manager.hasAccessToken()) {
      // no need to go through oauth process, access token already exists and has been refreshed
      chatClient.connect(manager.getAccessToken(), twitchBotName);
    } else {
      // need to go through oauth flow
      try {

        endpoint = HttpServer.create(new InetSocketAddress(8080), 0);
        endpoint.createContext("/oauth_redirect", new OAuthClient(clientId, clientSecret, chatClient, manager));
        endpoint.createContext("/", new HeartbeatHandler());
        // ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor)Executors.newFixedThreadPool(1);
        endpoint.setExecutor(null);
        endpoint.start();

      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    // Commands enabled with following method must have entries in plugin.yml
    // getCommand("example").setExecutor(new ExampleCommand(this));

    System.out.println("Hello world from Twitch");
  }

}
