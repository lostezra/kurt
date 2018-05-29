package com.aye.kurtsee.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.aye.kurtsee.R;
import com.huawei.android.hms.agent.HMSAgent;
import com.huawei.android.hms.agent.common.HMSAgentLog;
import com.huawei.android.hms.agent.common.handler.ConnectHandler;
import com.huawei.android.hms.agent.push.handler.GetTokenHandler;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.push.HuaweiPush;
import com.huawei.hms.support.api.push.TokenResult;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.EasyUtils;
import com.aye.kurtsee.utilis.Helper;

/*开屏页*/
public class SplashActivity extends AppCompatActivity {

    private static final int sleepTime = 2000;
    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.splash);
        super.onCreate(savedInstanceState);

        //连接华为移动服务
        HMSAgent.connect(this, new ConnectHandler() {
            @Override
            public void onConnect(int rst) {
                HMSAgentLog.d("HMS connect end:" + rst);
            }
        });

        getToken();

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

        int userType = mSharedPreferences.getInt("user_type", 0);
        boolean autoLogin = mSharedPreferences.getBoolean("auto_login", false);
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
            } else if(userType == 0){
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

    /**
     * 获取 token
     */
    private void getToken() {
        Log.e("HMS", "get token: begin");
        HMSAgent.Push.getToken(new GetTokenHandler() {

            @Override
            public void onResult(int rst) {
                Log.e("HMS","get token: end" + rst);
            }


        });
    }

}


