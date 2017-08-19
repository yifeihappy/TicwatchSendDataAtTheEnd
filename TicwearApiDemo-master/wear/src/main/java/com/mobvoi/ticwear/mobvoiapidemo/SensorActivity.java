package com.mobvoi.ticwear.mobvoiapidemo;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;

/**
 * Shows Sensor Messages from the Wearable APIs.
 */
public class SensorActivity extends Activity implements  SensorEventListener {

    //private static final String TAG = "SensorActivity";
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGravity;
    private Sensor mGyroscope;
    private Sensor mMagnet;
    String filenme = "SensorData.txt";
    Handler myHandler;
    TextView txtValue;
//    private String HOST = "192.168.0.152";//computer IP
//    private int PORT = 5000; //server PORT,the same to server
//    private String buffer;
//    Socket socket;
    FileOutputStream fout ;
    long start_time;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_sensor);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mMagnet = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        myHandler = new MyHandler();
        txtValue = (TextView)findViewById(R.id.value);

        start_time =  System.currentTimeMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mSensorManager.registerListener(this, mAccelerometer,SensorManager.SENSOR_DELAY_NORMAL);
//        mSensorManager.registerListener(this,mGravity,SensorManager.SENSOR_DELAY_NORMAL);
//        mSensorManager.registerListener(this,mGyroscope,SensorManager.SENSOR_DELAY_NORMAL);

        mSensorManager.registerListener(this, mAccelerometer,SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this,mGravity,SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this,mGyroscope,SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this,mMagnet,SensorManager.SENSOR_DELAY_GAME);
        try {
            fout =openFileOutput(filenme,MODE_PRIVATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        try {
            fout.flush();
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x,y,z;
        int sensorType = event.sensor.getType();
        long curtime=System.currentTimeMillis();
        String  curTimeStr=String.valueOf(curtime);
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];
                break;
            case Sensor.TYPE_GRAVITY:
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];
                break;
            case Sensor.TYPE_GYROSCOPE:
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];
                break;
            default:
                return;
        }
        String message = sensorType + "," + curTimeStr + "," + x + "," + y + "," + z + "\n";
        try {
            fout.write(message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"write "+filenme + " failed",Toast.LENGTH_SHORT);
        }
        Log.e("message",message);

        long recorder_time;
        recorder_time = curtime - start_time;
        Message msg = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putString("mytime",recorder_time+"");
        msg.setData(bundle);
        myHandler.sendMessage(msg);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String record_time = (String) bundle.get("mytime");
            txtValue.setText(record_time);
        }
    }

//    class  ClientThread extends Thread {
//        String str;
//        Socket socket;
//        PrintStream output;
//
//        public ClientThread (String str) {
//            this.str = str;
//        }
//
//        @Override
//        public void run() {
//
//
////            Message ms = myHandler.obtainMessage();
////            Bundle bundle = new Bundle();
////            bundle.clear();
//
//
//
//
//            try {
//                socket = new Socket();
//                socket.connect(new InetSocketAddress(HOST,PORT),5000);
//
//               //in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                output = new PrintStream(socket.getOutputStream());
//                output.println(str);
//                Log.e("str",str);
//                output.flush();
//  //              String line = null;
////                buffer = "";
////                while ((line = in.readLine()) != null) {
////                    buffer = buffer + line;
////                    if(buffer!=null) {
////                        bundle.putString("newcontent",buffer);
////                        ms.setData(bundle);
////                        myHandler.sendMessage(ms);
////                    }
////                }
//            }
//            catch (SocketTimeoutException aa)
//            {
////                bundle.putString("newcontent","There is Timeout");
////                ms.setData(bundle);
////                myHandler.sendMessage(ms);
//
//            }
//            catch (IOException e) {
//
////                bundle.putString("newcontent","There is IOEX");
////                ms.setData(bundle);
////                myHandler.sendMessage(ms);
//
//            }
//
//            try {
//a
//                output.close();
//                //writer.close();
//                //in.close();
//                socket.close();
//            }
//            catch (IOException e) {
////                bundle.putString("newcontent","There is IOEX close");
////                ms.setData(bundle);
////                myHandler.sendMessage(ms);
//
//            }
//        }
//
//    }
}
