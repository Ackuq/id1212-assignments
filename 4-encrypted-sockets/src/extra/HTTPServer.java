package extra;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Hashtable;
import java.util.UUID;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

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

  private final static int port = 443;
  // Trust store constants
  private final static String TRUST_STORE_NAME = "servercert.p12";
  private final static char[] TRUST_STORE_PASSWORD = "abc123".toCharArray();
  // Key store constants
  private final static String KEY_STORE_NAME = "servercert.p12";
  private final static char[] KEY_STORE_PASSWORD = "abc123".toCharArray();

  // Reply strings
  public final static String START_MESSAGE = "Guess a number between 1 and 100: ";
  public final static String TOO_HIGH_MESSAGE = "That's too high. Please guess lower: ";
  public final static String TOO_LOW_MESSAGE = "That's too low. Please guess higher: ";

  private final static String successMessage(int attempts) {
    return String.format("You made it in %d amount of guess(es): ", attempts);
  }

  // The context is shared between client and server for easier testing
  public static SSLContext setupContext() throws Exception {
    // Get trust store
    KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
    InputStream trustStoreReader = HTTPServer.class.getResourceAsStream("/" + TRUST_STORE_NAME);
    trustStore.load(trustStoreReader, TRUST_STORE_PASSWORD);
    trustStoreReader.close();
    // Create trust store manager and attach trust store to the manager
    TrustManagerFactory trustManagerFactory = TrustManagerFactory
        .getInstance(TrustManagerFactory.getDefaultAlgorithm());
    trustManagerFactory.init(trustStore);

    // Get key store
    KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
    InputStream keyStoreReader = HTTPServer.class.getResourceAsStream("/" + KEY_STORE_NAME);
    keyStore.load(keyStoreReader, KEY_STORE_PASSWORD);
    // Create key store manager and attach key store to the manager
    KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    keyManagerFactory.init(keyStore, KEY_STORE_PASSWORD);

    // Create TLS context and attach the managers to it
    SSLContext context = SSLContext.getInstance("TLS");
    context.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(),
        SecureRandom.getInstanceStrong());

    return context;
  }

  public static void main(String[] args) throws IOException, Exception {
    SSLContext context = setupContext();

    // Create server socket
    SSLServerSocketFactory factory = context.getServerSocketFactory();
    SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(port);
    System.out.println("Server listening on port " + port);

    // Connection socket
    SSLSocket socket = null;
    // Create empty storage
    Hashtable<String, Guesser> storage = new Hashtable<String, Guesser>();
    while ((socket = (SSLSocket) serverSocket.accept()) != null) {
      try {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        String cookieHeader = "";
        Guesser currentClient;
        String cookie = "";
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
            storage.remove(currentClient.getId());
            sendHTML(out, cookieHeader, successMessage(currentClient.getGuesses()), true);
          } else if (cmp > 0) {
            sendHTML(out, cookieHeader, TOO_HIGH_MESSAGE, false);
          } else {
            sendHTML(out, cookieHeader, TOO_LOW_MESSAGE, false);
          }
        } else {
          sendHTML(out, cookieHeader, START_MESSAGE, false);
        }

        out.close();
        in.close();
      } catch (Exception e) {
        System.out.println(e);
      }

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
