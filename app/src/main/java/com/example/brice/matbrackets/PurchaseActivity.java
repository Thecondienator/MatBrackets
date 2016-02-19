package com.example.brice.matbrackets;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class PurchaseActivity extends AppCompatActivity {
    Integer tournamentID;
    Integer tournament_year;
    String tournament_name;
    String location_city;
    String region_name;
    String region_abbr;
    String image_name;
    String imagesURL;
    Bitmap photobit;
    GetImageTask mGetImageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);

        setTitle("Purchase");
        imagesURL = getString(R.string.target_URL)+"/images/";

        tournamentID = getIntent().getIntExtra("tournament_id", 0);
        tournament_year = getIntent().getIntExtra("tournament_year", 0);
        tournament_name = getIntent().getStringExtra("tournament_name");
        location_city = getIntent().getStringExtra("tournament_city");
        region_name = getIntent().getStringExtra("tournament_region");
        region_abbr = getIntent().getStringExtra("tournament_abbreviation");
        image_name = getIntent().getStringExtra("tournament_image");

        prepareTextFields();
        getImage();
    }

    private void prepareImage(){
        ImageView img = (ImageView)findViewById(R.id.tournament_logo_view);
        if(image_name.equals("")) {
            img.setImageResource(R.drawable.logo);
        }else{
            try {
                img.setImageBitmap(photobit);
            }catch(Exception e){
                img.setImageResource(R.drawable.logo);
                e.printStackTrace();
            }
        }
    }

    private void prepareTextFields(){
        try{
            TextView tv = (TextView)findViewById(R.id.tournament_name_view);
            tv.setText(tournament_year+" "+tournament_name);
            TextView lv = (TextView)findViewById(R.id.tournament_location_view);
            lv.setText(location_city+", "+region_abbr);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getImage(){
        if (mGetImageTask != null) {
            return;
        }

        mGetImageTask = new GetImageTask();
        mGetImageTask.execute((Void) null);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class GetImageTask extends AsyncTask<Void, Void, Boolean> {

        GetImageTask() {

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            if (!image_name.equals("")) {
                try {
                    URL imgURL = new URL(imagesURL + image_name);
                    URLConnection imageConn = imgURL.openConnection();
                    imageConn.connect();
                    InputStream is = imageConn.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(is);
                    photobit = BitmapFactory.decodeStream(bis);
                    bis.close();
                    is.close();
                    return true;
                } catch (Exception e) {
                    System.out.println("you failed: " + imagesURL + image_name);
                    e.printStackTrace();
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean result) {
            mGetImageTask = null;

            if (result != null) {
                if(result){
                    //showProgress(false, localProgress);
                    System.out.println("Building image...");
                    prepareImage();

                }else{

                }
            } else {

            }
        }

        @Override
        protected void onCancelled() {
            mGetImageTask = null;
        }
    }
}
