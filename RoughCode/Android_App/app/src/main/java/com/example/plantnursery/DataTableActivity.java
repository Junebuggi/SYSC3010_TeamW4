package com.example.plantnursery;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class DataTableActivity extends AppCompatActivity {


    TableView tableView;
//
//    private static final String[][] SPACESHIPS = { { "Casini", "Chemical", "NASA" },
//            { "Spitzer", "Nuclear", "NASA" } };
    public static Handler exHandler = null;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datatable);
        tableView = findViewById(R.id.tableView);

        //'{"opcode" : "6", "statsArray":"["{ "light":"3", "tdate": "12-12-12","ttime": "12:45pm"}", "{ "light": 37, "tdate": "2-12-12","ttime": "1:45pm"}"]"}'
        //{"opcode" : "6", "statsArray" : "[{"light": 100.0, "ttime":14:48:58", "soilMoisture": 200.0, "tdate": "2019-11-28"},
        // {"light": 10, "ttime":34:48:58", "soilMoisture": 29, "tdate": "2019-12-28"}]


        //set the headers
        String[] spaceProbeHeaders={"Sensor","Value","Date","Time"};
        tableView.setHeaderBackgroundColor(Color.parseColor("#95F80D"));
        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(this,spaceProbeHeaders));
        tableView.setColumnCount(4);

        //tableView.setDataAdapter(new SimpleTableDataAdapter(this, SPACESHIPS ));


        exHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                System.out.println("~~~~ in th handler dataTableActivity");
                super.handleMessage(msg);
                ArrayList<String[]> list = new ArrayList<>();
                String[] stats;
                String sensor = "";
                String name = "";

                try{
                    JSONObject obj = new JSONObject((String) msg.obj);
                    JSONArray result = obj.getJSONArray("statsArray");
                    if(result.length() == 0){
                        Toast.makeText(DataTableActivity.this, "invalid potID, no data found :(", Toast.LENGTH_SHORT).show();
                    }
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject jsonObject = result.getJSONObject(i);
                        //all of them is 6

                        if(jsonObject.has("l")){
                            sensor = "Light";
                            name = jsonObject.getString("l");
                            String date = jsonObject.getString("d");
                            String time = jsonObject.getString("T");
                            stats = new String[]{sensor, name, date, time};
                            list.add(stats);
                        }

                        if(jsonObject.has("t")){
                            sensor = "Temp";
                            name = jsonObject.getString("t");
                            String date = jsonObject.getString("d");
                            String time = jsonObject.getString("T");
                            stats = new String[]{sensor, name, date, time};
                            list.add(stats);
                        }

                        if(jsonObject.has("h")){
                            sensor = "Humidity";
                            name = jsonObject.getString("h");
                            String date = jsonObject.getString("d");
                            String time = jsonObject.getString("T");
                            stats = new String[]{sensor, name, date, time};
                            list.add(stats);
                        }

                        if(jsonObject.has("s")){ //14 records
                            sensor = "Moisture";
                            name = jsonObject.getString("s");
                            String date = jsonObject.getString("d");
                            String time = jsonObject.getString("T");
                            stats = new String[]{sensor, name, date, time};
                            list.add(stats);
                        }

                        if(jsonObject.has("w")){
                            sensor = "WaterLevel";
                            name = jsonObject.getString("w");
                            String date = jsonObject.getString("d");
                            String time = jsonObject.getString("T");
                            stats = new String[]{sensor, name, date, time};
                            list.add(stats);
                        }
//                        if(list.size() > 30){
//                            Toast.makeText(DataTableActivity.this, "limit is 30 rows!", Toast.LENGTH_SHORT).show();
//                            break;
//                        }
                    }

                    tableView.setDataAdapter(new SimpleTableDataAdapter(DataTableActivity.this, list));
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        };

    }


}

//try{
//        JSONObject obj = new JSONObject((String) msg.obj);
//        JSONArray result = obj.getJSONArray("statsArray");
//        for (int i = 0; i < result.length(); i++) {
//        JSONObject jsonObject = result.getJSONObject(i);
//
//        String name = jsonObject.getString("light");
//        String phone = jsonObject.getString("tdate");
//        String email = jsonObject.getString("ttime");
//
//        String j = String.valueOf(i);
//        stats = new String[]{j, name, email, phone};
//
//        list.add(stats);
//        }
//
//        tableView.setDataAdapter(new SimpleTableDataAdapter(DataTableActivity.this, list));
//        }catch (JSONException e) {
//        e.printStackTrace();
//        }
