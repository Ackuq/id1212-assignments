package extra.app;

import java.io.InputStream;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Address;

public class EmailMessage {

    String subject;
    String[] to;
    String[] from;
    String content = "";

    EmailMessage(Message message) throws Exception {
        parsePart(message);
    }

    private void parsePart(Part part) throws Exception {
        if(part instanceof Message) {
            parseEnvelope((Message) part);
        }

        if(part.isMimeType("text/plain")) {
            content += (String) part.getContent();
        } else if(part.isMimeType("multipart/*")) {
            // If multipart
            Multipart multipart = (Multipart) part.getContent();
            int count = multipart.getCount();
            for (int i = 0; i < count; i++) {
                parsePart(multipart.getBodyPart(i));
            }
        } else if(part.isMimeType("message/rfc822")) {
            // Check if nested message
            parsePart((Part) part.getContent());
        } else {
            // Check other possibilities
            Object c = part.getContent();
            if(c instanceof String) {
                // Is string
                content += (String) c;
            } else if(c instanceof InputStream) {
                // Is input stream
                InputStream inputStream = (InputStream) c;
                int in;
                while((in = inputStream.read()) != -1) {
                    //noinspection StringConcatenationInLoop
                    content+= in;
                }
                inputStream.close();
            } else {
                // Is unknown
                content += c.toString();
            }
        }
    }

    private void parseEnvelope(Message message) throws Exception {
        Address[] addresses;

        // FROM
        if ((addresses = message.getFrom()) != null) {
            from = new String[addresses.length];
            for (int i = 0; i < addresses.length; i++) {
                from[i] = addresses[i].toString();
            }
        }

        // TO
        if ((addresses = message.getRecipients(Message.RecipientType.TO)) != null) {
            to = new String[addresses.length];
            for (int i = 0; i < addresses.length; i++) {
                to[i] = addresses[i].toString();
            }
        }

        // SUBJECT
        if (message.getSubject() != null) {
            this.subject = message.getSubject();
        }

    }
}
