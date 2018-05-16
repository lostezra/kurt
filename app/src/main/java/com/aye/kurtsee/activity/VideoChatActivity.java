package com.aye.kurtsee.activity;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aye.kurtsee.R;
import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.EMNoActiveCallException;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.media.EMCallSurfaceView;


public class VideoChatActivity  extends AppCompatActivity implements View.OnClickListener {
    //通话状态监听器
    protected EMCallStateChangeListener callStateListener;
    // 视频通话画面显示控件，这里在新版中使用同一类型的控件，方便本地和远端视图切换
    protected EMCallSurfaceView localSurface;
    protected EMCallSurfaceView oppositeSurface;
    private int surfaceState = -1;
    private String openType,from;
    private TextView tv_status;
    private Button btn_answer ,btn_refuse, btn_end;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat);
        initView();
        setListener();

        EMClient.getInstance().callManager().setSurfaceView(localSurface, oppositeSurface);
        try {
            EMClient.getInstance().callManager().setCameraFacing(Camera.CameraInfo.CAMERA_FACING_BACK);
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        btn_answer= (Button) findViewById(R.id.btn_answer);
        btn_refuse= (Button) findViewById(R.id.btn_refuse);
        btn_end= (Button) findViewById(R.id.btn_end);
        tv_status= (TextView) findViewById(R.id.tv_status);

        // 本地投影
        localSurface = (EMCallSurfaceView) findViewById(R.id.local_surface);
        localSurface.setOnClickListener(this);
        localSurface.setZOrderMediaOverlay(true);
        localSurface.setZOrderOnTop(true);

        // 远程投影
        oppositeSurface = (EMCallSurfaceView) findViewById(R.id.opposite_surface);

        //设置Surface
        EMClient.getInstance().callManager().setSurfaceView(localSurface, oppositeSurface);

        openType=getIntent().getStringExtra("openType");
        if("1".equals(openType)){
             from=getIntent().getStringExtra("from");
            tv_status.setText(from+"请求视频通话");
        }

    }

    private void showView(int callStatus){
        btn_answer.setVisibility(View.VISIBLE);
        btn_refuse.setVisibility(View.VISIBLE);
        btn_end.setVisibility(View.VISIBLE);
      switch (callStatus){
          case 1://正在连接
              btn_end.setVisibility(View.GONE);
              tv_status.setText(from+"视频通话中");
              break;
          case 2://连接成功
              btn_answer.setVisibility(View.GONE);
              break;
          case 3://连接断开
              finish();
              break;
      }

    }

    private void setListener() {
        btn_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answerCall();
            }
        });
        btn_refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refuseCall();
            }
        });
        btn_end.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                endCall();
            }
        });
        localSurface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeCallView();
            }
        });
        // 设置通话状态
        addCallStateListener();
    }

    /**
     * 终止通话
     */
    private void endCall() {
        /**
         * 挂断通话
         */
        try {
            EMClient.getInstance().callManager().endCall();
            finish();
        } catch (EMNoActiveCallException e) {

        }
    }

    /**
     * 接听通话
     */
    public void answerCall(){

        try {
            EMClient.getInstance().callManager().answerCall();
            Log.e("kurt","接听成功");
        } catch (EMNoActiveCallException e) {
            // TODO Auto-generated catch block
            Log.e("kurt","接听失败"+e.toString());
            e.printStackTrace();
        }

    }

    /**
     * 拒绝接听
     */
    public void refuseCall(){
        /**
         * 拒绝接听
         * @throws EMNoActiveCallException
         */
        try {
            EMClient.getInstance().callManager().rejectCall();
            Log.e("kurt", "拒觉成功");
            finish();
        } catch (EMNoActiveCallException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("kurt", "拒绝失败" + e.toString());
        }
    }

    /**
     * 切换通话界面，这里就是交换本地和远端画面控件设置，以达到通话大小画面的切换
     */
    private void changeCallView() {
        if (surfaceState == 0) {
            surfaceState = 1;
            EMClient.getInstance().callManager().setSurfaceView(oppositeSurface, localSurface);
        } else {
            surfaceState = 0;
            EMClient.getInstance().callManager().setSurfaceView(localSurface, oppositeSurface);
        }
    }

    private void addCallStateListener() {
        callStateListener=new EMCallStateChangeListener() {
            @Override
            public void onCallStateChanged(CallState callState, CallError callError) {
                switch (callState) {
                    case CONNECTING: // 正在连接对方
                        Log.e("kurt", "正在连接对方....");

                        break;
                    case CONNECTED: // 双方已经建立连接
                        Log.e("kurt","建立连接....");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showView(1);
                            }
                        });
                        break;

                    case ACCEPTED: // 电话接通成功
                        Log.e("kurt","连接成功....");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showView(2);
                            }
                        });
                        break;
                    case NETWORK_DISCONNECTED: // 电话断了
                        Log.e("kurt","连接断开....");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showView(3);
                            }
                        });
                        break;
                    case NETWORK_UNSTABLE: //网络不稳定
                        if(callError == CallError.ERROR_NO_DATA){
                            //无通话数据
                            Log.e("kurt","无通话数据....");
                        }else{
                            Log.e("kurt","网络不稳定....");
                        }
                        break;
                    case NETWORK_NORMAL: //网络恢复正常
                        Log.e("kurt","网络恢复正常....");
                        break;
                    default:
                        break;
                }

            }
        };

        EMClient.getInstance().callManager().addCallStateChangeListener(callStateListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().callManager().removeCallStateChangeListener(callStateListener);
    }

    @Override
    public void onClick(View v) {


    }
}
