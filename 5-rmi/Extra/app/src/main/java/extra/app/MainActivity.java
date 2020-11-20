package extra.app;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView usernameView;
    TextView passwordView;

    public void getMail(View view) {
        String username = usernameView.getText().toString();
        String password = passwordView.getText().toString();

        System.out.println(username);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usernameView = findViewById(R.id.username);
        passwordView = findViewById(R.id.password);
    }
}