package com.kth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer {
  private class HandleClient extends Thread {

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
          System.out.println(String.format("Received message %s", text));
          this.server.broadcast(this.client, text);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public class Client {
    PrintStream out;
    BufferedReader in;
    Socket socket;
    String name;

    public Client(Socket socket) throws IOException {
      this.socket = socket;
      this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      this.out = new PrintStream(socket.getOutputStream());

      this.name = this.in.readLine();
    }

  }

  private static int port = 3000;
  public ArrayList<Client> clients = new ArrayList<Client>();

  public void broadcast(Client client, String message) {
    this.clients.forEach((c) -> {
      if (client != c) {
        c.out.println(String.format("%s says: %s", client.name, message));
      }
    });
  }

  public void startServer() throws IOException {
    ServerSocket serverSocket = new ServerSocket(port);
    System.out.println(String.format("Listening on port %d", port));
    Socket socket = null;

    while ((socket = serverSocket.accept()) != null) {
      Client client = new Client(socket);
      this.clients.add(client);
      new HandleClient(client, this).start();
    }

    serverSocket.close();
  }
}
