package com.mobvoi.ticwear.mobvoiapidemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.api.ResultCallback;
import com.mobvoi.android.wearable.MessageApi;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Node;
import com.mobvoi.android.wearable.NodeApi;
import com.mobvoi.android.wearable.Wearable;

public class WeatherActivity extends Activity implements MobvoiApiClient.ConnectionCallbacks,
        MobvoiApiClient.OnConnectionFailedListener, NodeApi.NodeListener, MessageApi.MessageListener {
    private static final Uri WEATHER_URI = Uri.parse("content://com.mobvoi.provider.weather");
    /*
     * time 时间
     * temp 温度
     * address 具体地点
     * location 地区
     * maxtemp 最高温度
     * mintemp 最低温度
     * pm25 PM2.5
     * weather 天气情况
     * sunset 日落时间
     * sunrise 日出时间
     */
    private static final String[] COLUMN_NAMES = {"time", "temp", "address", "location", "maxtemp", "mintemp", "pm25",
            "weather", "sunset", "sunrise"};
    private static final String TAG = "WeatherActivity";
    private static final String DEFAULT_NODE = "default_node";

    private ContentResolver mResolver;
    private WeatherInfo mInfo;
    private ContentObserver mObserver;
    private TextView mWeatherTv;
    private MobvoiApiClient mMobvoiApiClient;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mInfo != null) {
                mWeatherTv.setText(mInfo.toString());
                sendMessagetoPhone();
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        init();
    }

    private void init() {
        mWeatherTv = (TextView) findViewById(R.id.weather_tv);
        mResolver = this.getContentResolver();
        mObserver = new ContentObserver(mHandler) {
            @Override
            public boolean deliverSelfNotifications() {
                return super.deliverSelfNotifications();
            }

            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                mInfo = fetchWeatherInfo();
                Message.obtain(mHandler, 0).sendToTarget();
            }
        };
        mMobvoiApiClient = new MobvoiApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mInfo = fetchWeatherInfo();
        if (mInfo != null) {
            mWeatherTv.setText(mInfo.toString());
        }
        registerContentObserver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMobvoiApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.MessageApi.removeListener(mMobvoiApiClient, this);
        Wearable.NodeApi.removeListener(mMobvoiApiClient, this);
        mMobvoiApiClient.disconnect();
    }

    private WeatherInfo fetchWeatherInfo() {
        Cursor cursor = mResolver.query(WEATHER_URI, COLUMN_NAMES, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    WeatherInfo info = new WeatherInfo();
                    info.time = cursor.getString(cursor.getColumnIndex(COLUMN_NAMES[0]));
                    info.temp = cursor.getString(cursor.getColumnIndex(COLUMN_NAMES[1]));
                    info.address = cursor.getString(cursor.getColumnIndex(COLUMN_NAMES[2]));
                    info.location = cursor.getString(cursor.getColumnIndex(COLUMN_NAMES[3]));
                    info.maxtemp = cursor.getString(cursor.getColumnIndex(COLUMN_NAMES[4]));
                    info.mintemp = cursor.getString(cursor.getColumnIndex(COLUMN_NAMES[5]));
                    info.pm25 = cursor.getString(cursor.getColumnIndex(COLUMN_NAMES[6]));
                    info.weather = cursor.getString(cursor.getColumnIndex(COLUMN_NAMES[7]));
                    info.sunset = cursor.getString(cursor.getColumnIndex(COLUMN_NAMES[8]));
                    info.sunrise = cursor.getString(cursor.getColumnIndex(COLUMN_NAMES[9]));
                    return info;
                }
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    private void registerContentObserver() {
        mResolver.registerContentObserver(WEATHER_URI, true, mObserver);
    }

    private void unregisterContentObserver() {
        mResolver.unregisterContentObserver(mObserver);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Wearable.MessageApi.addListener(mMobvoiApiClient, this);
        Wearable.NodeApi.addListener(mMobvoiApiClient, this);
        sendMessagetoPhone();
    }

    @Override
    public void onConnectionSuspended(int cause) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e(TAG, "onConnectionFailed(): Failed to connect, with result: " + result);
    }

    @Override
    public void onMessageReceived(MessageEvent event) {
        Log.d(TAG, "onMessageReceived: " + event);
    }

    @Override
    public void onPeerConnected(Node node) {
        Log.d(TAG, "onPeerConncted:");
    }

    @Override
    public void onPeerDisconnected(Node node) {
        Log.d(TAG, "onPeerDisconnected:");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterContentObserver();
    }

    private void sendMessagetoPhone() {
        byte[] data = mInfo.toString().getBytes();
        Wearable.MessageApi.sendMessage(
                mMobvoiApiClient, DEFAULT_NODE, "/weathers", data).setResultCallback(
                new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        if (!sendMessageResult.getStatus().isSuccess()) {
                            Log.e(TAG, "Failed to send message with status code: "
                                    + sendMessageResult.getStatus().getStatusCode());
                        } else {
                            Log.d(TAG, "Success");
                        }
                    }
                }
        );
    }
}
