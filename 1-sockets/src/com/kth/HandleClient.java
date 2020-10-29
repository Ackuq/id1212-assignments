package com.kth;

import java.io.IOException;

import com.kth.ChatServer.Client;

public class HandleClient extends Thread {

  ChatServer server;
  Client client;

  public HandleClient(Client client, ChatServer server) {
    this.client = client;
    this.server = server;
  }

  @Override
  public void run() {
    String text = "";
    try {
      while ((text = this.client.in.readLine()) != null) {
        System.out.println(String.format("Received message %", text));
        this.server.broadcast(this.client, text);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
