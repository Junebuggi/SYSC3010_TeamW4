package com.example.plantnursery;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Date;


class UDPReceiver extends Thread{
    private String ipAddress = "192.168.137.101";
    private static final int PORT =8008;

    UDPSender udpSender = new UDPSender();
    private DatagramSocket server = null;
    private int notificationCount = 0;



    public UDPReceiver() {
        try {
            server = new DatagramSocket(PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        Log.d("User","new server socket");
    }
    public void run(){
        JSONObject ack = new JSONObject();
        try {
            ack.put("opcode", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        byte[] byte1024 = new byte[1024];
        DatagramPacket dPacket = new DatagramPacket(byte1024, 1000);
        String txt;
        try{

            Log.d("User","runing run()");
            while(true){
                server.receive(dPacket);
               while(true)
               {
                   //server.setSoTimeout(3000);
                    txt = new String(byte1024, 0, dPacket.getLength());
                    System.out.println("~~~~~~~~this is the string" + txt);

                    JSONObject obj = new JSONObject(txt);
                    String opcode = obj.getString("opcode");
                    switch (opcode){
                        case "D":
                            System.out.println("~~~caseD");
                            //send ACK
                            udpSender.run(ipAddress, ack.toString() , 1003);
                            System.out.println("~~~~in notification opcode");
                            //send an ACK here
                            //{"opcode" : "D", "sensorArray" : "0,0,0,0,0,0,1,0,0,0,0"}
                            //process the array, add the data
                            String sensorArray = obj.getString("sensorArray");
                            String[] values = sensorArray.split(",");
                            Integer[] sensors = new Integer[values.length];
                            //create list of ints
                            for(int i = 0; i < values.length; i++) {
                                sensors[i] = Integer.parseInt(values[i].trim());
                            }

                            if (sensors[0] == 1) {
                                MainActivity.notificationHistory.add(0, "#" + notificationCount++ +" Threshold" + ": " + "Light threshold is met!" + " " + new Date());
                            }
                            if (sensors[1] == 1) {
                                MainActivity.notificationHistory.add(0, "#" + notificationCount++ +" Threshold" + ": " + "Humidity threshold is met!" + " " + new Date());
                            }
                            if (sensors[2] == 1) {
                                MainActivity.notificationHistory.add(0,"#" + notificationCount++ +" Threshold" + ": " + "Temperature threshold is met!" + " " + new Date());
                            }
                            if (sensors[3] == 1) {
                                MainActivity.notificationHistory.add(0,"#" + notificationCount++ +" Threshold" + ": " + "Soil moisture threshold is met!" + " " + new Date());
                            }
                            if (sensors[4] == 1) {
                                MainActivity.notificationHistory.add(0,"#" + notificationCount++ + "Water supply is low, please refill!" + " " + new Date());
                            }
                            if (sensors[5] == 1) {
                                MainActivity.notificationHistory.add(0,"#" + notificationCount++ +" ERROR" + ": " + "Light sensor error" + " " + new Date());
                            }
                            if (sensors[6] == 1) {
                                MainActivity.notificationHistory.add(0,"#" + notificationCount++ +" ERROR" + ": " + "Humidity sensor error" + " " + new Date());
                            }
                            if (sensors[7] == 1) {
                                MainActivity.notificationHistory.add(0,"#" + notificationCount++ +" ERROR" + ": " + "Soil Moisture sensor error" + " " + new Date());
                            }
                            if (sensors[8] == 1) {
                                MainActivity.notificationHistory.add(0,"#" + notificationCount++ +" ERROR" + ": " + "Ultrasonic sensor error" + " " + new Date());
                            }
                            if (sensors[9] == 1) {
                                MainActivity.notificationHistory.add(0,"#" + notificationCount++ +" ERROR" + ": " + "Water pump error" + " " + new Date());
                            }
                            break;
                        case "0":
                            //ACK received
                            System.out.println("~~~~~~ACK RECEIVED");
                            break;
                        case "4":
                            udpSender.run(ipAddress, ack.toString() , 1003);
                            MainActivity.notificationHistory.add(0,"#" + notificationCount++ +" Pump Started: Duration"+ obj.getString("pumpDuration") + new Date());
                            break;
                        case "6":
                            System.out.println("~~~~case6");
                            //send ACK
                            udpSender.run(ipAddress, ack.toString() , PORT);
                            //stats are sent
                            try {
                                //ensures handler is initialized first before thread is executed
                                Thread.sleep(1000);
                                DataTableActivity.exHandler.sendMessage(DataTableActivity.exHandler.obtainMessage(1, txt));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (NullPointerException nullPointerException){
                                System.out.println("Catch the Nullpointer exception");
                            }
                            //ViewDataActivity.exHandler.sendMessage(ViewDataActivity.exHandler.obtainMessage(1, txt));
                            break;
                        case "7":
                            System.out.println("~~~~caseE");
                            udpSender.run(ipAddress, ack.toString() , PORT);

                            try {
                                //ensures handler is initialized first before thread is executed
                                Thread.sleep(1000);
                                ViewStatusActivity.exHandler.sendMessage(ViewStatusActivity.exHandler.obtainMessage(1, txt));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (NullPointerException nullPointerException){
                                System.out.println("Catch the Nullpointer exception");
                            }
                            break;

                            //ViewStatusActivity.exHandler.sendMessage(ViewStatusActivity.exHandler.obtainMessage(1, txt));
                    }


                    Log.d("User","Handler send Message");
                    if(true) break;
                }
                //CloseSocket(client);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void CloseSocket(DatagramSocket socket) throws IOException{
        socket.close();
    }
}