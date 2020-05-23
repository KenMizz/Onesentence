package kenmizz.onesentence;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.content.res.Configuration;
import android.os.Bundle;
import android.content.Intent;
import android.widget.EditText;
import android.view.View;
import android.widget.TextView;

public class AddMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int DarkMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch(DarkMode) {
            case Configuration.UI_MODE_NIGHT_YES:
                setTheme(R.style.AppThemeDark);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                setTheme(R.style.AppTheme);

        }
        setContentView(R.layout.activity_add_message);
        Intent intent = getIntent();
        TextView text = findViewById(R.id.textView);
        String data = intent.getStringExtra("text");
        text.setText(data);
    }
}
