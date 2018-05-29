package com.aye.kurtsee.ui;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aye.kurtsee.utilis.Country;
import com.aye.kurtsee.utilis.CountryPicker;
import com.aye.kurtsee.utilis.OnPick;
import com.aye.kurtsee.R;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

public class Settings extends AppCompatActivity {

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEdit;

    private ImageView ivFlag;
    private TextView tvName;
    private TextView tvCode;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mSharedPreferences = getSharedPreferences("kurtsee", MODE_PRIVATE);
        mEdit = mSharedPreferences.edit();

        ivFlag = (ImageView) findViewById(R.id.iv_flag);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvCode = (TextView) findViewById(R.id.tv_code);


        findViewById(R.id.change_region).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CountryPicker.newInstance(null, new OnPick() {
                    @Override
                    public void onPick(Country country) {
                        if(country.flag != 0) ivFlag.setImageResource(country.flag);
                        tvName.setText(country.name);
                        tvCode.setText("+" + country.code);
                    }
                }).show(getSupportFragmentManager(), "country");
            }
        });
        findViewById(R.id.change_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.this, ChangePasswordActivity.class));
            }
        });

        findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();

                mEdit.putBoolean("autoLogin", false);
                mEdit.commit();

                Intent i = new Intent(Settings.this, WelcomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });



    }




    @Override
    protected void onDestroy() {
        Country.destroy();
        super.onDestroy();
    }

    private void logout() {
        EMClient.getInstance().logout(true, new EMCallBack() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                Log.e("kurt", "退出成功");
                finish();
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
