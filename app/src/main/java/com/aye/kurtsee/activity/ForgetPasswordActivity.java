package com.aye.kurtsee.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.aye.kurtsee.R;

public class ForgetPasswordActivity extends AppCompatActivity{

    private EditText et_email;
    private Button btn_reset_password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword);

        resetpwd();
    }

    private void resetpwd(){
        et_email = (EditText) findViewById(R.id.et_email);
        btn_reset_password = (Button) findViewById(R.id.btn_reset_password);

        btn_reset_password.setOnClickListener(null);

    }


}
