package com.aye.kurtsee.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.aye.kurtsee.R;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.EMServiceNotReadyException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainBlindActivity extends AppCompatActivity{

    private HttpURLConnection mHttpURLConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_blind);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        //如果需要给toolbar设置事件监听，需要将toolbar设置支持actionbar
        setSupportActionBar(toolbar);

        //toolbar的menu点击事件的监听
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(MainBlindActivity.this, Settings.class));
                return true;
            }
        });

        findViewById(R.id.make_video_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoChat();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //加载menu文件到布局
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }



    /**
     * 视频通话
     */
    //private static final String URLSTRING="http://106.14.196.127:8080/kanjian-server-maven/findVolunteer.action?language=1&region=0";
    private void videoChat() {
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                MatchVolunteer mv=new MatchVolunteer();
                try{
                    JSONObject json=new JSONObject(getConnectionContent(URLSTRING));
                    String dataMap= json.getString("dataMap");
                    JSONObject dM=new JSONObject(dataMap);
                    mv.setSuccess(dM.getString("success"));
                    String data=dM.getString("data");
                    JSONArray dt=new JSONArray(data);
                    String[] sa=new String[100000];
                    for (int i=0;i<dt.length();i++){
                        JSONObject dtElement=dt.getJSONObject(i);
                        if(dtElement!=null){
                            sa[i]=dtElement.getString("name");
                        }
                    }
                    mv.setUsername(sa);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).start();

        MatchVolunteer mv=(MatchVolunteer) msg.obj;
        String printStr=mv.getSuccess();
        String []usernameArray=mv.getUsername();
        for (int i=0;usernameArray[i]!=null;i++){
            printStr+=" ";
            printStr+=usernameArray[i];
        }
        String userName = printStr;*/

        String userName = "v0";

        try {//单参数
            EMClient.getInstance().callManager().makeVideoCall(userName);
            //跳转视频聊天界面
            Intent intent=new Intent(MainBlindActivity.this, VideoChatActivity.class);
            startActivity(intent);
        } catch (EMServiceNotReadyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("kurt","视频拨打失败："+e.toString());
        }

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

    private String convertStreamToString(InputStream is)throws IOException {
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

}
