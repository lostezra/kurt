package com.aye.kurtsee.utilis;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.aye.kurtsee.R;
import com.hyphenate.chat.EMClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Helper {

    /**
     * if ever logged in
     *
     * @return
     */
    public boolean isLoggedIn() {
        return EMClient.getInstance().isLoggedInBefore();
    }


    private HttpURLConnection mHttpURLConnection;

    public String getConnectionContent(String urlstring){
        InputStream inputStream=null;
        try{
            URL url=new URL(urlstring);
            mHttpURLConnection=(HttpURLConnection) url.openConnection();
            mHttpURLConnection.setConnectTimeout(5*1000);
            mHttpURLConnection.setReadTimeout(5*1000);
            mHttpURLConnection.setRequestMethod("GET");
            // 调用 HttpURLConnection 连接对象的 getInputStream() 函数,
            // 将内存缓冲区中封装好的完整的 HTTP 请求电文发送到服务端。
            inputStream=mHttpURLConnection.getInputStream();
            Log.e("http", String.valueOf(inputStream));
            String response= convertStreamToString(inputStream);
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
    public String convertStreamToString(InputStream is)throws IOException {
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


    public void popAlertDialog(Context context, int title, int message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return;

    }
}
