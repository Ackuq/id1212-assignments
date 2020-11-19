package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import shared.Mail;

public class RMIClient {
    static String host = "localhost";

    public static void main(String[] args) {

        String host = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            Mail stub = (Mail) registry.lookup("Hello");
            String response = stub.sendMail();
            System.out.println("response: " + response);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
