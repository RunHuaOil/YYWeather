package com.runhuaoil.yyweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by RunHua on 2016/10/31.
 */

public class MySharedPreferences {

    private static SharedPreferences pre;
    private static SharedPreferences.Editor editor;

    private MySharedPreferences(){

    }

    public synchronized static SharedPreferences getInstance(Context context){
        if (pre == null){
            pre = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return pre;
    }

    public synchronized static SharedPreferences.Editor getEditor(Context context){
        if (editor == null){
            if (pre == null){
                editor = getInstance(context).edit();
            }else {
                editor = pre.edit();
            }
        }
        return editor;
    }

}
