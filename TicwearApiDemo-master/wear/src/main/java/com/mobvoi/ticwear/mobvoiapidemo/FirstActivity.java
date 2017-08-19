package com.mobvoi.ticwear.mobvoiapidemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WearableListView;
import android.util.Log;

public class FirstActivity extends Activity implements WearableListView.ClickListener {
    private static final String TAG = "FirstActivity";
    // Sample dataset for the list
   // private final String[] mElements = {"数据传输", "传感器", "地理位置", "健康数据", "天气", "手势", "语音识别","语音合成", "语义", "搜索", "快捷卡片", "挠挠", "UI库Demo", "发送数据"};
    private final String[] mElements = {"传感器", "发送数据"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        // Get the list component from the layout of the activity
        WearableListView listView =
                (WearableListView) findViewById(R.id.wearable_list);

        // Assign an adapter to the list
        listView.setAdapter(new ListAdapter(this, mElements));

        // Set a click listener
        listView.setClickListener(this);
    }

    // WearableListView click listener
    @Override
    public void onClick(WearableListView.ViewHolder v) {
        Integer tag = (Integer) v.itemView.getTag();
        switch (tag) {
            case 0: {
                Intent startIntent = new Intent(this, SensorActivity.class);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startIntent);
                //Log.d(TAG, "MessageReceived! Open MainActivity");
                break;
            }
            case 1: {
                Intent sensorIntent = new Intent(this, SendDataActivity.class);
                sensorIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(sensorIntent);
                //Log.d(TAG, "MessageReceived! Open Sensors!");
                break;
            }
        }

    }

    @Override
    public void onTopEmptyRegionClick() {

    }
}