package com.aye.kurtsee.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.aye.kurtsee.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ChangePasswordActivity extends AppCompatActivity {

    private HttpURLConnection mHttpURLConnection;

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

        EditText new_password = (EditText) findViewById(R.id.et_new_password);
        final String newPassword=new_password.getText().toString().trim();

        EditText username = (EditText) findViewById(R.id.et_username);
        final String userName = username.getText().toString().trim();

        EditText old_password = (EditText) findViewById(R.id.et_old_password);
        final String oldPassword = old_password.getText().toString().trim();

        EditText confirm_new_password = (EditText) findViewById(R.id.et_confirm_new_password);
        final String confirmPassword = confirm_new_password.getText().toString().trim();

        if (newPassword!=confirmPassword){
            Toast.makeText(this, getResources().getString(R.string.Two_input_password),Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                String URLSTRING3="http://106.14.196.127:8080/kanjian-server-maven/isBlind.action?name=" + userName;
                String URLSTRING4="http://106.14.196.127:8080/kanjian-server-maven/isVolunteer.action?name=" + userName;
                String URLSTRING8 = "http://106.14.196.127:8080/kanjian-server-maven/blindChangePassword.action?name=" + userName +"&password=" + newPassword;
                String URLSTRING9 = "http://106.14.196.127:8080/kanjian-server-maven/volunteerChangePassword.action?name=" + userName + "&password=" + newPassword;

                Message m=new Message();

                String isSuccess="",isBlind="",isVolunteer="";
                try{
                    JSONObject json=new JSONObject(getConnectionContent(URLSTRING3));
                    String dataMap= json.getString("dataMap");
                    JSONObject dM=new JSONObject(dataMap);
                    isBlind=dM.getString("isblind");

                    JSONObject json1=new JSONObject(getConnectionContent(URLSTRING4));
                    String dataMap1= json1.getString("dataMap");
                    JSONObject dM1=new JSONObject(dataMap1);
                    isVolunteer=dM1.getString("isVolunteer");
                    if(isBlind=="true"){
                        JSONObject json2=new JSONObject(getConnectionContent(URLSTRING8));
                        String dataMap2= json2.getString("dataMap");
                        JSONObject dM2=new JSONObject(dataMap2);
                        isSuccess=dM2.getString("success");
                    }else if(isVolunteer=="true"){
                        JSONObject json3=new JSONObject(getConnectionContent(URLSTRING9));
                        String dataMap3= json3.getString("dataMap");
                        JSONObject dM3=new JSONObject(dataMap3);
                        isSuccess=dM3.getString("success");
                    }else {
                        isSuccess="false";
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                isSuccess=getConnectionContent(URLSTRING1);
                m.obj=isSuccess;
                //Toast.makeText(this, getResources().getString(R.string.Password_changing_succeed),Toast.LENGTH_SHORT).show();
            }

        }).start();


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
     *//*
    public boolean checkUsername(){
        String regex = "([a-zA-Z0-9]*)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher( username.getText().toString().trim());
        return m.matches();
    }

    *//*
     * 验证密码
     * @return boolean
     *//*
    public boolean checkPassword(){
        String regex = "([a-zA-Z0-9]{4,12})";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(newassword.getText().toString().trim());
        return m.matches();
    }*/
}
