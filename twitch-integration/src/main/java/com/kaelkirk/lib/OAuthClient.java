package com.kaelkirk.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.plugin.Plugin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kaelkirk.TwitchIntegrationPlugin;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class OAuthClient implements HttpHandler {

  private TwitchIntegrationPlugin plugin;
  private String clientSecret;
  private String clientId;
  private String state;

  public OAuthClient(TwitchIntegrationPlugin plugin) {
    this.plugin = plugin;

    clientId = System.getenv("TWITCH_CLIENT_ID");
    clientSecret = System.getenv("TWITCH_CLIENT_SECRET");
    state = UUID.randomUUID().toString();

    String accessMessage = "Allow access at: "
      + "https://id.twitch.tv/oauth2/authorize"
      + "?client_id=" + clientId
      + "&redirect_uri=https%3A%2F%2Fapi.kael.dev%2Foauth_redirect"
      + "&scope=chat%3Aread"
      + "&response_type=code";

    System.out.println(accessMessage);
  }

  @Override
  public void handle(HttpExchange req) throws IOException {
    String queryParams = req.getRequestURI().getRawQuery();
    // System.out.println("New query: " + queryParams);
    HashMap<String, String> params = parseQueryParams(queryParams);

    String response;
    int status;
    if (!params.containsKey("code")) {

      response = "code query parameter not found";
      status = 400;
      req.sendResponseHeaders(status, response.getBytes().length);
      OutputStream output = req.getResponseBody();
      output.write(response.getBytes());
      output.close();
      return;
    }

    response = "Success :)";
    status = 200;

    Map<String, String> body = new HashMap<String, String>();

    body.put("client_id", clientId);
    body.put("client_secret", clientSecret);
    body.put("code", params.get("code"));
    body.put("grant_type", "authorization_code");
    body.put("redirect_uri", "https://api.kael.dev/oauth_redirect");

    String result = sendURLEncodedFormPost("https://id.twitch.tv/oauth2/token", body);
    JsonObject json = JsonParser.parseString(result).getAsJsonObject();
    String accessToken = json.get("access_token").getAsString();
    // String refreshToken = json.get("refresh_token").getAsString();

    plugin.connectChatBot("apadinator", accessToken);

    req.sendResponseHeaders(status, response.getBytes().length);
    OutputStream output = req.getResponseBody();
    output.write(response.getBytes());
    output.close();
  }

  private HashMap<String, String> parseQueryParams(String queryParams) throws UnsupportedEncodingException {
    HashMap<String, String> result = new HashMap<String, String>();
    for (String pair : queryParams.split("&")) {

      String[] splitPair = pair.split("=");
      if (splitPair.length != 2) {
        continue;
      }
      result.put(
        URLDecoder.decode(splitPair[0], "UTF-8"),
        URLDecoder.decode(splitPair[1], "UTF-8")
      );
    }
    return result;
  }

  private String sendURLEncodedFormPost(String url, Map<String, String> body) throws IOException {
    URLConnection con = new URL(url).openConnection();
    HttpURLConnection http = (HttpURLConnection) con;

    http.setRequestMethod("POST");
    http.setDoOutput(true);

    StringJoiner encodedBody = new StringJoiner("&");

    for (Map.Entry<String,String> entry : body.entrySet()) {
      encodedBody.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
    }

    byte[] out = encodedBody.toString().getBytes(StandardCharsets.UTF_8);
    int length = out.length;
    http.setFixedLengthStreamingMode(length);
    http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
    http.connect();
    OutputStream os = http.getOutputStream();
    os.write(out);

    BufferedReader br = (100 <= http.getResponseCode() && http.getResponseCode() <= 399)
      ? new BufferedReader(new InputStreamReader(http.getInputStream()))
      : new BufferedReader(new InputStreamReader(http.getErrorStream()));

    String responseBody = br.lines().collect(Collectors.joining());
    br.close();
    return responseBody;
  }
  
}
