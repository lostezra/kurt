package com.aye.kurtsee.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.aye.kurtsee.R;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

public class WelcomeActivity extends AppCompatActivity {

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mSharedPreferences = getSharedPreferences("kurtsee", MODE_PRIVATE);
        mEdit = mSharedPreferences.edit();

        findViewById(R.id.enter_blind_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WelcomeActivity.this, LoginActivity.class);
                mEdit.putInt("user_type", 0);
                mEdit.commit();
                startActivity(i);
            }
        });

        findViewById(R.id.enter_volunteer_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(WelcomeActivity.this, LoginActivity.class);
                mEdit.putInt("user_type", 1);
                mEdit.commit();
                startActivity(i);
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logout();
    }

    private void logout() {
        EMClient.getInstance().logout(true, new EMCallBack() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                Log.e("kurt", "退出成功");

            }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(int code, String message) {
                // TODO Auto-generated method stub
                Log.e("kurt", "退出失败：" + code + "," + message);
            }
        });
    }
}
