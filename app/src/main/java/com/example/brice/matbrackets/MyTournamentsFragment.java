package com.example.brice.matbrackets;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
 * {@link MyTournamentsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyTournamentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyTournamentsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int prefID;
    private String prefToken;
    private GetTask mGetTask = null;
    private ArrayList<Tournament> tournamentsArray;

    private String getMyTournamentsURL;
    private String imagesURL;
    private HashMap<String, Bitmap> imagesHash;
    private ProgressBar myProgress;

    private OnFragmentInteractionListener mListener;

    public MyTournamentsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyTournamentsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyTournamentsFragment newInstance(String param1, String param2) {
        MyTournamentsFragment fragment = new MyTournamentsFragment();
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
        getMyTournamentsURL = getString(R.string.target_URL)+"/mobile/getMyTournaments.php";
        imagesURL = getString(R.string.target_URL)+"/images/";
    }

    @Override
    public void onStart(){
        super.onStart();

        FloatingActionButton fab = (FloatingActionButton) this.getActivity().findViewById(R.id.fab);
        fab.show();

        SharedPreferences userPrefs = this.getActivity().getSharedPreferences("user", 0);
        prefID = userPrefs.getInt("user_id", 0);
        prefToken = userPrefs.getString("user_token", "");
        tournamentsArray = new ArrayList<Tournament>();
        imagesHash = new HashMap<String, Bitmap>();

        myProgress = (ProgressBar)getActivity().findViewById(R.id.my_tournaments_progress);
        populateTournaments(prefID, prefToken);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_tournaments, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    public void populateTournaments(int id, String token){
        if (mGetTask != null) {
            return;
        }

        //showProgress(true);
        mGetTask = new GetTask(id, token, this.getContext());
        mGetTask.execute((Void) null);
    }

    private void showProgress(final boolean show) {
        myProgress.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void buildPage() {
        LinearLayout mainLayout = (LinearLayout) this.getActivity().findViewById(R.id.myTournamentsLayout);
        mainLayout.removeAllViews();

        if(tournamentsArray.size() == 0 || tournamentsArray.isEmpty()){
            CardView cardView = makeDefaultCard();
            mainLayout.addView(cardView);
        }
        // outer for loop
        for (int i = 0; i < tournamentsArray.size(); i++) {
            CardView cardView = makeCard(i);
            setListener(cardView, i);
            mainLayout.addView(cardView);
        }
    }

    private CardView makeDefaultCard(){
        CardView cardView = new CardView(this.getContext());
        cardView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        cardView.setMinimumHeight(200);
        cardView.setUseCompatPadding(true);

        TextView tv = makeTextView("You don't have any tournaments yet!");
        cardView.addView(tv);
        return cardView;
    }

    private CardView makeCard(int i){
        String temp;
        CardView cardView = new CardView(this.getContext());
        cardView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        cardView.setMinimumHeight(250);
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
        //    tv.setBackgroundResource(R.drawable.cell_shape);
        tv.setGravity(Gravity.LEFT);
        tv.setTextSize(18);
        tv.setPadding(15, 15, 15, 5);
        tv.setText(text);
        return tv;
    }

    private void setListener(CardView cv, int i){
        cv.setOnClickListener(new SpecialOnClickListener(i) {});
    }

    private class SpecialOnClickListener implements View.OnClickListener{
        int index;
        public SpecialOnClickListener(int arrayIndex) {
            this.index = arrayIndex;
        }

        @Override
        public void onClick(View v)
        {
            Intent intent = new Intent(v.getContext(), ViewTournamentActivity.class);
            intent.putExtra("tournament_id", tournamentsArray.get(index).getId());
            startActivity(intent);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class GetTask extends AsyncTask<Void, Void, Boolean> {

        private JSONObject resultJSON;
        private final int mID;
        private final String mToken;
        private Context getTaskContext;
        private Bitmap photobit = null;

        GetTask(int id, String token, Context getContext) {
            this.getTaskContext = getContext;
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

                URL devURL = new URL(getMyTournamentsURL);
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
                        Iterator<?> keys = resultJSON.keys();
                        JSONObject tempJObject;
                        while(keys.hasNext()){
                            String key = (String)keys.next();
                            if(!key.equals("status")){
                                Tournament tourney = new Tournament();
                                if(resultJSON.get(key) instanceof JSONObject) {
                                    //System.out.println(resultJSON.get(key).toString());
                                    tempJObject = (JSONObject) resultJSON.get(key);
                                    tourney.setId((int)tempJObject.get("tournament_id"));
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

                                            //Drawable thumb = Drawable.createFromStream(imgURL.openStream(), tourney.getImage_name());
                                            imagesHash.put(tourney.getImage_name(), photobit);
                                        } catch (Exception e) {
                                            System.out.println("you failed: " + imagesURL + tourney.getImage_name());
                                            System.out.println("Hashmap check: " + imagesHash.toString());
                                            e.printStackTrace();
                                        }
                                    }
                                    //System.out.println(tourney.toString());
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
            mGetTask = null;

            if (result != null) {
                if(result){
                    //showProgress(false);
                    System.out.println("Building page...");
                    buildPage();
                }else{

                }
            } else {

            }
        }

        @Override
        protected void onCancelled() {
            mGetTask = null;
        }
    }
}
