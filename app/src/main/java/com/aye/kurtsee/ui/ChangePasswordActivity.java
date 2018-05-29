package com.aye.kurtsee.ui;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.aye.kurtsee.R;
import com.aye.kurtsee.utilis.Helper;

import org.json.JSONException;
import org.json.JSONObject;


public class ChangePasswordActivity extends AppCompatActivity {

    private SharedPreferences mSharedPreferences;
    private Helper helper = new Helper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);


        findViewById(R.id.btn_change_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changePassword();
            }
        });
    }


    public void changePassword(){

        final EditText et_old_password = (EditText) findViewById(R.id.et_old_password);
        final String oldPassword = et_old_password.getText().toString().trim();

        EditText et_new_password = (EditText) findViewById(R.id.et_new_password);
        final String newPassword=et_new_password.getText().toString().trim();

        EditText et_confirm_new_password = (EditText) findViewById(R.id.et_confirm_new_password);
        final String confirmPassword = et_confirm_new_password.getText().toString().trim();

        if (!newPassword.equals(confirmPassword)){
            Toast.makeText(this, getResources().getString(R.string.Two_input_password),Toast.LENGTH_SHORT).show();
            return;
        }



        new Thread(new Runnable() {
            @Override
            public void run() {

                mSharedPreferences = getSharedPreferences("kurtsee", MODE_PRIVATE);
                int userType = mSharedPreferences.getInt("user_type", 0);
                String userName = mSharedPreferences.getString("username", "");

                String URLSTRING3="http://106.14.196.127:8080/kanjian-server-maven/isBlind.action?name=" + userName;
                String URLSTRING4="http://106.14.196.127:8080/kanjian-server-maven/isVolunteer.action?name=" + userName;
                String URLSTRING8 = "http://106.14.196.127:8080/kanjian-server-maven/blindChangePassword.action?name=" + userName +"&password=" + newPassword;
                String URLSTRING9 = "http://106.14.196.127:8080/kanjian-server-maven/volunteerChangePassword.action?name=" + userName + "&password=" + newPassword;

                //Message m=new Message();

                String isSuccess="";

                String password;

                try{

                    if(userType == 0){
                        //盲人 type = 0, 志愿者 type = 1
                        JSONObject json=new JSONObject(helper.getConnectionContent(URLSTRING3));
                        String dataMap= json.getString("dataMap");
                        JSONObject dM=new JSONObject(dataMap);
                        password=dM.getString("password");

                        if (!oldPassword.equals(password)){
                            AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this);
                            builder.setTitle(R.string.password);
                            builder.setMessage(R.string.wrong_password);
                            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                            et_old_password.requestFocus();
                            return;
                        } else {
                            JSONObject json2=new JSONObject(helper.getConnectionContent(URLSTRING8));
                            String dataMap2= json2.getString("dataMap");
                            JSONObject dM2=new JSONObject(dataMap2);
                            isSuccess=dM2.getString("success");
                        }
                    } else{
                        JSONObject json1=new JSONObject(helper.getConnectionContent(URLSTRING4));
                        String dataMap1= json1.getString("dataMap");
                        JSONObject dM1=new JSONObject(dataMap1);
                        password=dM1.getString("password");

                        if (!oldPassword.equals(password)) {
                            Toast.makeText(ChangePasswordActivity.this, getResources().getString(R.string.wrong_password), Toast.LENGTH_SHORT).show();
                            et_old_password.requestFocus();
                            return;
                        } else {
                            helper.getConnectionContent(URLSTRING9);
                            JSONObject json3=new JSONObject(helper.getConnectionContent(URLSTRING9));
                            String dataMap3= json3.getString("dataMap");
                            JSONObject dM3=new JSONObject(dataMap3);
                            isSuccess=dM3.getString("success");
                            Toast.makeText(ChangePasswordActivity.this, getResources().getString(R.string.Password_changing_succeed), Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }).start();


    }

}
