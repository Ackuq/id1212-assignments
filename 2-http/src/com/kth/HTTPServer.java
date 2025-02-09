package com.kth;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.UUID;

public class HTTPServer {

  private static void sendHTML(DataOutputStream out, String cookieHeader, String reply, boolean restart)
      throws IOException {
    String HTTPHeader = "HTTP/1.1 200 OK";
    String contentType = "Content-Type: text/html";
    String inputTag = restart ? "<input type='submit' value='restart'/>"
        : "<input name='guess' type='number' min='1' max='100'/>";
    String html = String.format("<!DOCTYPE html><html><body><form method='GET'><span>%s</span>%s</form></body></html>",
        reply, inputTag);
    out.writeBytes(HTTPHeader + "\r\n" + contentType + "\r\n" + cookieHeader + "\r\n\r\n" + html);
  }

  public static void main(String[] args) throws IOException {
    int port = 4000;
    ServerSocket serverSocket = new ServerSocket(port);
    Socket socket = null;
    System.out.println("Server listening on port " + port);
    Hashtable<String, Guesser> storage = new Hashtable<String, Guesser>();

    while ((socket = serverSocket.accept()) != null) {
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      DataOutputStream out = new DataOutputStream(socket.getOutputStream());

      String cookieHeader = "";
      Guesser currentClient;
      String cookie = "";
      String reply = "";
      String guess = null;
      String text = in.readLine();

      if (text.contains("?guess=")) {
        guess = text.split("/?guess=")[1].split(" ")[0];
      }

      while ((text = in.readLine()) != null && !text.isEmpty()) {
        if (text.startsWith("Cookie")) {
          cookie = parseCookieId(text).trim();
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

      if (guess != null && !guess.isEmpty()) {
        int cmp = currentClient.guess(Integer.parseInt(guess));
        if (cmp == 0) {
          reply = String.format("You made it in %d amount of guess(es): ", currentClient.getGuesses());
          storage.remove(currentClient.getId());
          sendHTML(out, cookieHeader, reply, true);
        } else if (cmp > 0) {
          reply = "That's too high. Please guess lower: ";
          sendHTML(out, cookieHeader, reply, false);
        } else {
          reply = "That's too low. Please guess higher: ";
          sendHTML(out, cookieHeader, reply, false);
        }
      } else {
        reply = "Guess a number between 1 and 100: ";
        sendHTML(out, cookieHeader, reply, false);
      }

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
