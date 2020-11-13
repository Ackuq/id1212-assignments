import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Base64;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SendMail {

  public static void main(String[] args) throws Exception {

    String host = "smtp.kth.se";
    Integer port = 587;
    Socket socket = new Socket(host, port);
    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    PrintStream out = new PrintStream(socket.getOutputStream());

    System.out.println(in.readLine());
    sendCommand(out, in, "EHLO " + host, new int[] { 250 });
    out.println("EHLO " + host);

    out.println("STARTTLS");
    sendCommand(out, in, "STARTTLS", new int[] { 220, 250 });
    read(in);

    SSLSocket sslSocket = (SSLSocket) ((SSLSocketFactory) SSLSocketFactory.getDefault()).createSocket(socket,
        socket.getInetAddress().getHostAddress(), socket.getPort(), true);

    sslSocket.setUseClientMode(true);
    sslSocket.setEnableSessionCreation(true);

    System.out.println("CLIENT: securing connection");
    sslSocket.startHandshake();
    System.out.println("CLIENT: secured");

    socket = sslSocket;
    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    out = new PrintStream(socket.getOutputStream());

    authorize(out, in, host);

    Console console = System.console();
    String mail = new String(console.readLine("Please enter your email: "));
    String subject = new String(console.readLine("Please enter your subject: "));
    String mailText = new String(console.readLine("Please enter your email text: "));

    sendMail(out, in, mail, subject, mailText);

  }

  public static void authorize(PrintStream out, BufferedReader in, String host) throws Exception {

    sendCommand(out, in, "EHLO " + host, new int[] { 250 });

    boolean loggedIn = false;
    while (!loggedIn) {
      try {
        sendCommand(out, in, "AUTH LOGIN", new int[] { 334 });
        Console console = System.console();
        String username = new String(console.readLine("Please enter your username: "));
        String encodedUsername = Base64.getEncoder().encodeToString(username.getBytes());
        sendCommand(out, in, encodedUsername, new int[] { 334 });

        String password = new String(console.readPassword("Please enter your password: "));
        String encodedPassword = Base64.getEncoder().encodeToString(password.getBytes());
        sendCommand(out, in, encodedPassword, new int[] { 235 });
        loggedIn = true;
      } catch (Exception e) {
        System.out.println("Wrong credentials");
      }
    }
  }

  public static void sendMail(PrintStream out, BufferedReader in, String mail, String subject, String text)
      throws Exception {

    String msgEnd = "\r\n.";

    sendCommand(out, in, "MAIL FROM: <" + mail + ">", new int[] { 250 });

    sendCommand(out, in, "RCPT TO: <" + mail + ">", new int[] { 250 });

    sendCommand(out, in, "DATA", new int[] { 354 });

    String mailBody = "From: <" + mail + ">\n";
    mailBody += "To: <" + mail + ">\n";
    mailBody += "Subject: " + subject + "\n\n";
    mailBody += text + msgEnd;
    sendCommand(out, in, mailBody, new int[] { 250 });

    sendCommand(out, in, "QUIT", new int[] { 221 });

  }

  public static void sendCommand(PrintStream out, BufferedReader in, String command, int[] expectedCodes)
      throws Exception {
    out.println(command);
    String response = in.readLine();
    System.out.println(response);
    int responseCode = Integer.parseInt(response.substring(0, 3));

    while ((response.length() > 3) && (response.charAt(3) == '-')) {
      response = in.readLine();
      System.out.println("SERVER: " + response);
    }

    for (int i = 0; i < expectedCodes.length; i++) {
      if (expectedCodes[i] == responseCode) {
        return;
      }
    }
    System.out.println("Unexpected response code received");
    throw new Exception("Invalid response code");
  }

  public static void read(BufferedReader in) throws Exception {
    String response;
    while ((response = in.readLine()) != null) {
      System.out.println(response);
      if (response.charAt(3) == ' ') {
        System.out.println("End of response");
        break;
      }
    }
  }

}
