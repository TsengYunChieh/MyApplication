package com.example.myapplication;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class TwoActivity extends Activity
{
    private EditText EdiSetTime,EdiPassword,EdiSetPassword,EdiSerRePassword,EdiSetLockTime;
    private SharedPreferences myPassW, timeControl, timeLock;
    private static final String TAG = "CountdownTimerDemoActivity";

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);
        //按鍵設定
        Button butReSetPass = findViewById(R.id.button2);
        Button butSetTime = findViewById(R.id.button4);
        Button butStopService = findViewById(R.id.button5);
        Button butBack = findViewById(R.id.button6);
        Button butStart = findViewById(R.id.button7);
        Button butSetLockTime = findViewById(R.id.button8);
        //文字框設定
        EdiPassword = findViewById(R.id.editText2);
        EdiSetPassword = findViewById(R.id.editText6);
        EdiSerRePassword = findViewById(R.id.editText7);
        EdiSetTime = findViewById(R.id.editText4);
        EdiSetLockTime = findViewById(R.id.editText3);
        //存儲參數設定
        myPassW = getSharedPreferences("info", MODE_PRIVATE);
        timeControl = getSharedPreferences("info", MODE_PRIVATE);
        timeLock = getSharedPreferences("info", MODE_PRIVATE);
        //設定時間裏面的數字對應
        int setTime = timeControl.getInt("setTime",0);
        if(setTime == 0) EdiSetTime.setText(""+30);
        else EdiSetTime.setText(""+setTime);
        int setTimeLock = timeControl.getInt("setLockTime",0);
        if(setTimeLock == 0) EdiSetLockTime.setText(""+10);
        else EdiSetLockTime.setText(""+setTimeLock);

        //執行按鈕設定
        butStart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int setTime = timeControl.getInt("setTime",0);
                int setTimeLock = timeControl.getInt("setLockTime",0);
                Log.d(TAG, "執行");
                    
                if(!this.isWorked())
                {
                    //登出
                    Intent logOut = new Intent(TwoActivity.this,MainActivity.class);
                    startActivity(logOut);
                    //如果"使用時間計時器"及"休息時間計時器"內已經有設定好參數時的要做的動作
                    if(setTime != 0 && setTimeLock != 0)
                    {
                        //回到桌面
                        Intent i = new Intent(Intent.ACTION_MAIN);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addCategory(Intent.CATEGORY_HOME);
                        startActivity(i);
                        Intent intent = new Intent(TwoActivity.this,MyService.class);
                        intent.setAction("服務intent-filter的action");
                        // 啟動Service
                        startService(intent);
                        Toast.makeText(getApplicationContext(),"開始執行",Toast.LENGTH_SHORT).show();
                    }
                    //如果"使用時間計時器"及"休息時間計時器"內都沒有給定參數的時候要執行的動作
                    else if(setTime == 0 && setTimeLock == 0)
                    {
                        //給定"使用時間計時器"以及"休息時間計時器"的預設值
                        timeControl.edit().putInt("setTime", 30).apply();
                        timeLock.edit().putInt("setLockTime", 10).apply();
                        //回到桌面
                        Intent i = new Intent(Intent.ACTION_MAIN);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addCategory(Intent.CATEGORY_HOME);
                        startActivity(i);
                        Intent intent = new Intent(TwoActivity.this,MyService.class);
                        intent.setAction("服務intent-filter的action");
                        // 啟動Service
                        startService(intent);
                        Toast.makeText(getApplicationContext(),"開始執行",Toast.LENGTH_SHORT).show();
                    }
                    //如果"休息時間計時器"已經有給定數值但"使用時間計時器"還沒有給定參數時要執行的動作
                    else if(setTimeLock != 0)
                    {
                        //給定"使用時間計時器"的預設值
                        timeControl.edit().putInt("setTime", 30).apply();
                        //回到桌面
                        Intent i = new Intent(Intent.ACTION_MAIN);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addCategory(Intent.CATEGORY_HOME);
                        startActivity(i);
                        Intent intent = new Intent(TwoActivity.this,MyService.class);
                        intent.setAction("服務intent-filter的action");
                        // 啟動Service
                        startService(intent);
                        Toast.makeText(getApplicationContext(),"開始執行",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        //給定"休息時間計時器"的預設值
                        timeLock.edit().putInt("setLockTime", 10).apply();
                        //回到桌面
                        Intent i = new Intent(Intent.ACTION_MAIN);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addCategory(Intent.CATEGORY_HOME);
                        startActivity(i);
                        Intent intent = new Intent(TwoActivity.this,MyService.class);
                        intent.setAction("服務intent-filter的action");
                        // 啟動Service
                        startService(intent);
                        Toast.makeText(getApplicationContext(),"開始執行",Toast.LENGTH_SHORT).show();
                    }
                }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"已啟用",Toast.LENGTH_SHORT).show();
                    }

                if(setTime == 30) EdiSetTime.setText(""+30);
                else EdiSetTime.setText(""+setTime);
                if(setTimeLock == 10) EdiSetLockTime.setText(""+10);
                else EdiSetLockTime.setText(""+setTimeLock);
            }

            //判斷Service是否有在運作
            private boolean isWorked()
            {
                ActivityManager myManager = (ActivityManager) TwoActivity.this.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
                ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(30);
                for (int i = 0; i < runningService.size(); i++)
                {
                    if (runningService.get(i).service.getClassName().equals("com.example.myapplication.MyService"))
                    {
                        return true;
                    }
                }
                return false;
            }
        });

        //設定時間的button的監聽器
        butSetTime.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //設定時間從EdiSetTime取值
                String setTime = EdiSetTime.getText().toString();
                //isEmpty (看是不是空的)
                if(setTime.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"時間不能是空的",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(isNumeric(setTime))
                    {
                        int SetTime = Integer.parseInt(setTime);
                        timeControl.edit().putInt("setTime", SetTime).apply();
                        Toast.makeText(getApplicationContext(),"保存成功",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"請輸入整數",Toast.LENGTH_SHORT).show();
                    }
                }


            }
            //檢查輸入是否是整數
            boolean isNumeric(String str)
            {
                for (int i = str.length();--i>=0;)
                {
                    if (!Character.isDigit(str.charAt(i)))
                    {
                        return false;
                    }
                }
                return true;
            }
        });

        //設定所定時間的按鈕
        butSetLockTime.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //設定鎖定時間從EdiSetLockTime取值
                String setLockTime = EdiSetLockTime.getText().toString();

                if(setLockTime.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"時間不能是空的",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(isNumeric(setLockTime))
                    {
                        int SetLockTime = Integer.parseInt(setLockTime);
                        timeLock.edit().putInt("setLockTime", SetLockTime).apply();
                        Toast.makeText(getApplicationContext(),"保存成功",Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(getApplicationContext(),"請輸入整數",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            //檢查是否是整數
            boolean isNumeric(String str)
            {
                for (int i = str.length();--i>=0;)
                {
                    if (!Character.isDigit(str.charAt(i)))
                    {
                        return false;
                    }
                }
                return true;
            }
        });

        //重設密碼按鈕
        butReSetPass.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                //舊密碼從EdiPassword取值
                String originalPassword = EdiPassword.getText().toString().trim();
                //新密碼從EdiSetPassword取值
                String setPassword = EdiSetPassword.getText().toString().trim();
                //新密碼再次輸入從EdiSerRePassword取值
                String setRePassword = EdiSerRePassword.getText().toString().trim();
                //從myPassW關鍵字"password"取得已經儲存的密碼
                String passWord = myPassW.getString("password",null);

                if(originalPassword.isEmpty() && setPassword.isEmpty() && setRePassword.isEmpty() || originalPassword.isEmpty() || setPassword.isEmpty() || setRePassword.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"重新設定是空的",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(originalPassword.equals(passWord)&& setPassword.equals(setRePassword))
                    {
                        SharedPreferences.Editor editor = myPassW.edit();
                        editor.putString("password", setPassword).apply();
                        Toast.makeText(getApplicationContext(),"保存成功",Toast.LENGTH_SHORT).show();
                        EdiPassword.setText("");
                        EdiSetPassword.setText("");
                        EdiSerRePassword.setText("");
                    }
                    else if(!originalPassword.equals(passWord)&& setPassword.equals(setRePassword))
                    {
                        Toast.makeText(getApplicationContext(),"密碼錯誤",Toast.LENGTH_SHORT).show();
                    }
                    else if(originalPassword.equals(passWord))
                    {
                        Toast.makeText(getApplicationContext(),"設定密碼有誤",Toast.LENGTH_SHORT).show();
                    }
                    else
                        {
                            Toast.makeText(getApplicationContext(),"密碼設定錯誤並且設定密碼有誤",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //設定停止按鈕
        butStopService.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(this.isWorked("com.example.myapplication.MyService") || this.isWorked("com.example.myapplication.CountdownTimerService")){
                    //停止MyService
                    Intent stopMyS = new Intent(TwoActivity.this, MyService.class);
                    stopService(stopMyS);
                    //停止CountdownTimerService
                    Intent stopTimer = new Intent(TwoActivity.this, CountdownTimerService.class);
                    stopService(stopTimer);
                    Toast.makeText(getApplicationContext(),"停止",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"沒有服務在執行",Toast.LENGTH_SHORT).show();
                }
            }
            
            //檢查服務是否有開啟
            private boolean isWorked(String className) {
                ActivityManager myManager = (ActivityManager) TwoActivity.this
                        .getApplicationContext().getSystemService(
                                Context.ACTIVITY_SERVICE);
                ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(30);
                for (int i = 0; i < runningService.size(); i++) {
                    if (runningService.get(i).service.getClassName().equals(className))
                    {
                        return true;
                    }
                }
                return false;
            }
        });
        //設定返回按鈕
        butBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TwoActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
