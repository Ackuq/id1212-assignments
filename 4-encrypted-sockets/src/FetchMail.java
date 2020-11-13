import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class FetchMail {

  final static String LOGIN_TAG = "A01";
  final static String SELECT_TAG = "A02";
  final static String FETCH_TAG = "A03";

  final static String IMAP_SUCCES = "OK";

  final static String INBOX = "INBOX";

  public static void main(String[] args) throws Exception {
    String host = "webmail.kth.se";
    Integer port = 993;
    SocketFactory sslsocketfactory = SSLSocketFactory.getDefault();
    SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(host, port);
    BufferedReader in = new BufferedReader(new InputStreamReader(sslsocket.getInputStream()));
    PrintStream out = new PrintStream(sslsocket.getOutputStream());

    Console console = System.console();

    logIn(in, out, console);

    int latest = listInbox(in, out, console);

    fetchMessage(in, out, console, latest);

  }

  public static void logIn(BufferedReader in, PrintStream out, Console console) throws IOException {
    boolean loggedIn = false;

    while (!loggedIn) {
      String username = new String(console.readLine("Please enter your username: "));
      String password = new String(console.readPassword("Please enter your password: "));

      out.println(LOGIN_TAG + " LOGIN " + username + " " + password);
      out.flush();

      String response;
      while ((response = in.readLine()) != null) {

        String[] split = response.split(" ");

        if (split[0].equals(LOGIN_TAG)) {
          if (split[1].equals(IMAP_SUCCES)) {
            loggedIn = true;
            System.out.println("Login successful");
          } else {
            System.out.println("Wrong credentials");
          }
          break;
        }
      }
    }
  }

  public static int listInbox(BufferedReader in, PrintStream out, Console console) throws IOException {

    int latest = 0;

    out.println(SELECT_TAG + " SELECT " + INBOX);
    out.flush();

    String response;
    while ((response = in.readLine()) != null) {

      System.out.println(response);

      String[] split = response.split(" ");

      if (split.length >= 3 && split[2].equals("EXISTS")) {
        latest = Integer.parseInt(split[1]);
      }

      if (split[0].equals(SELECT_TAG)) {
        break;
      }
    }
    return latest;
  }

  public static void fetchMessage(BufferedReader in, PrintStream out, Console console, int messageNumber)
      throws IOException {

    System.out.println("Fetching last received mail:\n");

    String request = String.format("%s FETCH %d:%d (BODY.PEEK[HEADER.FIELDS (FROM Subject Date)] BODY.PEEK[TEXT])",
        FETCH_TAG, messageNumber, messageNumber);
    out.println(request);
    out.flush();

    String response = in.readLine();
    while ((response = in.readLine()) != null) {

      String[] split = response.split(" ");

      if (split.length > 0 && split[0].equals(FETCH_TAG)) {
        break;
      }

      if (!response.contains("BODY[TEXT]")) {
        System.out.println(response);
      }

    }
  }

}
