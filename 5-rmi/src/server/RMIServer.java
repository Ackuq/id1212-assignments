package server;

import java.io.InputStream;
import java.rmi.AlreadyBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;

import shared.Mail;

public class RMIServer implements Mail {

    static final int PORT = Registry.REGISTRY_PORT;
    static final String IMAP_HOST = "webmail.kth.se";
    static final int IMAP_PORT = 993;
    Session session;

    public void init() {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imap");
        properties.put("mail.imap.host", IMAP_HOST);
        properties.put("mail.imap.port", IMAP_PORT);
        properties.put("mail.imap.starttls.enable", "true");

        session = Session.getDefaultInstance(properties);
    }

    public String sendMail(String username, String password) throws Exception {
        Store store = session.getStore("imaps");

        store.connect(IMAP_HOST, username, password);
        Folder inbox = store.getFolder("Inbox");
        inbox.open(Folder.READ_ONLY);

        Message message = inbox.getMessage(inbox.getMessageCount());

        String parsedMessage = parsePart(message, new StringBuilder());

        // close the store and folder objects
        inbox.close(false);
        store.close();
        return parsedMessage;
    }

    public static String parsePart(Part part, StringBuilder sb) throws Exception {
        if (part instanceof Message) {
            // Parse the headers
            parseHeaders((Message) part, sb);
        }
        sb.append("----------------------------\n");
        // Check if just plain text
        if (part.isMimeType("text/plain")) {
            sb.append((String) part.getContent() + "\n");
        }
        // Check if attachments are found
        else if (part.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) part.getContent();
            int count = mp.getCount();
            for (int i = 0; i < count; i++) {
                parsePart(mp.getBodyPart(i), sb);
            }
        }
        // Check if nested message
        else if (part.isMimeType("message/rfc822")) {
            parsePart((Part) part.getContent(), sb);
        }
        // Check when type is unknown
        else {
            Object object = part.getContent();
            if (object instanceof String) {
                sb.append((String) object);
            } else if (object instanceof InputStream) {
                InputStream is = (InputStream) object;
                int character;
                while ((character = is.read()) != -1) {
                    sb.append(character);
                }
                sb.append('\n');
                is.close();
            } else {
                sb.append(object.toString());
            }
        }
        return sb.toString();
    }

    public static void parseHeaders(Message message, StringBuilder sb) throws Exception {
        sb.append("---------------------------\n");
        Address[] addresses;

        // Parse list of senders
        if ((addresses = message.getFrom()) != null) {
            String[] addressStrings = new String[addresses.length];
            for (int i = 0; i < addresses.length; i++) {
                addressStrings[i] = addresses[i].toString();
            }
            sb.append("From: " + String.join("delimiter", addressStrings) + '\n');
        }
        // Parse list of recipients
        if ((addresses = message.getRecipients(Message.RecipientType.TO)) != null) {
            String[] addressStrings = new String[addresses.length];
            for (int i = 0; i < addresses.length; i++) {
                addressStrings[i] = addresses[i].toString();
            }
            sb.append("To: " + String.join("delimiter", addressStrings) + '\n');
        }
        // Parse subject
        if (message.getSubject() != null) {
            sb.append("Subject: " + message.getSubject() + "\n");
        }

    }

    public static void main(String args[]) {
        try {
            RMIServer obj = new RMIServer();

            Mail stub = (Mail) UnicastRemoteObject.exportObject(obj, 0);
            stub.init();
            Registry registry = LocateRegistry.getRegistry(PORT);
            // Bind the remote object's stub in the registry
            try {
                registry.bind("Mail", stub);
            } catch (AlreadyBoundException e) {
                System.out.println("Mail class already bound...");
                registry.rebind("Mail", stub);
            }

            System.err.println(String.format("Server ready, connected to registry on port %d", PORT));
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
