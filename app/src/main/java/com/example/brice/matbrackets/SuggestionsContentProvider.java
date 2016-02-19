package com.example.brice.matbrackets;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.BitmapFactory;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.ArrayList;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class SuggestionsContentProvider extends ContentProvider {
    private static final String[] COLUMNS = {
            "_id", // must include this column
            SearchManager.SUGGEST_COLUMN_TEXT_1,
            SearchManager.SUGGEST_COLUMN_TEXT_2};
    public MatrixCursor cursor = new MatrixCursor(COLUMNS);

    public SuggestionsContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        //throw new UnsupportedOperationException("Not yet implemented");
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String query = uri.getLastPathSegment().toLowerCase();
        if(!query.equals("search_suggest_query")){
            ArrayList<Tournament> tournamentsArray = getSuggestions(query);
            for(Tournament tournament: tournamentsArray){
                cursor.addRow(new Object[] {tournament.getId(), tournament.getYear()+" "+tournament.getName(), tournament.getLocation_city()+", "+tournament.getRegion()});
            }
        }
        MatrixCursor returnMatrix = cursor;
        cursor = new MatrixCursor(COLUMNS);
        return returnMatrix;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        //throw new UnsupportedOperationException("Not yet implemented");
        return 0;
    }

    private ArrayList<Tournament> getSuggestions(String matchText){
        ArrayList<Tournament> tournamentsArray = new ArrayList<Tournament>();
        String findTournamentsLikeURL = getContext().getString(R.string.target_URL)+"/mobile/getTournamentsLike.php";
        JSONObject resultJSON;

        SharedPreferences userPrefs = getContext().getSharedPreferences("user", 0);
        int user_id = userPrefs.getInt("user_id", 0);

        try{
            String query = "input="+URLEncoder.encode(matchText, "UTF-8");
            query += "&";
            query += "user="+user_id;
            System.out.println("Match query: "+query);

            URL devURL = new URL(findTournamentsLikeURL);
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
            System.out.println("Match response: "+responseStrBuilder.toString());
            try {
                resultJSON = new JSONObject(responseStrBuilder.toString());
                if(resultJSON.getBoolean("status")){
                    Iterator<?> keys = resultJSON.keys();
                    JSONObject tempJObject;
                    while(keys.hasNext()){
                        String key = (String)keys.next();
                        if(!key.equals("status")){
                            Tournament tourney = new Tournament();
                            if(resultJSON.get(key) instanceof JSONObject) {
                                tempJObject = (JSONObject) resultJSON.get(key);
                                tourney.setName(tempJObject.get("tournament_name").toString());
                                tourney.setSize((int) tempJObject.get("size"));
                                tourney.setLocation_city(tempJObject.get("location_city").toString());
                                tourney.setYear((int) tempJObject.get("year"));
                                tourney.setRegion(tempJObject.get("region_name").toString());
                                tourney.setAbbreviation(tempJObject.get("abbreviation").toString());
                                tournamentsArray.add(tourney);
                            }
                        }
                    }
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

        return tournamentsArray;
    }
}
