package com.kaelkirk.lib;

import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Color;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class TwitchChatClient implements WebSocket.Listener {
  
  private WebSocket socket;
  private String channelName;
  private Pattern pingPattern;
  private Pattern messagePattern;

  public TwitchChatClient(String channelName, WebSocket socket) {
    this.channelName = channelName;
    this.socket = socket;

    pingPattern = Pattern.compile("^PING :(.*)$", Pattern.MULTILINE);
    messagePattern = Pattern.compile("^:(\\w+)!.*PRIVMSG #kaelinator17 :(.*)$", Pattern.MULTILINE);
  }

  @Override
  public void onOpen(WebSocket webSocket) {
    System.out.println("Twitch chat client connected");
    WebSocket.Listener.super.onOpen(webSocket);
  }

  @Override
  public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
    String d = data.toString();

    Matcher pingMatch = pingPattern.matcher(d);
    if (pingMatch.find()) {
      // ping message
      if (pingMatch.groupCount() < 1) {

        System.out.println("Looked like ping, but too few groups: \"" + d + "\"");
        return WebSocket.Listener.super.onText(webSocket, data, last);
      }

      socket.sendText("PONG :" + pingMatch.group(1), true);

      return WebSocket.Listener.super.onText(webSocket, data, last);
    }
  
    Matcher messageMatch = messagePattern.matcher(d);
    if (messageMatch.find()) {

      if (messageMatch.groupCount() < 2) {
        System.out.println("Looked like message, but too few groups: \"" + d + "\"");
        return WebSocket.Listener.super.onText(webSocket, data, last);
      }

      String username = messageMatch.group(1);
      String message = messageMatch.group(2);

      Component serverMessage = Component.text("[").color(TextColor.color(Color.GRAY.asRGB()))
        .append(Component.text("T").color(TextColor.color(Color.PURPLE.asRGB())))
        .append(Component.text("] " + username + ": ").color(TextColor.color(Color.GRAY.asRGB())))
        .append(Component.text(message).color(TextColor.color(1f, 1f, 0.9f)));


      Bukkit.broadcast(serverMessage);

      return WebSocket.Listener.super.onText(webSocket, data, last);
    }

    System.out.println("Can't parse text: \"" + d + "\"");
    
    return WebSocket.Listener.super.onText(webSocket, data, last);
  }
  @Override
  public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
    System.out.println("Twitch chat client closed: " + statusCode + " " + reason);
    return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
  }

  @Override
  public void onError(WebSocket webSocket, Throwable error) {
    System.out.println("Twitch chat client error: " + error);
    WebSocket.Listener.super.onError(webSocket, error);
  }
}
