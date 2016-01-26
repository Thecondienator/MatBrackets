package com.example.brice.matbrackets;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

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
    private Tournament[] tournamentsArray;

    //private String getMyTournamentsURL = "https://dev.matbrackets.com/mobile/myTournaments.php";
    private String getMyTournamentsURL;

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
        getMyTournamentsURL = getString(R.string.target_URL)+"getMyTournaments.php";
    }

    @Override
    public void onStart(){
        super.onStart();

        SharedPreferences userPrefs = this.getActivity().getSharedPreferences("user", 0);
        prefID = userPrefs.getInt("user_id", 0);
        prefToken = userPrefs.getString("user_token", "");

        populateTournaments(prefID, prefToken);
        //expListView = (ExpandableListView) getView().findViewById(R.id.expandableListView);
        //prepareListData();
        //listAdapter = new ExpandableListAdapter(this.getContext(), listDataHeader, listDataChild);
        //expListView.setAdapter(listAdapter);
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

        mGetTask = new GetTask(id, token, this.getContext());
        mGetTask.execute((Void) null);
    }

    private void BuildTable() {

//        sqlcon.open();
//        Cursor c = sqlcon.readEntry();
//
//        int rows = c.getCount();
//        int cols = c.getColumnCount();
//        String[] array;
//        c.moveToFirst();
//
//        // outer for loop
//        for (int i = 0; i < rows; i++) {
//
//            TableRow row = new TableRow(this);
//            row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
//                    LayoutParams.WRAP_CONTENT));
//
//            // inner for loop
//            for (int j = 0; j < cols; j++) {
//
//                TextView tv = new TextView(this);
//                tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
//                        LayoutParams.WRAP_CONTENT));
//                //    tv.setBackgroundResource(R.drawable.cell_shape);
//                tv.setGravity(Gravity.CENTER);
//                tv.setTextSize(18);
//                tv.setPadding(0, 5, 0, 5);
//                array = c.getString(1).split(",");
//                for (int k = 0; k < array.length; k++) {
//                    tv.setText(array[k]);
//                    row.addView(tv);
//
//                }
//
//            }
//
//            c.moveToNext();
//
//            table_layout.addView(row);
//
//        }
//        sqlcon.close();
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class GetTask extends AsyncTask<Void, Void, Boolean> {

        private JSONObject resultJSON;
        private final int mID;
        private final String mToken;
        private Context getContext;

        GetTask(int id, String token, Context getContext) {
            this.getContext = getContext;
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
                        JSONArray arrJSON = new JSONArray(resultJSON);
//                        for(int i = 0; i < arrJSON.length(); i++){
//
//                        }
                        HashMap<String, String> tourney = new HashMap<String, String>();
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
            mGetTask = null;
        }
    }
}
