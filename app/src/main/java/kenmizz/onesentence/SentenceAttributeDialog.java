package kenmizz.onesentence;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.appwidget.AppWidgetManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

public class SentenceAttributeDialog extends AppCompatActivity implements ColorPickerDialogListener {

    SharedPreferences sentenceAttrPreferences;
    SharedPreferences widgetPreferences;

    private static final String TAG = "SentenceAttribute";
    TextInputEditText SentenceAttributeSetenceEditText;
    TextView SentenceAttributeTextView;
    Slider SentenceAttributeSlider;
    Button SentenceAtrributeColorPicker;
    Button SentenceAtrributeConfirmButton;

    int widgetId;
    String sentence;
    Float textSize;
    int textColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentence_attribute_dialog);
        sentenceAttrPreferences = getSharedPreferences(MainActivity.SENATTR_PREFS, MODE_PRIVATE);
        widgetPreferences = getSharedPreferences(SentenceWidgetConfiguration.WIDGET_PREFS, MODE_PRIVATE);
        Bundle extras = getIntent().getExtras();
        widgetId = extras.getInt("widgetId");
        sentence = widgetPreferences.getString(widgetId + SentenceWidgetConfiguration.SENTENCE_TEXT, SentenceWidgetConfiguration.SENTENCE_TEXT);
        textSize = sentenceAttrPreferences.getFloat(widgetId + SentenceWidgetConfiguration.SENTENCE_TEXT + "textSize", 25);
        textColor = sentenceAttrPreferences.getInt(widgetId + SentenceWidgetConfiguration.SENTENCE_TEXT + "textColor", getColor(R.color.white));
        SentenceAttributeTextView = findViewById(R.id.SentenceAttributeTextView);
        SentenceAttributeSlider = findViewById(R.id.SentenceAttributeSlider);
        SentenceAtrributeColorPicker = findViewById(R.id.SentenceAttributeColorPicker);
        SentenceAtrributeConfirmButton = findViewById(R.id.SentenceAttributeConfirmButton);
        SentenceAttributeSetenceEditText = findViewById(R.id.SentenceAttributeEditText);
        setUpDialog();
    }

    public void setUpDialog() {
        Log.d(TAG, "Setting up dialog for widgetId: " + widgetId +"\nSentence: " + sentence +"\ntextSize: " + textSize +"\ntextColor: " + textColor + "\ntextColor(toHex): " + String.format("#%06X", (0xFFFFFF & textColor)) + "");
        SentenceAttributeSetenceEditText.setText(sentence);
        SentenceAttributeTextView.setText(sentence);
        SentenceAttributeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        SentenceAttributeTextView.setTextColor(textColor);
        SentenceAtrributeColorPicker.setBackgroundColor(textColor);
        SentenceAttributeSlider.setValue(textSize);
        SentenceAtrributeColorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialog.newBuilder()
                        .setColor(textColor)
                        .show(SentenceAttributeDialog.this);
            }
        });
        SentenceAttributeSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {

            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                SentenceAttributeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, slider.getValue());
                Log.d(TAG, "SliderValue: " + slider.getValue() +"");
            }
        });
        SentenceAttributeSetenceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                SentenceAttributeTextView.setText(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                SentenceAttributeTextView.setText(editable.toString());
            }
        });
        SentenceAtrributeConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
                RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.sentence_widget);
                views.setTextViewText(R.id.SentenceTextView, SentenceAttributeTextView.getText());
                views.setTextColor(R.id.SentenceTextView, SentenceAttributeTextView.getCurrentTextColor());
                views.setTextViewTextSize(R.id.SentenceTextView, TypedValue.COMPLEX_UNIT_SP, SentenceAttributeSlider.getValue());
                appWidgetManager.updateAppWidget(widgetId, views);
                SharedPreferences.Editor widgetEditor = widgetPreferences.edit();
                SharedPreferences.Editor attrEditor = sentenceAttrPreferences.edit();
                widgetEditor.putString(widgetId + SentenceWidgetConfiguration.SENTENCE_TEXT, SentenceAttributeTextView.getText().toString());
                attrEditor.putFloat(widgetId + SentenceWidgetConfiguration.SENTENCE_TEXT + "textSize", SentenceAttributeSlider.getValue());
                attrEditor.putInt(widgetId + SentenceWidgetConfiguration.SENTENCE_TEXT + "textColor", SentenceAttributeTextView.getCurrentTextColor());
                widgetEditor.apply();
                attrEditor.apply();
                Toast.makeText(getApplicationContext(), R.string.change_success, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "widgetId: " + widgetId +" textSize: " + SentenceAttributeSlider.getValue() + " textColor: " + SentenceAttributeTextView.getCurrentTextColor() +"");
                finish();
            }
        });
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        Button SentenceAtrributeColorPicker = findViewById(R.id.SentenceAttributeColorPicker);
        SentenceAtrributeColorPicker.setBackgroundColor(color);
        TextView SentenceAttributeTextView = findViewById(R.id.SentenceAttributeTextView);
        SentenceAttributeTextView.setTextColor(color);
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }
}