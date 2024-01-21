package com.kaelkirk;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;

import org.bukkit.plugin.java.JavaPlugin;

import com.kaelkirk.lib.HeartbeatHandler;
import com.kaelkirk.lib.OAuthClient;
import com.kaelkirk.lib.TwitchChatClient;
import com.sun.net.httpserver.HttpServer;

public class TwitchIntegrationPlugin extends JavaPlugin {

  private WebSocket socket;
  private HttpServer endpoint;

  @Override
  public void onDisable() {
    if (socket != null) {
      socket.sendClose(0, "Plugin disabled");
    }
    if (endpoint != null) {
      endpoint.stop(0);
    }
  }

  @Override
  public void onEnable() {
    // Don't log enabling, Spigot does that for you automatically!

    // Commands enabled with following method must have entries in plugin.yml
    // getCommand("example").setExecutor(new ExampleCommand(this));
    try {
      endpoint = HttpServer.create(new InetSocketAddress(8080), 0);
    } catch (IOException e) {
      e.printStackTrace();
    }

    endpoint.createContext("/oauth_redirect", new OAuthClient(this));
    endpoint.createContext("/", new HeartbeatHandler());
    // ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor)Executors.newFixedThreadPool(1);
    endpoint.setExecutor(null);
    endpoint.start();

    System.out.println("Hello world from Twitch");
  }

  public void connectChatBot(String name, String accessToken) {

    socket = HttpClient
      .newHttpClient()
      .newWebSocketBuilder()
      .buildAsync(URI.create("wss://irc-ws.chat.twitch.tv:443"), new TwitchChatClient("kaelinator17", socket))
      .join();

    socket.sendText("PASS oauth:" + accessToken, true);
    socket.sendText("NICK " + name, true);
    socket.sendText("JOIN #kaelinator17", true);
  }
}
