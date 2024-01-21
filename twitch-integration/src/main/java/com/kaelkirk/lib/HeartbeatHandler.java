package com.kaelkirk.lib;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


public class HeartbeatHandler implements HttpHandler {
  
  @Override
  public void handle(HttpExchange req) throws IOException {
    String response = "Hello, world!";
    req.sendResponseHeaders(200, response.getBytes().length);
    OutputStream output = req.getResponseBody();
    output.write(response.getBytes());
    output.close();
  }
}
