package com.example.brice.matbrackets;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import java.util.HashMap;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private int screenHeight;
    private int screenWidth;
    private int prefID;
    private String prefToken;
    private GetLocalTask mGetLocalTask = null;
    private ArrayList<Tournament> tournamentsArray;

    private String getLocalTournamentsURL;
    private String imagesURL;
    private HashMap<String, Bitmap> imagesHash;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        getLocalTournamentsURL = getString(R.string.target_URL)+"/mobile/getMyTournaments.php";
        imagesURL = getString(R.string.target_URL)+"/images/";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();

        SharedPreferences userPrefs = this.getActivity().getSharedPreferences("user", 0);
        prefID = userPrefs.getInt("user_id", 0);
        prefToken = userPrefs.getString("user_token", "");
        tournamentsArray = new ArrayList<Tournament>();
        imagesHash = new HashMap<String, Bitmap>();

        populateLocal(prefID, prefToken);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void populateLocal(int id, String token){
        if (mGetLocalTask != null) {
            return;
        }

        mGetLocalTask = new GetLocalTask(id, token);
        mGetLocalTask.execute((Void) null);
    }

    public void buildPage(){
        Display display = this.getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        LinearLayout mainLayout = (LinearLayout) this.getActivity().findViewById(R.id.localTournamentsLayout);
        mainLayout.removeAllViews();

//        Display display = this.getActivity().getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        int width = size.x;
//        int height = size.y;
//
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                height,
//                LinearLayout.LayoutParams.MATCH_PARENT);
//        mainLayout.setLayoutParams(lp);

        if(tournamentsArray.size() == 0 || tournamentsArray.isEmpty()){
            CardView cardView = makeDefaultCard();
            mainLayout.addView(cardView);
        }
        // outer for loop
        for (int i = 0; i < tournamentsArray.size(); i++) {
            System.out.println(tournamentsArray.get(i).getName());
            CardView cardView = makeCard(i);

            mainLayout.addView(cardView);
        }
    }

    private CardView makeDefaultCard(){
        CardView cardView = new CardView(this.getContext());
        cardView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        cardView.setMinimumHeight(200);
        cardView.setUseCompatPadding(true);

        TextView tv = makeTextView("There are no tournaments in your area!");
        cardView.addView(tv);
        return cardView;
    }

    private CardView makeCard(int i){
        String temp;
        CardView cardView = new CardView(this.getContext());
        cardView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        cardView.setMinimumHeight(screenHeight/4);
        cardView.setUseCompatPadding(true);
        cardView.setCardElevation(10);
        cardView.setRadius(10);

        RelativeLayout newRL = new RelativeLayout(this.getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        newRL.setGravity(Gravity.LEFT);

        ImageView img = new ImageView(this.getContext());
        if(tournamentsArray.get(i).getImage_name().equals("")) {
            img.setImageResource(R.drawable.logo);
        }else{
            try {
                img.setImageBitmap(imagesHash.get(tournamentsArray.get(i).getImage_name()));
            }catch(Exception e){
                img.setImageResource(R.drawable.logo);
                e.printStackTrace();
            }
        }
        RelativeLayout.LayoutParams imgParams = new RelativeLayout.LayoutParams(250, 250);
        img.setLayoutParams(imgParams);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        img.setId(R.id.imgViewID);
        newRL.addView(img);

        params.addRule(RelativeLayout.RIGHT_OF, img.getId());

        temp = " "+String.valueOf(tournamentsArray.get(i).getYear())+" "+tournamentsArray.get(i).getName();
        TextView tv = makeTextView(temp);
        tv.setId(R.id.nameViewID);
        newRL.addView(tv, params);

        params2.addRule(RelativeLayout.RIGHT_OF, img.getId());
        params2.addRule(RelativeLayout.BELOW, tv.getId());

        temp = "      "+tournamentsArray.get(i).getLocation_city() + ", " + tournamentsArray.get(i).getAbbreviation();
        TextView locationView = makeTextView(temp);
        newRL.addView(locationView, params2);

        cardView.addView(newRL);
        return cardView;
    }

    private TextView makeTextView(String text){
        TextView tv = new TextView(this.getContext());
        tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        tv.setGravity(Gravity.LEFT);
        tv.setTextSize(18);
        tv.setPadding(15, 15, 15, 5);
        tv.setText(text);
        return tv;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class GetLocalTask extends AsyncTask<Void, Void, Boolean> {

        private JSONObject resultJSON;
        private final int mID;
        private final String mToken;
        private Bitmap photobit = null;

        GetLocalTask(int id, String token) {
            mID = id;
            mToken = token;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                String query = "user="+mID;
                query += "&";
                query += "token="+URLEncoder.encode(mToken, "UTF-8");
                System.out.println("Tournaments query: "+query);

                URL devURL = new URL(getLocalTournamentsURL);
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
                System.out.println("Tournaments response: "+responseStrBuilder.toString());
                try {
                    resultJSON = new JSONObject(responseStrBuilder.toString());
                    if(resultJSON.getBoolean("status")){
//                        HashMap<String, String> tourney = new HashMap<String, String>();
//                        for(int i = 0; i < resultJSON.length(); i++){
//                            Tournament tourney = new Tournament();
//                            tourney.setName(resultJSON)
//                        }
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
                                    tourney.setImage_name(tempJObject.get("image_name").toString());
                                    if (!tourney.getImage_name().equals("")) {
                                        try {
                                            URL imgURL = new URL(imagesURL + tourney.getImage_name());
                                            URLConnection imageConn = imgURL.openConnection();
                                            imageConn.connect();
                                            InputStream is = imageConn.getInputStream();
                                            BufferedInputStream bis = new BufferedInputStream(is);
                                            photobit = BitmapFactory.decodeStream(bis);
                                            bis.close();
                                            is.close();

                                            Drawable thumb = Drawable.createFromStream(imgURL.openStream(), tourney.getImage_name());
                                            imagesHash.put(tourney.getImage_name(), photobit);
                                        } catch (Exception e) {
                                            System.out.println("you failed: " + imagesURL + tourney.getImage_name());
                                            System.out.println("Hashmap check: " + imagesHash.toString());
                                            e.printStackTrace();
                                        }
                                    }
                                    System.out.println(tourney.toString());
                                    tournamentsArray.add(tourney);
                                }
                            }
                        }
                        //System.out.println(tournamentsArray.toString());
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
            mGetLocalTask = null;
            System.out.println("Building page...");
            buildPage();

            if (result != null) {
                if(result){
//                    System.out.println("Building page...");
//                    buildPage();
//                    SharedPreferences userPrefs = getSharedPreferences("user", 0);
//                    SharedPreferences.Editor editor = userPrefs.edit();
//                    editor.putString("user_email", resultEmail);
//                    editor.putString("user_token", resultToken);
//                    editor.putInt("user_id", resultUserID);
//                    //System.out.println("First: "+resultFirstName+", Last: "+resultLastName);
//                    editor.putString("user_first_name", resultFirstName);
//                    editor.putString("user_last_name", resultLastName);
//                    editor.commit();
//                    Intent mainActivityIntent = new Intent(loginContext, MainActivity.class);
//                    startActivity(mainActivityIntent);
//                    finish();
                }else{
//                    mPasswordView.setError(resultMessage);
//                    mPasswordView.requestFocus();
                }
            } else {
//                mPasswordView.setError(getString(R.string.error_occurred));
//                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mGetLocalTask = null;
        }
    }
}
