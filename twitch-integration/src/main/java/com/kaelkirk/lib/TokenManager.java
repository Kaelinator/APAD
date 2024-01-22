package com.kaelkirk.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TokenManager {

  final private static String TWITCH_ACCESS_TOKEN = "TWITCH_ACCESS_TOKEN";
  final private static String TWITCH_REFRESH_TOKEN = "TWITCH_REFRESH_TOKEN";
  private String accessToken;
  private String refreshToken;

  public TokenManager() {
    accessToken = System.getProperty(TWITCH_ACCESS_TOKEN);
    refreshToken = System.getProperty(TWITCH_REFRESH_TOKEN);
  }
  
  public boolean hasAccessToken() {
    return accessToken != null;
  }

  public void setTokens(String accessToken, String refreshToken) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    System.setProperty(TWITCH_ACCESS_TOKEN, accessToken);
    System.setProperty(TWITCH_REFRESH_TOKEN, refreshToken);
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void fetchTokens(String clientId, String clientSecret, String oauthCode) throws IOException {

    Map<String, String> body = new HashMap<String, String>();

    body.put("client_id", clientId);
    body.put("client_secret", clientSecret);
    body.put("code", oauthCode);
    body.put("grant_type", "authorization_code");
    body.put("redirect_uri", "https://api.kael.dev/oauth_redirect");

    String result = sendURLEncodedFormPost("https://id.twitch.tv/oauth2/token", body);
    JsonObject json = JsonParser.parseString(result).getAsJsonObject();
    String accessToken = json.get("access_token").getAsString();
    String refreshToken = json.get("refresh_token").getAsString();

    setTokens(accessToken, refreshToken);
  }

  public void refreshTokens(String clientId, String clientSecret) throws IOException {

    Map<String, String> body = new HashMap<String, String>();

    body.put("client_id", clientId);
    body.put("client_secret", clientSecret);
    body.put("refresh_token", refreshToken);
    body.put("grant_type", "refresh_token");

    String result = sendURLEncodedFormPost("https://id.twitch.tv/oauth2/token", body);
    JsonObject json = JsonParser.parseString(result).getAsJsonObject();
    String accessToken = json.get("access_token").getAsString();
    String refreshToken = json.get("refresh_token").getAsString();

    setTokens(accessToken, refreshToken);
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
