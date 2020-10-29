package com.kth;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Random;
import java.util.UUID;


public class HTTPServer {

  public static class Client {
    public int goal;
    public int guesses;

    public Client(){
      Random random = new Random();
      this.goal = random.nextInt(100) + 1;
      this.guesses = 0;
    }

    public void incrementGuesses(){
      this.guesses++;
    }
  }

  public static void main(String[] args) throws IOException{


    String html = "<html><body><form method='GET'><input name='guess' type='number'/></form></body></html>";
    String HTTPHeader = "HTTP/1.1 200 OK\n";
    String cookieHeader = "";
    int port = 4000;
    ServerSocket serverSocket = new ServerSocket(port);
    Socket socket = null;;
    String text = "";
    int j = 0;

    Hashtable<String, Client> storage = new Hashtable<String, Client>();
    

    while((socket = serverSocket.accept()) != null){
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      DataOutputStream out = new DataOutputStream(socket.getOutputStream());
      
      Client currentClient;
      String cookie = "";
      while( (text = in.readLine()) != null && !text.isEmpty()){

        if(text.startsWith("GET") && text.contains("?guess=")){
          String guess = text.split("?guess=")[1];
        }

        if(text.startsWith("Cookie")){
          cookie = parseCookieId(text); 
        }
      }
      
      if(cookie.equals("") || !storage.containsKey(cookie)){
        String id = UUID.randomUUID().toString();
        currentClient = new Client();
        storage.put(id, currentClient);
        cookieHeader = "Set-Cookie: id=" + id + "\n";
      }
      else{
        currentClient = storage.get(cookie);
      }

      out.writeBytes(HTTPHeader + cookieHeader + "\r\n\r\n" + html);
      
      out.close();
      in.close();
    }
  }


  public static String parseCookieId(String cookieLine){
    
    String request = cookieLine.split(" ", 2)[1];
    String[] cookies = request.split("; ", 0);
    
    for(int i = 0; i < cookies.length; i++){
      if(cookies[i].startsWith("id=")){
        return cookies[i].split("=")[1];
      }
    }
    return "";

  }

  

}