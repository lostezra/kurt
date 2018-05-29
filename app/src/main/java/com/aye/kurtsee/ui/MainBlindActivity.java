package com.aye.kurtsee.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.aye.kurtsee.R;
import com.aye.kurtsee.utilis.Helper;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.EMServiceNotReadyException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainBlindActivity extends AppCompatActivity{


    private Message msg;

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


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 视频通话
     */
    private static final String URLSTRING="http://106.14.196.127:8080/kanjian-server-maven/findVolunteer.action?language=1&region=0";
    private void videoChat() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //得到志愿者用户名数组
                MatchVolunteer mv=new MatchVolunteer();
                Helper helper = new Helper();
                try{
                    JSONObject json=new JSONObject(helper.getConnectionContent(URLSTRING));
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


                String printStr=mv.getSuccess();
                String []usernameArray=mv.getUsername();

                //调试用
                //打印所有符合的志愿者
                for (int i=0;usernameArray[i]!=null;i++){
                    printStr+=" ";
                    printStr+=usernameArray[i];
                }
                String alluser = printStr;
                Log.e("kurt", "所有 "+alluser);

                //跳转视频聊天界面
                Intent intent=new Intent(MainBlindActivity.this, VideoChatActivity.class);
                startActivity(intent);

                //每隔10秒向下一位志愿者发送通话
                try{
                    for (int i = 0;i <= usernameArray.length;i++){
                        printStr = usernameArray[i];
                        String userName = printStr;

                        //拨打志愿者
                        EMClient.getInstance().callManager().makeVideoCall(userName);
                        Log.e("kurt", "拨打志愿者："+userName);

                        //等待10s
                        Thread.sleep(2*1000);
                    }
                }catch (EMServiceNotReadyException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.e("kurt","视频拨打失败："+e.toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();


    }





}
