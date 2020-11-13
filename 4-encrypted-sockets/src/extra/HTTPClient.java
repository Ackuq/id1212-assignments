package extra;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPClient {
  static int attempts = 100;
  static String LOW = "That's too low. Please guess higher: ";
  static String HIGH = "That's too high. Please guess lower: ";
  static String CORRECT = "You made it in %d amount of guess(es): ";

  private static int makeRequest(String cookieHeader, int guess) throws Exception {
    HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:4000/?guess=" + guess)
        .openConnection();
    connection.setRequestProperty("Cookie", cookieHeader);

    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

    String text = "";

    while (!(text = in.readLine()).contains("<!DOCTYPE html>")) {
    }

    if (text.contains(LOW)) {
      return -1;
    } else if (text.contains(HIGH)) {
      return 1;
    } else {
      return 0;
    }
  }

  private static String getCookieHeader() throws Exception {
    HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:4000").openConnection();

    String cookieHeader = connection.getHeaderField("Set-Cookie");
    return cookieHeader;
  }

  public static void main(String[] args) throws Exception {

    int[] attemptResults = new int[attempts];
    for (int i = 0; i < attempts; i++) {
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
          attemptResults[i] = guesses;
          break;
        }
        guess = (int) min + ((max - min) / 2);
        guesses++;
      }
    }

    double sum = 0;

    for (int i = 0; i < attemptResults.length; i++) {
      sum += attemptResults[i];
    }

    System.out.println("Average amount of guesses: " + sum / attempts);
  }
}
