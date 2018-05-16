package com.aye.kurtsee.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aye.kurtsee.R;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginBlindActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REGIST_WHAT_FAILE = 1;
    private static final int REGIST_WHAT_SUCCESS = 0;
    private static final int LOGIN_WHAT_SUCCESS=2;
    private static final int LOGIN_WHAT_FAIL=3;
    private Button btn_login, btn_register;
    private EditText et_username, et_password;
    private HttpURLConnection mHttpURLConnection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        setListener();
    }

    private void setListener() {
        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
    }

    private void initView() {
        btn_login= (Button) findViewById(R.id.btn_login);
        btn_register = (Button) findViewById(R.id.btn_register);
        et_username= (EditText) findViewById(R.id.et_username);
        et_password= (EditText) findViewById(R.id.et_password);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_register:
                register();
                break;

        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case REGIST_WHAT_SUCCESS:
                    Toast.makeText(LoginBlindActivity.this,"注册成功", Toast.LENGTH_LONG).show();
                    break;
                case REGIST_WHAT_FAILE:
                    String dis= (String) msg.obj;
                    Toast.makeText(LoginBlindActivity.this, dis, Toast.LENGTH_LONG).show();
                    break;
                case LOGIN_WHAT_SUCCESS:
                    Toast.makeText(LoginBlindActivity.this,"登录成功", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(LoginBlindActivity.this, MainBlindActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };

    private void register() {
        final String password=et_password.getText().toString().trim();
        final String username=et_username.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            //Toast.makeText(this, getResources().getString(R.string.User_name_cannot_be_empty), Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginBlindActivity.this);
            builder.setTitle(R.string.username);
            builder.setMessage(R.string.User_name_cannot_be_empty);
            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            et_username.requestFocus();
            return;
        } else if (!checkUsername()){
            //Toast.makeText(this, getResources().getString(R.string.username_illegal), Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginBlindActivity.this);
            builder.setTitle(R.string.username);
            builder.setMessage(R.string.username_illegal);
            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            et_username.requestFocus();
            return;
        } else if (TextUtils.isEmpty(password)) {
            //Toast.makeText(this, getResources().getString(R.string.Password_cannot_be_empty), Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginBlindActivity.this);
            builder.setTitle(R.string.password);
            builder.setMessage(R.string.Password_cannot_be_empty);
            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            et_password.requestFocus();
            return;
        } else if (!checkPassword()){
            //Toast.makeText(this, getResources().getString(R.string.password_illegal), Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginBlindActivity.this);
            builder.setTitle(R.string.password);
            builder.setMessage(R.string.password_illegal);
            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            et_password.requestFocus();
            return;
        }

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)){
            new Thread(new Runnable() {
                @Override
                public void run() {

                    Message m=new Message();
                    String isSuccess="";
                    String URLSTRING1="http://106.14.196.127:8080/kanjian-server-maven/addBlindUser.action?language=1&region=0&name=" + username +"&password=" + password +"&gender=0";
                    try{
                        JSONObject json=new JSONObject(getConnectionContent(URLSTRING1));
                        String dataMap= json.getString("dataMap");
                        JSONObject dM=new JSONObject(dataMap);
                        isSuccess=dM.getString("success");

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                    m.obj=isSuccess;

                    if(isSuccess=="true"){
                        m.what=REGIST_WHAT_SUCCESS;
                        mHandler.sendMessage(m);
                    } else {
                        m.what=REGIST_WHAT_FAILE;
                        mHandler.sendMessage(m);
                    }

                    //注册失败会抛出HyphenateException
                    /*try {
                        EMClient.getInstance().createAccount(username, password);//同步方法
                        Log.e("kurt", "注册成功");
                        Message message=Message.obtain();
                        message.what=REGIST_WHAT_SUCCESS;
                        mHandler.sendMessage(message);
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        Log.e("kurt", "注册失败" + ":" + e.getErrorCode() + "," + e.getDescription());
                        Message message=Message.obtain();
                        message.obj="注册失败" + ":" + e.getErrorCode() + "," + e.getDescription();
                        message.what=REGIST_WHAT_FAILE;
                        mHandler.sendMessage(message);
                    }*/
                }
            }).start();
        }

    }

    private void login() {
        String password=et_password.getText().toString().trim();
        String userName=et_username.getText().toString().trim();
        //回调
        EMClient.getInstance().login(userName,password,new EMCallBack() {
            @Override
            public void onSuccess() {
                Message m=new Message();

                String isSuccess="";
                String password="";
                //是否是 Blind
                //用户登录等的验证
                //成功返回密码
                String URLSTRING3="http://106.14.196.127:8080/kanjian-server-maven/isBlind.action?name=" + et_username;
                try{
                    JSONObject json=new JSONObject(getConnectionContent(URLSTRING3));
                    String dataMap= json.getString("dataMap");
                    JSONObject dM=new JSONObject(dataMap);
                    isSuccess=dataMap;
                    isSuccess=dM.getString("isVolunteer");
                    if(isSuccess=="true"){
                        password=dM.getString("password");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                m.obj=password;
                Message message=Message.obtain();
                message.what=LOGIN_WHAT_SUCCESS;
                mHandler.sendMessage(message);
            }

            @Override
            public void onProgress(int progress, String status) {
                Log.e("kurt","登录状态："+progress+"=="+status);
            }

            @Override
            public void onError(int code, final String message) {
                Log.d("main", "登录聊天服务器失败！");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(LoginBlindActivity.this,"登录聊天服务器失败:"+message,Toast.LENGTH_LONG).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginBlindActivity.this);
                        builder.setTitle("登录失败");
                        builder.setMessage(message);
                        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
            }
        });
    }

    private String getConnectionContent(String urlstring){
        InputStream inputStream=null;
        try{
            URL url=new URL(urlstring);
            mHttpURLConnection=(HttpURLConnection) url.openConnection();
            mHttpURLConnection.setConnectTimeout(5*1000);
            mHttpURLConnection.setReadTimeout(5*1000);
            mHttpURLConnection.setRequestMethod("GET");
            inputStream=mHttpURLConnection.getInputStream();
            String response=convertStreamToString(inputStream);
            return response;
        }catch (MalformedURLException e){
            e.printStackTrace();
            return "";
        }catch (IOException e){
            e.printStackTrace();
            return "";
        }finally {
            if(inputStream!=null){
                try {
                    inputStream.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            if (mHttpURLConnection!=null){
                mHttpURLConnection.disconnect();
            }
        }
    }

    private String convertStreamToString(InputStream is)throws IOException{
        BufferedReader reader =new BufferedReader(new InputStreamReader(is));
        StringBuffer sb=new StringBuffer();
        String line;
        while ((line=reader.readLine())!=null){
            sb.append(line+"\n");
        }
        String respose=sb.toString();
        if(reader!=null){
            reader.close();
        }
        return respose;
    }

    /*
     * 验证用户名
     * @return boolean
     */
    public boolean checkUsername(){
        String regex = "([a-zA-Z0-9]*)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(et_username.getText().toString().trim());
        return m.matches();
    }

    /*
     * 验证密码
     * @return boolean
     */
    public boolean checkPassword(){
        String regex = "([a-zA-Z0-9]{4,12})";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(et_password.getText().toString().trim());
        return m.matches();
    }

}
