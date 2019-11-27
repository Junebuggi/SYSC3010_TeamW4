package com.example.plantnursery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ViewDataActivity extends AppCompatActivity {

    private String ipAddress = "172.17.47.247";
    private static final int PORT = 8008;

    UDPSender  udpSender = new UDPSender();
    private Button viewData;
    private EditText numRecords, potID;
    private CheckBox temp, humidity, light, ultrasonic, soilMoisture;
    String sensorType = "";
    JSONObject data;

    public ViewDataActivity() throws IOException, JSONException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewdata);

        viewData = findViewById(R.id.button7);
        numRecords = findViewById(R.id.editText16);
        temp = findViewById(R.id.checkBox9);
        humidity = findViewById(R.id.checkBox10);
        light = findViewById(R.id.checkBox11);
        ultrasonic = findViewById(R.id.checkBox12);
        soilMoisture = findViewById(R.id.checkBox14);
        potID = findViewById(R.id.editText3);


        viewData.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ViewDataActivity.this, "view data", Toast.LENGTH_SHORT).show();
                //put it in JSON format
                //[‘opcode’: ‘5’, ‘sensorType’: comma separated string, ‘rowNumbers’: integer]
                data = new JSONObject();
                try {
                    data.put("opcode", "5");

                    if(temp.isChecked()){
                        sensorType += "temperature,";
                    }
                    if(humidity.isChecked()){
                        sensorType += "humidity,";
                    }
                    if(light.isChecked()){
                        sensorType += "light,";
                    }
                    if(ultrasonic.isChecked()){
                        sensorType += "waterDistance,";
                    }
                    if(soilMoisture.isChecked()){
                        sensorType += "soilMoisture,";
                    }


                    String sensor = sensorType.substring(0, sensorType.lastIndexOf(","));
                    System.out.println("~~~~~~~~~~" + sensor);

                    data.put("rowNumbers", numRecords.getText().toString());
                    data.put("potID", potID.getText().toString());
                    data.put("sensorType", sensor);


                    udpSender.run(ipAddress, data.toString() , PORT);

                    startActivity(new Intent(ViewDataActivity.this, DataTableActivity.class));

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                }
            });

    }

}
