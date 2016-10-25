package com.runhuaoil.yyweater.util;


import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by RunHua on 2016/10/19.
 */

public class HttpUtil {

    public static void sendHttpRequest(final String address, final HttpCallBack callBack){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                InputStream inputStream = null;
                BufferedReader reader = null;
                try{
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setRequestMethod("GET");
                    connection.connect();
                    if (connection.getResponseCode() == 200){
                        Log.d("Test","SuccessConncet");
                        inputStream = connection.getInputStream();

                        StringBuilder builder = new StringBuilder();
                        reader = new BufferedReader(new InputStreamReader(inputStream));
                        String line;
                        while( (line = reader.readLine()) != null){
                            builder.append(line);
                        }

                        if (callBack != null){
                            callBack.onFinish(builder.toString());
                        }
                    }
                }catch (Exception e){
                    if (callBack != null){
                        callBack.onError(e);
                    }
                }finally {
                    if (connection != null){
                        connection.disconnect();
                    }
                    if (reader != null){
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }


        }).start();

    }


}
