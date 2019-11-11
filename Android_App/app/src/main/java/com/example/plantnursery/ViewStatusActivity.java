package com.example.plantnursery;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class ViewStatusActivity extends AppCompatActivity {
    public static Handler exHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewstatus);

        //initialize textViews
        final TextView tTemp = findViewById(R.id.textView11);
        final TextView tHumidity = findViewById(R.id.textView10);
        final TextView tLight = findViewById(R.id.textView13);
        final TextView tWaterLevel = findViewById(R.id.textView9);
        final TextView tSoil = findViewById(R.id.textView12);
        TextView tIPAddress = findViewById(R.id.textView4);

        //compatibility
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        //Get JSON file and process it
        exHandler=new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String msgString = (String)msg.obj;
//                JSONObject obj = null; //cast to JSON
//                try {
//                    obj = new JSONObject("msgString");
//                    String temperature = obj.getJSONObject("message").getString("temp"); //get string associated with JSON
//                    String humidity = obj.getJSONObject("message").getString("humidity"); //get string associated with JSON
//                    String light = obj.getJSONObject("message").getString("light");
//                    String waterLevel = obj.getJSONObject("message").getString("waterLevel");
//                    String soilMoisture = obj.getJSONObject("message").getString("soil");
//
//                    tTemp.setText("" + temperature);
//                    tHumidity.setText("Recieved: " + humidity);
//                    tWaterLevel.setText("Recieved: " + light);
//                    tLight.setText("Recieved: " + waterLevel);
//                    tSoil.setText("Recieved: " + soilMoisture);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

                Log.d("Handler","Now in Handler");
                tLight.setText("Recieved: " + msgString);


            }
        };

    }//end OnCreate

}