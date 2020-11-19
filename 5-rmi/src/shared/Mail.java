package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Mail extends Remote {
    String sendMail() throws RemoteException;
}
