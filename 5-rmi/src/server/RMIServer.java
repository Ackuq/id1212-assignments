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

        String s = writePart(message, new StringBuilder());

        // close the store and folder objects
        inbox.close(false);
        store.close();
        return s;
    }

    /*
     * This method checks for content-type based on which, it processes and fetches
     * the content of the message
     */
    public static String writePart(Part p, StringBuilder sb) throws Exception {
        if (p instanceof Message)
            // Call methos writeEnvelope
            writeEnvelope((Message) p, sb);

        sb.append("----------------------------\n");
        sb.append("CONTENT-TYPE: " + p.getContentType() + "\n");

        // check if the content is plain text
        if (p.isMimeType("text/plain")) {
            sb.append((String) p.getContent() + "\n");
        }
        // check if the content has attachment
        else if (p.isMimeType("multipart/*")) {
            sb.append("This is a Multipart\n");
            sb.append("---------------------------\n");
            Multipart mp = (Multipart) p.getContent();
            int count = mp.getCount();
            for (int i = 0; i < count; i++)
                writePart(mp.getBodyPart(i), sb);
        }
        // check if the content is a nested message
        else if (p.isMimeType("message/rfc822")) {
            sb.append("This is a Nested Message\n");
            sb.append("---------------------------\n");
            writePart((Part) p.getContent(), sb);
        }
        // check if the content is an inline image
        else {
            Object o = p.getContent();
            if (o instanceof String) {
                sb.append("This is a string\n");
                sb.append("---------------------------\n");
                sb.append((String) o);
            } else if (o instanceof InputStream) {
                sb.append("This is just an input stream\n");
                sb.append("---------------------------\n");
                InputStream is = (InputStream) o;
                is = (InputStream) o;
                int c;
                while ((c = is.read()) != -1)
                    System.out.write(c);
            } else {
                sb.append("This is an unknown type\n");
                sb.append("---------------------------\n");
                sb.append(o.toString());
            }
        }
        return sb.toString();
    }

    /*
     * This method would print FROM,TO and SUBJECT of the message
     */
    public static void writeEnvelope(Message m, StringBuilder sb) throws Exception {
        sb.append("This is the message envelope\n");
        sb.append("---------------------------\n");
        Address[] a;

        // FROM
        if ((a = m.getFrom()) != null) {
            for (int j = 0; j < a.length; j++)
                sb.append("FROM: " + a[j].toString() + "\n");
        }

        // TO
        if ((a = m.getRecipients(Message.RecipientType.TO)) != null) {
            for (int j = 0; j < a.length; j++)
                sb.append("TO: " + a[j].toString() + "\n");
        }

        // SUBJECT
        if (m.getSubject() != null)
            sb.append("SUBJECT: " + m.getSubject() + "\n");

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
