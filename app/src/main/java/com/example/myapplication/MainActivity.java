package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    private SharedPreferences myPassW;
    private Button butSetPass;
    private EditText EdiPassword;
    private static final String TAG = "CountdownTimerDemoActivity";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "startCountdownTimerDemoActivity");
        //建立Activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        butSetPass = findViewById(R.id.button3);
        EdiPassword = findViewById(R.id.editText);

        //設定、儲存密碼
        myPassW = getSharedPreferences("info", MODE_PRIVATE);
        String myPass = myPassW.getString("password",null);

        //設定改變登入按鈕裡面的文字  *(如果裡面有密碼就是登入)*   *(裡面沒有密碼(第一次登入)就是設定)*
        if(myPass != null)
        {
            butSetPass.setText("登入");
        }
        else butSetPass.setText("設定密碼");

        //判斷權限是否有開
        if (!Settings.canDrawOverlays(this))
        {
            Toast.makeText(this, "當前無權限，請授權", Toast.LENGTH_SHORT).show();
            //延遲執行
            new Handler().postDelayed(new Runnable()
            {
                public void run()
                {
                    //execute the task
                    checkPermission(MainActivity.this);
                }
            }, 1000);
        }

        //設定密碼的監聽器
        butSetPass.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String setPassword = EdiPassword.getText().toString().trim();
                String myPass = myPassW.getString("password",null);
                if(myPass != null)
                {
                    if(setPassword.isEmpty())
                    {
                        Toast.makeText(getApplicationContext(),"密碼不能是空的",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        String my_pass = myPass.trim();
                        if(my_pass.equals(setPassword))
                        {
                            EdiPassword.setText("");
                            Toast.makeText(getApplicationContext(),"登入成功",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, TwoActivity.class);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"密碼有誤",Toast.LENGTH_SHORT).show();
                        }

                    }
                }
                else{
                    if(setPassword.isEmpty())
                    {
                        Toast.makeText(getApplicationContext(),"設定密碼不能是空的",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        butSetPass.setText("登入");
                        SharedPreferences.Editor editor = myPassW.edit();
                        editor.putString("password", setPassword).apply();
                        Toast.makeText(getApplicationContext(),"保存成功",Toast.LENGTH_SHORT).show();
                        EdiPassword.setText("");
                    }
                }
            }
        });
    }

    public static void checkPermission(Activity activity){
        if (!Settings.canDrawOverlays(activity)) {
            activity.startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + activity.getPackageName())), 0);
        }
    }
    @Override
    //檢查權限startActivityForResult裡面會呼叫的東西
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == 0 && !Settings.canDrawOverlays(this))
        {
            //建立計時器 是因為有8.0以下Settings.canDrawOverlays裡面的值不會更新
            new CountDownTimer(500, 500) {
                @Override
                public void onTick(long millisUntilFinished)
                {
                }

                @Override
                public void onFinish()
                {
                    if (Settings.canDrawOverlays(MainActivity.this))
                    {
                        //授權成功
                        Toast.makeText(getApplicationContext(), "授權成功", Toast.LENGTH_SHORT).show();
                    }
                    else
                        {
                            //授權失敗重新開啟程式
                        Toast.makeText(getApplicationContext(), "授權失敗", Toast.LENGTH_SHORT).show();
                        //延遲執行
                            new Handler().postDelayed(new Runnable()
                            {
                                public void run()
                                {
                                    //execute the task
                                    checkPermission(MainActivity.this);
                                }
                            }, 800);
                    }
                }
            }.start();
        }
    }
}




