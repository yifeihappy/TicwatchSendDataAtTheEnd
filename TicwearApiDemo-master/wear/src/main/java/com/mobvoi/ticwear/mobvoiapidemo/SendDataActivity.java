package com.mobvoi.ticwear.mobvoiapidemo;

import android.app.Activity;
import android.net.IpPrefix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.util.EncodingUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class SendDataActivity extends Activity {

   // private TextView mTextView;
    private Button sendButton;
    private EditText IPedt;
    String filename = "SensorData.txt";
    private String HOST = "192.168.0.152";//computer IP
    private int PORT = 5000; //server PORT,the same to server
    private final int SEND_ERROR = 1;
    private final int SEND_SUCCESS = 0;

    Handler myHandler = new MyHandler();

    private  class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SEND_SUCCESS:
                    Toast.makeText(SendDataActivity.this,"Send Success",Toast.LENGTH_SHORT).show();
                    break;
                case SEND_ERROR:
                    Toast.makeText(SendDataActivity.this,"Send failed",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_send_data);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//?

        IPedt = (EditText)findViewById(R.id.IPedt);
        sendButton = (Button)findViewById(R.id.sendbutton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"Hahahaha",Toast.LENGTH_SHORT).show();
                HOST = IPedt.getText().toString();
                String buffer = readData(filename);
                //Log.e("Test",buffer);
                new ClientThread(buffer).start();
                sendButton.setEnabled(false);
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        sendButton.setEnabled(true);
    }

    public String readData(String filename) {
        String res = "";
        FileInputStream fin = null;
        try {
            fin = openFileInput(filename);
            int length = fin.available();
            byte[] buffer = new  byte[length];
            fin.read(buffer);
            res = EncodingUtils.getString(buffer,"UTF-8");
            fin.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Open "+filename+"failed!",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Length "+filename+"failed!",Toast.LENGTH_SHORT).show();
        }
        return  res;
    }
    private class ClientThread extends Thread {
        String buffer;
        Socket socket;
        PrintStream output;
        ClientThread(String buffer) {
            this.buffer = buffer;
        }

        @Override
        public void run() {
            socket = new Socket();
            Message msg = Message.obtain();
            try {
                socket.connect(new InetSocketAddress(HOST,PORT),5000);
                output = new PrintStream(socket.getOutputStream());
                output.println(buffer);
                output.flush();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
                msg.what = SEND_ERROR;
            }
            msg.what = SEND_SUCCESS;
            myHandler.sendMessage(msg);

        }
    }
}
