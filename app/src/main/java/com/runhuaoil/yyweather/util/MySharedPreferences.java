package com.runhuaoil.yyweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by RunHua on 2016/10/31.
 * 封装 getDefaultSharedPreferences 方法，返回同一个 SharedPreferences，减少资源浪费
 */

public class MySharedPreferences {

    private static SharedPreferences pre;

    private MySharedPreferences(){

    }

    public synchronized static SharedPreferences getInstance(Context context){
        if (pre == null){
            pre = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return pre;
    }

}
