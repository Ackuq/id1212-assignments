package extra.app;

import android.annotation.SuppressLint;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;

public class EmailHandler extends Thread {

    private final MainActivity activity;

    // Print parsed message
    private class PrintSuccess implements Runnable {
        private final EmailMessage message;

        PrintSuccess(EmailMessage message) {
            this.message = message;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void run() {
            activity.subjectView.setText("Subject: " + message.subject);
            activity.toView.setText("To: " + String.join(", ", message.to) );
            activity.fromView.setText("From: " + String.join(", ", message.from));
            activity.contentView.setText(message.content);
        }
    }

    // Print error message
    private class PrintError implements Runnable {
        private final String message;

        PrintError(String message) {
            this.message = message;
        }

        @Override
        public void run() {
            activity.errorView.setText(message);
        }
    }

    EmailHandler(MainActivity mainActivity) {
        super();
        this.activity = mainActivity;
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Override
    public void run() {
        try {
            // Clear error if present
            if(!activity.errorView.getText().equals("")) {
                activity.runOnUiThread(new PrintError(""));
            }

            String username = activity.usernameView.getText().toString();
            String password = activity.passwordView.getText().toString();

            if(username.equals("")) {
                activity.runOnUiThread(new PrintError("Please enter username"));
                return;
            }

            if(password.equals("")) {
                activity.runOnUiThread(new PrintError("Please enter password"));
                return;
            }

            Session session = Session.getDefaultInstance(activity.properties);
            Store store = session.getStore("imaps");
            try {
                store.connect(MainActivity.IMAP_HOST, username, password);
            } catch (Exception _e) {
                activity.runOnUiThread(new PrintError("Wrong credentials"));
                return;
            }

            Folder inbox = store.getFolder("Inbox");
            inbox.open(Folder.READ_ONLY);

            // Get latest message
            Message message = inbox.getMessage(inbox.getMessageCount());

            EmailMessage parsedMessage = new EmailMessage(message);

            inbox.close(false);
            store.close();
            activity.runOnUiThread(new PrintSuccess(parsedMessage));

        } catch (Exception e) {
            e.printStackTrace();
            activity.runOnUiThread(new PrintError("Something went wrong"));
        }
    }
}
