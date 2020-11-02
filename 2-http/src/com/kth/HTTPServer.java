package com.kth;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.UUID;

public class HTTPServer {

  private static void sendHTML(DataOutputStream out, String cookieHeader, String reply) throws IOException {
    String HTTPHeader = "HTTP/1.1 200 OK";
    String contentType = "Content-Type: text/html";
    String html = String.format(
        "<!DOCTYPE html><html><body><form method='GET'><span>%s</span><input name='guess' type='number'/></form></body></html>",
        reply);
    out.writeBytes(HTTPHeader + "\r\n" + contentType + "\r\n" + cookieHeader + "\r\n\r\n" + html);
  }

  public static void main(String[] args) throws IOException {
    String cookieHeader = "";
    int port = 4000;
    ServerSocket serverSocket = new ServerSocket(port);
    Socket socket = null;
    String text = "";

    Hashtable<String, Guesser> storage = new Hashtable<String, Guesser>();

    while ((socket = serverSocket.accept()) != null) {
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      DataOutputStream out = new DataOutputStream(socket.getOutputStream());

      Guesser currentClient;
      String cookie = "";
      String reply = "";
      String guess = null;

      while ((text = in.readLine()) != null && !text.isEmpty()) {
        if (!text.startsWith("GET")) {
          break;
        } else if (text.contains("?guess=")) {
          guess = text.split("/?guess=")[1].split(" ")[0];
        }

        if (text.startsWith("Cookie")) {
          cookie = parseCookieId(text);
        }
      }

      if (cookie.equals("") || !storage.containsKey(cookie)) {
        String id = UUID.randomUUID().toString();
        currentClient = new Guesser(id);
        storage.put(id, currentClient);
        cookieHeader = "Set-Cookie: id=" + id;
      } else {
        currentClient = storage.get(cookie);
      }
      System.out.println(guess);
      if (guess != null) {
        System.out.println("Test");
        int cmp = currentClient.guess(Integer.parseInt(guess));
        if (cmp < 0) {
          reply = "That's too low. Please guess higher";
        } else if (cmp > 0) {
          reply = "That's too high. Please guess low";
        } else {
          reply = String.format("You made it in %d amount of guess(es)", currentClient.getGuesses());
          storage.remove(currentClient.getId());
        }
      }

      sendHTML(out, cookieHeader, reply);

      out.close();
      in.close();
    }
    serverSocket.close();
  }

  public static String parseCookieId(String cookieLine) {

    String request = cookieLine.split(" ", 2)[1];
    String[] cookies = request.split("; ", 0);

    for (int i = 0; i < cookies.length; i++) {
      if (cookies[i].startsWith("id=")) {
        return cookies[i].split("=")[1];
      }
    }
    return "";

  }

}
