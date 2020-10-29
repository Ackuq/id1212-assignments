package com.kth;

import java.io.IOException;

public class ClientMain {
  public static void main(String[] args) throws IOException {
    String name = args.length > 0 ? args[0] : "No name";
    new ChatClient().startClient(name);
  }
}
