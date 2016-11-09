package com.runhuaoil.yyweather.util;

/**
 * Created by RunHua on 2016/10/19.
 * 网络请求返回结果的回调接口
 */

public interface HttpCallBack {

    void onFinish(String responseData);

    void onError(Exception e);
}
