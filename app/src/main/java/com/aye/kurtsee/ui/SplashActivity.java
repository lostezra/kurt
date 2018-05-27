package com.aye.kurtsee.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.aye.kurtsee.R;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.EasyUtils;
import com.aye.kurtsee.Helper;

/*开屏页*/
public class SplashActivity extends AppCompatActivity {

    private static final int sleepTime = 2000;
    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.splash);
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onStart() {
        super.onStart();

        new Thread(new Runnable() {
            public void run() {

                autoLogin();

            }
        }).start();

    }


    protected void autoLogin(){
        mSharedPreferences = getSharedPreferences("kurtsee", MODE_PRIVATE);

        int user_type = mSharedPreferences.getInt("user_name", 0);
        boolean autoLogin = mSharedPreferences.getBoolean("autoLogin", false);
        Helper helper = new Helper();
        if (helper.isLoggedIn() && autoLogin ) {
            //自动登录，进入主界面
            long start = System.currentTimeMillis();
            long costTime = System.currentTimeMillis() - start;
            //wait
            if (sleepTime - costTime > 0) {
                try {
                    Thread.sleep(sleepTime - costTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            String topActivityName = EasyUtils.getTopActivityName(EMClient.getInstance().getContext());
            if (topActivityName != null && topActivityName.equals(VideoChatActivity.class.getName())) {
                // nop
                // avoid main screen overlap Calling Activity
            } else if(user_type == 0){
                //盲人 type = 0, 志愿者 type = 1
                startActivity(new Intent(SplashActivity.this, MainBlindActivity.class));
            } else{
                startActivity(new Intent(SplashActivity.this, MainVolunteerActivity.class));
            }
            finish();
        }else {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
            }
            startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
            finish();
        }
    }



}


