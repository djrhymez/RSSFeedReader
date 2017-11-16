package com.example.dan.assignment7;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class singleArticleActivity extends AppCompatActivity {
    private String title;
    private String date;
    private String description;
    private String link;
    private TextView txtTitle;
    private TextView txtDate;
    private TextView txtDesc;
    private View articleView;
    private View rootView;
    private SharedPreferences settingsPrefs;
    private String slateGrey = "#383838";
    private String white = "#ffffff";

    @Override
    protected void onResume() {
        super.onResume();
        articleView = (View) findViewById(R.id.articleView);
        rootView = articleView.getRootView();

        settingsPrefs = getSharedPreferences("mainPrefs", 0);

        String backgroundColor = settingsPrefs.getString("backgroundColor", white);
        rootView.setBackgroundColor(Color.parseColor(backgroundColor));

        String textColor = settingsPrefs.getString("textColor", slateGrey);
        int fontSize = settingsPrefs.getInt("fontSize", 14);

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setTextColor(Color.parseColor(textColor));

        txtDate = (TextView) findViewById(R.id.txtDate);
        txtDate.setTextColor(Color.parseColor(textColor));

        txtDesc = (TextView) findViewById(R.id.txtDesc);
        txtDesc.setTextColor(Color.parseColor(textColor));
        txtDesc.setTextSize(TypedValue.COMPLEX_UNIT_SP,fontSize);
    }

    private Button btnRead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_article);

        Intent intent = getIntent();
        title = intent.getStringExtra("title").toString();
        date = intent.getStringExtra("date").toString();
        description = intent.getStringExtra("description").toString();
        link = intent.getStringExtra("link").toString();

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setText(title);

        txtDate = (TextView) findViewById(R.id.txtDate);
        txtDate.setText(date);

        txtDesc = (TextView) findViewById(R.id.txtDesc);
        txtDesc.setText(Html.fromHtml(description));

        btnRead = (Button) findViewById(R.id.btnReadMore);

        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(link));
                startActivity(browserIntent);
            }
        });
    }
}
