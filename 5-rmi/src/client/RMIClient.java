package client;

import java.io.Console;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.mail.AuthenticationFailedException;

import shared.Mail;

public class RMIClient {
    static final int PORT = Registry.REGISTRY_PORT;

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(PORT);
            Mail stub = (Mail) registry.lookup("Mail");

            Console console = System.console();

            while (true) {
                try {
                    String username = new String(console.readLine("Please enter your username: "));
                    String password = new String(console.readPassword("Please enter your password: "));
                    String message = stub.sendMail(username, password);
                    System.out.println(message);
                    break;
                } catch (AuthenticationFailedException _e) {
                    System.out.println("Authentication failed, please try again");
                }
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
