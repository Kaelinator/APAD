package com.kaelkirk.lib;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.UUID;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class OAuthClient implements HttpHandler {

  private TwitchChatClient chatClient;
  private TokenManager tokenManager;
  private String clientSecret;
  private String clientId;
  private String state;

  public OAuthClient(String clientId, String clientSecret, TwitchChatClient chatClient, TokenManager tokenManager) {
    this.chatClient = chatClient;
    this.tokenManager = tokenManager;

    this.clientId = clientId;
    this.clientSecret = clientSecret;
    state = UUID.randomUUID().toString();

    String accessMessage = "Allow access at: "
      + "https://id.twitch.tv/oauth2/authorize"
      + "?client_id=" + clientId
      + "&redirect_uri=https%3A%2F%2Fapi.kael.dev%2Foauth_redirect"
      + "&scope=chat%3Aread"
      + "&response_type=code"
      + "&state=" + state;

    System.out.println(accessMessage);
  }

  @Override
  public void handle(HttpExchange req) throws IOException {
    String queryParams = req.getRequestURI().getRawQuery();
    HashMap<String, String> params = parseQueryParams(queryParams);

    String response;
    int status;
    if (!params.containsKey("code") || !params.containsKey("state") || !params.get("state").equals(state)) {

      response = !params.containsKey("code")
                ? "Query parameter 'code' not found"
                : !params.containsKey("state")
                ? "Query parameter 'state' not found"
                : !params.get("state").equals(state)
                ? "Query parameter 'state' does not match"
                : "Unknown error has occurred";

      status = 400;

      req.sendResponseHeaders(status, response.getBytes().length);
      OutputStream output = req.getResponseBody();
      output.write(response.getBytes());
      output.close();
      return;
    }

    response = "Success :)";
    status = 200;

    tokenManager.fetchTokens(clientId, clientSecret, params.get("code"));
    chatClient.connect(tokenManager.getAccessToken(), "apadinator");

    state = UUID.randomUUID().toString();

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

  
}
