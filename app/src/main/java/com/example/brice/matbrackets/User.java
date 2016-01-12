package com.example.brice.matbrackets;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

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

/**
 * Created by Brice on 1/8/2016.
 */
public class User extends AppCompatActivity{

    private String email;
    private String password;
    private String mobileToken;

    public User(){

    }

    public User(String email, String token){
        this.email = email;
        this.mobileToken = token;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public void setMobileToken(String token){
        this.mobileToken = token;
    }

}
