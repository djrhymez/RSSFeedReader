package com.example.dan.assignment7;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

public class settingsActivity extends AppCompatActivity {
    private ToggleButton nightModeToggle;
    private TextView txtNightMode;
    private Spinner fontSpinner;
    private TextView txtFont;
    private View settingsView;
    private View rootView;
    private String slateGrey = "#383838";
    private String white = "#ffffff";
    private SharedPreferences settingsPrefs;
    private Spinner spinnerFeed;
    private TextView txtFeed;

    @Override
    protected void onResume() {
        super.onResume();

        String backgroundColor = settingsPrefs.getString("backgroundColor", white);
        String textColor = settingsPrefs.getString("textColor", slateGrey);
        Boolean nightMode = settingsPrefs.getBoolean("nightMode", false);
        int fontSizeInt = settingsPrefs.getInt("fontSize", 14);
        String defaultFeedURL = settingsPrefs.getString("defaultFeed", "https://www.winnipegfreepress.com/rss/?path=%2Fsports%2Fhockey%2Fjets");


        settingsView = (View) findViewById(R.id.settingsView);
        rootView = settingsView.getRootView();

        rootView.setBackgroundColor(Color.parseColor(backgroundColor));

        txtNightMode = (TextView)findViewById(R.id.txtNightMode);
        txtNightMode.setTextColor(Color.parseColor(textColor));

        nightModeToggle = (ToggleButton) findViewById(R.id.tbNightMode);
        nightModeToggle.setChecked(nightMode);

        txtFont = (TextView) findViewById(R.id.txtFontSize);
        txtFont.setTextColor(Color.parseColor(textColor));

        txtFeed = (TextView) findViewById(R.id.txtFeed);
        txtFeed.setTextColor(Color.parseColor(textColor));

        fontSpinner = (Spinner) findViewById(R.id.spinnerFont);

        switch (fontSizeInt) {
            case 14:
                fontSpinner.setSelection(0);
                break;
            case 16:
                fontSpinner.setSelection(1);
                break;
            case 18:
                fontSpinner.setSelection(2);
                break;
        }

        spinnerFeed = (Spinner) findViewById(R.id.spinnerFeed);

        switch (defaultFeedURL) {
            case "https://www.winnipegfreepress.com/rss/?path=%2Fsports%2Fhockey%2Fjets":
                spinnerFeed.setSelection(0);
                break;
            case "https://www.winnipegfreepress.com/rss/?path=%2Fsports%2Ffootball%2Fbombers":
                spinnerFeed.setSelection(1);
                break;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        nightModeToggle = (ToggleButton) findViewById(R.id.tbNightMode);
        txtNightMode = (TextView)findViewById(R.id.txtNightMode);
        txtFont = (TextView) findViewById(R.id.txtFontSize);
        txtFeed = (TextView) findViewById(R.id.txtFeed);
        fontSpinner = (Spinner) findViewById(R.id.spinnerFont);
        spinnerFeed = (Spinner) findViewById(R.id.spinnerFeed);
        settingsPrefs = getSharedPreferences("mainPrefs", 0);
        final SharedPreferences.Editor editor = settingsPrefs.edit();
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.teleporter);

        spinnerFeed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String defaultFeed = spinnerFeed.getSelectedItem().toString();

                switch (defaultFeed) {
                    case "Winnipeg Jets":
                        editor.putString("defaultFeed", "https://www.winnipegfreepress.com/rss/?path=%2Fsports%2Fhockey%2Fjets");
                        editor.commit();
                        break;
                    case "Winnipeg Blue Bombers":
                        editor.putString("defaultFeed", "https://www.winnipegfreepress.com/rss/?path=%2Fsports%2Ffootball%2Fbombers");
                        editor.commit();
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        fontSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String chosenFontSize = fontSpinner.getSelectedItem().toString();

                switch (chosenFontSize) {
                    case "Small":
                        editor.putInt("fontSize", 14);
                        editor.commit();
                        break;
                    case "Large":
                        editor.putInt("fontSize", 16);
                        editor.commit();
                        break;
                    case "Larger":
                        editor.putInt("fontSize", 18);
                        editor.commit();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        nightModeToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (nightModeToggle.isChecked()) {
                    mediaPlayer.start();
                }

            }
        });

        nightModeToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settingsView = (View)findViewById(R.id.settingsView);
                rootView = settingsView.getRootView();

                if (b == true) {
                    rootView.setBackgroundColor(Color.parseColor(slateGrey));
                    txtNightMode.setTextColor(Color.parseColor(white));
                    txtFont.setTextColor(Color.parseColor(white));
                    txtFeed.setTextColor(Color.parseColor(white));

                    editor.putString("backgroundColor", slateGrey);
                    editor.putString("textColor", white);
                    editor.putBoolean("nightMode", b);
                    editor.commit();
                }
                else {
                    rootView.setBackgroundColor(Color.parseColor(white));
                    txtNightMode.setTextColor(Color.parseColor(slateGrey));
                    txtFont.setTextColor(Color.parseColor(slateGrey));
                    txtFeed.setTextColor(Color.parseColor(slateGrey));

                    editor.putString("backgroundColor", white);
                    editor.putString("textColor", slateGrey);
                    editor.putBoolean("nightMode", b);
                    editor.commit();
                }

            }
        });


    }
}
