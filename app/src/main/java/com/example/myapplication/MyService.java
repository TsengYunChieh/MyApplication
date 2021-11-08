package com.example.myapplication;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

public class MyService extends Service {
    private static final String TAG = "MyService";
    public static final String TAB = "SCREEN";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate()
    {
        super .onCreate();
        //建立見聽器(監聽螢幕發出的亮暗訊息)
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
        Log.d(TAG, "onCreate() executed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(TAG, "onStartCommand() executed");
        // 把自己變成前台服務
        startForeground(110,new Notification());
        //呼叫CountdownTimerService服務
        intent = new Intent(MyService.this,CountdownTimerService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(intent);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //關閉監聽器
        unregisterReceiver(mBroadcastReceiver);
        //停止前台服務
        stopForeground(true);
        Log.d(TAG, "onDestroy() executed");
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action))
            {
                assert action != null;
                switch (action)
                {
                    case Intent.ACTION_SCREEN_OFF:
                        Log.d(TAB, "屏幕關閉，變黑");
                        break;
                    case Intent.ACTION_SCREEN_ON:
                        Log.d(TAB, "屏幕開啓，變亮");
                        //呼叫CountdownTimerService的服務
                        Intent intent1 = new Intent(MyService.this,CountdownTimerService.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startService(intent1);
                        break;
                    default:
                        break;
                }
            }
        }
    };
}



