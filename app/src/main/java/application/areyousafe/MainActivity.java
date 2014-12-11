package application.areyousafe;

//IMPORT LISTS
import android.app.Dialog;

import android.content.Context;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class MainActivity extends FragmentActivity implements LocationListener{
    private GoogleMap map;
    public LocationManager lm;
    Button GetMyLocationButton;
    public List<Incident> incidentList;
    public Vector<Incident> incidentVector;
    public Location currentLocation;
    public String killed, injured, factor1, factor2;

    public TextView responseTextView;
    public Button mainButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mainButton = (Button) findViewById(R.id.MainButton);
        this.responseTextView = (TextView) this.findViewById(R.id.textView);

        //If google play is available
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        //Else Google Play Services are not available
        if(status != ConnectionResult.SUCCESS)
        {
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
        }
        else
        {
            map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment)).getMap();
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            map.setMyLocationEnabled(true);
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                Location lastLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (lastLocation != null){
                    onLocationChanged(lastLocation);
                }

                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            }
        }



        //set onClick Listener
        GetMyLocationButton = (Button) findViewById(R.id.MainButton);

        //BUTTON FUNCTION

        GetMyLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //map.animateCamera(CameraUpdateFactory.zoomTo(18));
                new GetIncidentsTask().execute(new ApiConnector());


                mainButton.setEnabled(false);

            }
        });



    }//End of MAIN


    @Override
    public void onLocationChanged(Location location) {
        LatLng lastLng = new LatLng(location.getLatitude(),location.getLongitude());

        currentLocation = location;
        map.moveCamera(CameraUpdateFactory.newLatLng(lastLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(17));


    }




    //APIConnector - passed to do in background
    //Long - passed on to progressUpdate
    //JSONArray is the result returned
    private class GetIncidentsTask extends AsyncTask<ApiConnector,String, String>
    {
        //Before entering the background thread
        @Override
        protected void onPreExecute() {

        }

        //Entering background thread
        @Override
        protected String doInBackground(ApiConnector... params) {

            DefaultHttpClient httpClient = new DefaultHttpClient();  // Default HttpClient
            HttpResponse httpResponse = null;

            String longitude = String.valueOf( currentLocation.getLongitude());
            String latitude = String.valueOf( currentLocation.getLatitude());
            String url = "http://mgltr.root.sx/query.php?";
            url += "x=";
            url += longitude;
            url += "&y=";
            url += latitude;

            publishProgress("Accessing:" + url);
            HttpGet httpGet = new HttpGet(url);


            int numTries = 3;//number of attempts to execute the httpGet

            while (true) {
                try {
                    httpResponse = httpClient.execute(httpGet);
                    break;
                } catch (Exception e ) {
                    if (--numTries == 0) try {
                        throw e;
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        Log.e("HTTP Tries error ", e1.toString());
                        publishProgress("Tried too many times.");
                        return "TRIED TOO MANY TIMES HTTP PROBLEM";
                    }
                }
            }

            publishProgress("HTTP executed");
            HttpEntity entity = null;
            if (httpResponse != null) {
                 entity = httpResponse.getEntity();
            }
            else{
                publishProgress("Empty Entity");
                return null;
            }

            publishProgress("Converting to string");

            InputStream is = null;
            try {
                is = entity.getContent();
            } catch (IOException e) {
                e.printStackTrace();
                publishProgress("Stream cannot be created");
                return null;
            }


            //CONVERT MESSAGE INTO STRING
            String message = params[0].getStringFromInputStream(is);
            //publishProgress(message);
            return message;
        }


        //Update the progress on main thread
        @Override
        protected  void onProgressUpdate(String... incoming){
            responseTextView.setText(incoming[0]);
        }


        @Override
        protected  void onPostExecute (String stringMessage){
            handleHTTPMessage(stringMessage);
        }




    }

    private void handleHTTPMessage(String message){
        if(message.equals("ERROR_Qpg_get_result")){
            mainButton.setEnabled(true);
            responseTextView.setText("ERROR IN SQL QUERY, TRY AGAIN");
        }
        if(message.equals("ERROR_D")){
            responseTextView.setText("Not enough data.");
        }
        if(message.length() == 0){
            mainButton.setEnabled(true);
            responseTextView.setText("ERROR MESSAGE = 0");
        }
        else{
            //parse string into a JSON
            responseTextView.setText("PARSING");
            StringtoJSON(message);
            //ParseResults(theArray);
        }
    }


    //Takes the message and tokenizes it
    private void StringtoJSON(String message){
        // Convert HttpEntity into JSON Array

        //responseTextView.setText("HTTP: "  + message);


        try {

            JSONObject obj = new JSONObject(message);
            Log.d("My App", obj.toString());
            responseTextView.setText("PARSING: " + message);

            killed = obj.getString("KILLED");
            injured = obj.getString("INJURED");
            factor1 = obj.getString("FACTOR1");
            factor2 = obj.getString("FACTOR2");

            responseTextView.setText(killed + " " + injured + " " + factor1 + " " + factor2);

        } catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON: " + message)   ;
            mainButton.setEnabled(true);

            responseTextView.setText("THREW EXCEPTION");

        }



   }


    public void ParseResults(JSONArray results){
        responseTextView.setText(results.toString());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}


