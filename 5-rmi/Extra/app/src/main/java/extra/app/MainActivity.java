package extra.app;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Properties;



public class MainActivity extends AppCompatActivity {
    private static final String PROTOCOL = "imap";
    public static final String IMAP_HOST = "webmail.kth.se";
    private static final int IMAP_PORT = 993;


    Properties properties;
    TextView usernameView;
    TextView passwordView;

    TextView subjectView;
    TextView toView;
    TextView fromView;
    TextView contentView;
    TextView errorView;

    public void getMail(View _view) {
        new EmailHandler(MainActivity.this).start();
    }

    private void initMail() {
        properties = new Properties();
        properties.put("mail.store.protocol", PROTOCOL);
        properties.put("mail.imap.host", IMAP_HOST);
        properties.put("mail.imap.port", IMAP_PORT);
        properties.put("mail.imap.starttls.enable", "true");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initMail();
        setContentView(R.layout.activity_main);
        usernameView = findViewById(R.id.username);
        passwordView = findViewById(R.id.password);

        subjectView = findViewById(R.id.subject);
        toView = findViewById(R.id.to);
        fromView = findViewById(R.id.from);
        contentView = findViewById(R.id.content);
        errorView = findViewById(R.id.error);
    }
}