package com.kth;

import java.io.IOException;

public class ServerMain {
  public static void main(String[] args) throws IOException {
    new ChatServer().startServer();
  }
}
