package com.kth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ChatClient {
  private class MessageListener extends Thread {

    BufferedReader in;

    public MessageListener(Socket socket) throws IOException {
      this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
      String text = "";
      try {
        while ((text = this.in.readLine()) != null) {
          System.out.println(text);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  static int serverPort = 3000;

  public void startClient(String name) throws IOException {
    Socket socket = new Socket("localhost", serverPort);
    PrintStream out = new PrintStream(socket.getOutputStream());
    out.println(name);
    new MessageListener(socket).start();

    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    String text;
    while ((text = in.readLine()) != null) {
      out.println(text);
    }
    socket.close();
  }
}
