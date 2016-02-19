package com.example.brice.matbrackets;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

public class ViewTournamentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tournament);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences userPrefs = getSharedPreferences("user", 0);
        String prefEmail = userPrefs.getString("user_email", "");
        String prefToken = userPrefs.getString("user_token", "");

        Integer tournamentID = getIntent().getIntExtra("tournament_id", 0);
        String address = getString(R.string.target_URL)+"/viewTournament.php?";
        address += "tournament="+tournamentID.toString();
        address += "token="+prefToken;
        address += "email="+prefEmail;
        address += "weight=106";

        WebView wv = (WebView)findViewById(R.id.web_view);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.loadUrl(address);

//        TextView tv = (TextView)findViewById(R.id.tournament_id);
//        tv.setText("Tournament ID: "+tournamentID);

//        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
//        startActivity(browserIntent);
    }
}
