package com.aye.kurtsee;

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
            inputStream=mHttpURLConnection.getInputStream();
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
}
