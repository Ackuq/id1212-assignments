package server;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import shared.Mail;

public class RMIServer implements Mail {

    public String sendMail() {
        return "Mail";
    }

    public static void main(String args[]) {
        try {
            RMIServer obj = new RMIServer();

            Mail stub = (Mail) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("Mail", stub);

            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
