package kenmizz.onesentence;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MainActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_main);
        FloatingActionButton addButton = (FloatingActionButton)findViewById(R.id.floatingActionButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddMessage(v);
            }
        });
    }


    public void AddMessage(View v) {
        Intent intent = new Intent(this, AddMessageActivity.class);
        intent.putExtra("text", R.string.app_name);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.option) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.about)
                    .setMessage(R.string.about_info)
                    .setPositiveButton(R.string.confirmButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //pass
                        }
                    })
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
