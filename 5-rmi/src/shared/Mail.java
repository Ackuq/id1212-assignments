package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Mail extends Remote {
    void init() throws RemoteException;

    String sendMail(String username, String password) throws Exception;
}
