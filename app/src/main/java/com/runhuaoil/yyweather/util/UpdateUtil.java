package com.runhuaoil.yyweather.util;


import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by RunHua on 2016/11/8.
 *
 */

public class UpdateUtil {

    public static void getWeatherData(String countyName, final Context context,final CompleteCallBack completeCallBack){

        if (!TextUtils.isEmpty(countyName)){
            String address1 = null;
            try {
                address1 = "http://wthrcdn.etouch.cn/weather_mini?city=" + URLEncoder.encode(countyName,"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            HttpUtil.sendHttpRequest(address1, new HttpCallBack() {
                @Override
                public void onFinish(String responseData) {
                    Boolean result = ResponseHandle.handleWeatherData(responseData, context);
                    if (result){
                        if (completeCallBack != null){
                            completeCallBack.onComplete();
                        }
                    }
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(context, "更新天气失败,请检查网络状态", Toast.LENGTH_SHORT).show();
                }
            });

            String address2 = null;
            try {
                address2 = "http://wthrcdn.etouch.cn/WeatherApi?city=" + URLEncoder.encode(countyName,"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            HttpUtil.sendHttpRequest(address2, new HttpCallBack() {
                @Override
                public void onFinish(String responseData) {
                    ResponseHandle.handlePublishTime(responseData, context);
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(context, "更新天气失败,请检查网络状态", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
