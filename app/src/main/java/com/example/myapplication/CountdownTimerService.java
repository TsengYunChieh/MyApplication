package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.app.AlertDialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.Objects;

public class CountdownTimerService extends Service
{
    private static final String TAG = "CountdownTimerDemoActivity";
    /**
     *
     */
    private SharedPreferences myPassW;
    //計時器名稱設定
    CountDownTimer countDownTimer,lockCountDownTimer;
    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(TAG, "startCountdownTimerDemoActivity");
        //建立音效
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.dindondindon);
        // 彈出圖片設定
        final ImageView image = new ImageView(this);
        image.setImageResource(R.drawable.ic_launcher_round);
        //儲存位置設定
        SharedPreferences timeControl = getSharedPreferences("info", MODE_PRIVATE);
        myPassW = getSharedPreferences("info", MODE_PRIVATE);
        SharedPreferences timeLock = getSharedPreferences("info", MODE_PRIVATE);
        //取得儲存位置的值
        final int timer = timeControl.getInt("setTime",0);
        final int lockTimer = timeLock.getInt("setLockTime",0);
        //計時器設定
        countDownTimer = new CountDownTimer(timer * 60000, 1000)
        {
            @Override
            // onFinish (計時器倒數完要做的事)
            public void onFinish()
            {
                //先關掉MyService  (因為中間如果關掉螢幕在開會跳出計時器)
                Intent stopMyService = new Intent(CountdownTimerService.this, MyService.class);
                stopService(stopMyService);
                //播放音效
                mp.start();
                Log.d(TAG, "onFinish");//只是debug用
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)//如果版本確認設備版本大於等於API 26
                {
                    final AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(CountdownTimerService.this);
                    mAlertDialog.setTitle("提醒時間已經到了");//設定標題
                    mAlertDialog.setView(image);//設定圖片
                    mAlertDialog.setMessage("倒數");//設定文字訊息
                    mAlertDialog.setIcon(R.mipmap.ic_launcher);//設定標題前面的圖片
                    mAlertDialog.setPositiveButton("關閉視窗", null);//設定按鈕的文字

                    final AlertDialog dialog = mAlertDialog.create();
                    dialog.setCancelable(false);//讓視窗不能點除了特定位置而關閉
                    Objects.requireNonNull(dialog.getWindow()).setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);//設定視窗的最高權限(讓它在所有視窗之上)
                    dialog.show();//生成視窗
                    //設定按鈕裡面要觸發的效果(關閉視窗)
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v) {
                            {
                                AlertDialog.Builder alert = new AlertDialog.Builder(CountdownTimerService.this);//設定登入視窗
                                final EditText edittext = new EditText(CountdownTimerService.this);//建立輸入框
                                alert.setTitle("請登入");//設定標題
                                alert.setMessage("輸入密碼");//設定訊息
                                alert.setView(edittext);//設定輸入框
                                //確認按鈕
                                alert.setPositiveButton("確定", null);
                                //取消按鈕
                                alert.setNegativeButton("取消", null);
                                final AlertDialog dialogPass = alert.create();
                                Objects.requireNonNull(dialogPass.getWindow()).setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                                //設定視窗的最高權限(讓它在所有視窗之上)
                                dialogPass.setCancelable(false);
                                //讓視窗不能點除了特定位置而關閉
                                dialogPass.show();
                                //生成登入視窗
                                //設定確定按鈕裡面的效果
                                dialogPass.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        String YouEditTextValue = edittext.getText().toString();
                                        String passWord = myPassW.getString("password", null);
                                        if (YouEditTextValue.equals(passWord)) {
                                            Toast.makeText(getApplicationContext(), "即將關閉", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "密碼正確");
                                            dialog.dismiss();//關閉阻擋視窗
                                            dialogPass.dismiss();//關閉密碼輸入視窗
                                            lockCountDownTimer.cancel();//停止lockCountDownTimer計時器
                                        }
                                        else {
                                            Toast.makeText(getApplicationContext(), "密碼錯誤", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "密碼錯誤");
                                        }
                                    }
                                });
                                //設定取消按鈕裡面的效果
                                dialogPass.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        dialogPass.cancel();//關閉密碼輸入視窗
                                    }
                                });
                            }
                        }
                    });

                    lockCountDownTimer = new CountDownTimer(lockTimer*60000, 1000)
                    {
                        @Override
                        public void onTick(long millisUntilFinished)
                        {
                            Log.d(TAG, "onTick 1" );
                            //設定倒數文字
                            if(millisUntilFinished/1000%60 < 10){
                                dialog.setMessage("倒數"+millisUntilFinished/60000 +":0"+ (millisUntilFinished/1000%60));
                            }
                            else dialog.setMessage("倒數"+millisUntilFinished/60000 +":"+ (millisUntilFinished/1000%60));
                        }
                        @Override
                        public void onFinish()
                        {
                            //關閉阻擋視窗
                            dialog.dismiss();
                            //執行MyService(再次判斷亮暗)
                            Intent intent1 = new Intent(CountdownTimerService.this, MyService.class);
                            startService(intent1);
                        }
                    }.start();

                }
                else
                    {
                    final AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(CountdownTimerService.this);
                    mAlertDialog.setTitle("提醒時間已經到了");
                    mAlertDialog.setView(image);
                    mAlertDialog.setMessage("倒數");
                    mAlertDialog.setIcon(R.mipmap.ic_launcher);
                    mAlertDialog.setPositiveButton("關閉視窗", null);

                    final AlertDialog dialog = mAlertDialog.create();
                    dialog.setCancelable(false);
                    Objects.requireNonNull(dialog.getWindow()).setType(WindowManager.LayoutParams.TYPE_PRIORITY_PHONE);
                    dialog.show();

                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            {
                                AlertDialog.Builder alert = new AlertDialog.Builder(CountdownTimerService.this);
                                final EditText edittext = new EditText(CountdownTimerService.this);
                                alert.setTitle("請登入");
                                alert.setMessage("輸入密碼");
                                alert.setView(edittext);
                                //確認按鈕
                                alert.setPositiveButton("確定", null);
                                //取消按鈕
                                alert.setNegativeButton("取消", null);
                                final AlertDialog dialogPass = alert.create();
                                Objects.requireNonNull(dialogPass.getWindow()).setType(WindowManager.LayoutParams.TYPE_PRIORITY_PHONE);
                                dialogPass.setCancelable(false);
                                dialogPass.show();
                                dialogPass.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        String YouEditTextValue = edittext.getText().toString();
                                        String passWord = myPassW.getString("password", null);
                                        if (YouEditTextValue.equals(passWord)) {
                                            Toast.makeText(getApplicationContext(), "即將關閉", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "密碼正確");
                                            dialog.dismiss();
                                            dialogPass.dismiss();
                                            lockCountDownTimer.cancel();
                                        }
                                        else {
                                            Toast.makeText(getApplicationContext(), "密碼錯誤", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "密碼錯誤");
                                        }
                                    }
                                });
                                dialogPass.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        dialogPass.cancel();
                                    }
                                });
                            }
                        }
                    });

                    lockCountDownTimer = new CountDownTimer(lockTimer*60000, 1000)
                    {
                        @Override
                        public void onTick(long millisUntilFinished)
                        {
                            Log.d(TAG, "onTick 1" );
                            if(millisUntilFinished/1000%60 < 10){
                                dialog.setMessage("倒數"+millisUntilFinished/60000 +":0"+ (millisUntilFinished/1000%60));
                            }
                            else dialog.setMessage("倒數"+millisUntilFinished/60000 +":"+ (millisUntilFinished/1000%60));
                        }
                        @Override
                        public void onFinish()
                        {
                            dialog.dismiss();
                            Intent intent1 = new Intent(CountdownTimerService.this, MyService.class);
                            startService(intent1);
                        }
                    }.start();
                }
            }

            @Override
            public void onTick(long millisUntilFinished)
            {
                Log.d(TAG, "onTick 0");
                //計時器判斷每秒如果是暗的話直接停止countDownTimer
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                if (!pm.isInteractive())
                {
                    countDownTimer.cancel();
                }
            }
        }.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    //服務如果呼叫stopService (會執行這裡的指令)
    public void onDestroy()
    {
        super.onDestroy();
        //停止countDownTimer的計時器
        if(countDownTimer != null)
        {
            countDownTimer.cancel();
        }
        //停止lockCountDownTimer的計時器
        if(lockCountDownTimer != null){
            lockCountDownTimer.cancel();
        }
        Log.d(TAG, "onDestroy() executed");
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
