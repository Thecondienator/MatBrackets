package com.example.brice.matbrackets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void populateLocal(){

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

                //URL devURL = new URL(getLocalTournamentsURL);
                URL devURL = new URL("blah");
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
                                    //System.out.println(resultJSON.get(key).toString());
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
//                                            URL imgURL = new URL(imagesURL + tourney.getImage_name());
//                                            URLConnection imageConn = imgURL.openConnection();
//                                            imageConn.connect();
//                                            InputStream is = imageConn.getInputStream();
//                                            BufferedInputStream bis = new BufferedInputStream(is);
//                                            photobit = BitmapFactory.decodeStream(bis);
//                                            bis.close();
//                                            is.close();

                                            //Drawable thumb = Drawable.createFromStream(imgURL.openStream(), tourney.getImage_name());
                                            //imagesHash.put(tourney.getImage_name(), photobit);
                                        } catch (Exception e) {
//                                            System.out.println("you failed: " + imagesURL + tourney.getImage_name());
//                                            System.out.println("Hashmap check: " + imagesHash.toString());
//                                            e.printStackTrace();
                                        }
                                    }
                                    //System.out.println(tourney.toString());
                                    //tournamentsArray.add(tourney);
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
            //mGetTask = null;
            //System.out.println("Building page...");
            //buildPage();

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
            //mGetTask = null;
        }
    }
}
