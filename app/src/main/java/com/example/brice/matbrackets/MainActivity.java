package com.example.brice.matbrackets;

import android.animation.LayoutTransition;
import android.app.SearchManager;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AccountFragment.OnFragmentInteractionListener,
        HomeFragment.OnFragmentInteractionListener,
        MyTournamentsFragment.OnFragmentInteractionListener{

    String email;
    String token;
    String firstName;
    String lastName;
    Integer curFragment;
    private UserAuthTask mAuthTask = null;

    //private String mobileLoginURL = "https://dev.matbrackets.com/mobile/mobileLogin.php";
    private String mobileLoginURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mobileLoginURL = getString(R.string.target_URL)+"/mobile/mobileLogin.php";

        SharedPreferences userPrefs = getSharedPreferences("user", 0);
        SharedPreferences activityPrefs = getSharedPreferences("activity", 0);
        email = userPrefs.getString("user_email", "");
        token = userPrefs.getString("user_token", "");
        firstName = userPrefs.getString("user_first_name", "");
        lastName = userPrefs.getString("user_last_name", "");
        curFragment = activityPrefs.getInt("current_fragment", 0);

        if(!email.isEmpty() && !token.isEmpty()) {
            checkToken(email, token);
        }else{
            Intent loginActivityIntent = new Intent(this, LoginActivity.class);
            startActivity(loginActivityIntent);
            finish();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //View header = LayoutInflater.from(this).inflate(R.layout.nav_header_main, null);
        //navigationView.addHeaderView(header);
        View header = navigationView.getHeaderView(0);
        TextView textName = (TextView) header.findViewById(R.id.textViewName);
        textName.setText(firstName + " " + lastName);
        TextView textEmail = (TextView) header.findViewById(R.id.textViewEmail);
        textEmail.setText(email);

//        TextView nameText = (TextView)findViewById(R.id.textViewName);
//        nameText.setText(firstName + " " + lastName);
//        TextView emailText = (TextView)findViewById(R.id.textViewEmail);
//        emailText.setText(email);

        if(curFragment == 0 || curFragment == null){
            displayView(R.id.nav_home);
        }else{
            displayView(curFragment);
        }

    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        if(curFragment != null) {
            SharedPreferences activityPrefs = getSharedPreferences("activity", 0);
            SharedPreferences.Editor editor = activityPrefs.edit();
            editor.putInt("current_fragment", curFragment);
            editor.commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            displayView(item.getItemId());
        } else if (id == R.id.nav_my_tournaments) {
            displayView(item.getItemId());
        } else if (id == R.id.nav_account) {
            displayView(item.getItemId());
        } else if (id == R.id.nav_logout) {
            logout();
        }

        return true;
    }

    public void displayView(int viewId) {

        Fragment fragment = null;
        String title = getString(R.string.app_name);

        switch (viewId) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                title  = "MatBrackets";
                curFragment = R.id.nav_home;
                break;
            case R.id.nav_my_tournaments:
                fragment = new MyTournamentsFragment();
                title = "My Tournaments";
                curFragment = R.id.nav_my_tournaments;
                break;
            case R.id.nav_account:
                fragment = new AccountFragment();
                title = "Account";
                curFragment = R.id.nav_account;
                break;
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        // set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void logout(){
        SharedPreferences userPrefs = getSharedPreferences("user", 0);
        SharedPreferences.Editor editor = userPrefs.edit();
        editor.clear();
        editor.commit();

        Intent loginActivityIntent = new Intent(this, LoginActivity.class);
        startActivity(loginActivityIntent);
        finish();
    }

    public void checkToken(String email, String token){
        if (mAuthTask != null) {
            return;
        }

        mAuthTask = new UserAuthTask(email, token, "token", this);
        mAuthTask.execute((Void) null);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserAuthTask extends AsyncTask<Void, Void, Boolean> {

        private JSONObject resultJSON;
        private final String mEmail;
        private final String mAuthValue;
        private final String mAuthType;
        private Context authContext;

        UserAuthTask(String email, String authValue, String authType, Context authContext) {
            this.authContext = authContext;
            mEmail = email;
            mAuthValue = authValue;
            mAuthType = authType;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                String query = "email="+ URLEncoder.encode(mEmail, "UTF-8");
                query += "&";
                if(mAuthType == "password"){
                    query += "password="+URLEncoder.encode(mAuthValue, "UTF-8");
                }else if(mAuthType == "token"){
                    query += "token="+URLEncoder.encode(mAuthValue, "UTF-8");
                }
                System.out.println("Check query: "+query);

                URL devURL = new URL(mobileLoginURL);
                HttpsURLConnection con = (HttpsURLConnection)devURL.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-length", String.valueOf(query.length()));
                con.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
                con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;Windows98;DigExt)");
                con.setConnectTimeout(8000);
                con.setDoOutput(true);
                con.setDoInput(true);

                DataOutputStream output = new DataOutputStream(con.getOutputStream());

                output.writeBytes(query);
                output.close();

                DataInputStream input = new DataInputStream(con.getInputStream());

                BufferedReader streamReader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr = null;
                while((inputStr = streamReader.readLine()) != null){
                    responseStrBuilder.append(inputStr);
                }
                System.out.println("Check response: "+responseStrBuilder.toString());
                try {
                    resultJSON = new JSONObject(responseStrBuilder.toString());
                    if(resultJSON.getBoolean("status")){
                        return true;
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (MalformedURLException e){
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean result) {
            mAuthTask = null;

            if (result != null) {
                if(!result){
                    Intent loginActivityIntent = new Intent(authContext, LoginActivity.class);
                    startActivity(loginActivityIntent);
                    finish();
                }
            } else {
                Intent loginActivityIntent = new Intent(authContext, LoginActivity.class);
                startActivity(loginActivityIntent);
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }

    public void onFragmentInteraction(Uri uri){

    }
}
