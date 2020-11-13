package extra;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

public class HTTPClient {
  private static final int attempts = 100;
  private static final String PROTOCOL = "https";

  private static final String HOST = PROTOCOL + "://127.0.0.1";

  private static int makeRequest(String cookieHeader, int guess) throws Exception {
    URL url = new URL(HOST + "/?guess=" + guess);
    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
    connection.setRequestProperty("Cookie", cookieHeader);

    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

    String text = "";

    while (!(text = in.readLine()).contains("<!DOCTYPE html>")) {
    }

    if (text.contains(HTTPServer.TOO_LOW_MESSAGE)) {
      return -1;
    } else if (text.contains(HTTPServer.TOO_HIGH_MESSAGE)) {
      return 1;
    } else {
      return 0;
    }
  }

  private static String getCookieHeader() throws Exception {
    URL url = new URL(HOST);
    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
    String cookieHeader = connection.getHeaderField("Set-Cookie");
    return cookieHeader;
  }

  private static int playGame() throws Exception {
    String cookieHeader = getCookieHeader();
    int guesses = 1;
    int min = 0;
    int max = 100;
    int guess = 50;
    while (true) {
      int res = makeRequest(cookieHeader, guess);
      if (res < 0) {
        min = guess + 1;
      } else if (res > 0) {
        max = guess - 1;
      } else {
        return guesses;
      }
      guess = (int) min + ((max - min) / 2);
      guesses++;
    }
  }

  public static void main(String[] args) throws Exception {
    // Use the same context as server, easier for testing
    SSLContext context = HTTPServer.setupContext();
    SSLSocketFactory factory = (SSLSocketFactory) context.getSocketFactory();
    // Apply the socket factory to each HTTPS connection
    HttpsURLConnection.setDefaultSSLSocketFactory(factory);

    int[] attemptResults = new int[attempts];
    for (int i = 0; i < attempts; i++) {
      int guesses = playGame();
      attemptResults[i] = guesses;
    }

    double sum = 0;

    for (int i = 0; i < attemptResults.length; i++) {
      sum += attemptResults[i];
    }

    System.out.println("Average amount of guesses: " + sum / attempts);
  }
}
