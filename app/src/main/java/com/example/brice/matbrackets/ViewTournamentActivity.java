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
import android.webkit.WebViewClient;
import android.widget.TextView;

public class ViewTournamentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tournament);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        SharedPreferences userPrefs = getSharedPreferences("user", 0);
        String prefEmail = userPrefs.getString("user_email", "");
        String prefToken = userPrefs.getString("user_token", "");

        Integer tournamentID = getIntent().getIntExtra("tournament_id", 0);
        Integer tournamentYear = getIntent().getIntExtra("tournament_year", 0);
        String tournamentName = getIntent().getStringExtra("tournament_name");
        setTitle(tournamentYear+" "+tournamentName);

        String address = getString(R.string.target_URL)+"/mobile/mobileView.php?";
        address += "tournament="+tournamentID.toString();
        address += "&token="+prefToken;
        address += "&email="+prefEmail;
        address += "&weight=106";

        System.out.println("Viewing tournament.....");
        System.out.println("Address: "+address);

        WebView wv = (WebView)findViewById(R.id.web_view);
        wv.setWebViewClient(new WebViewClient());
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setBuiltInZoomControls(true);
        wv.loadUrl(address);

//        TextView tv = (TextView)findViewById(R.id.tournament_id);
//        tv.setText("Tournament ID: "+tournamentID);

//        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
//        startActivity(browserIntent);
    }
}
