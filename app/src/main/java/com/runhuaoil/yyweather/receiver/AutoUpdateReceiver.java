package com.runhuaoil.yyweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.runhuaoil.yyweather.service.AutoUpdateService;

/**
 * Created by RunHua on 2016/10/31.
 * 通过 AlarmManager 定时来发送广播，该类接收广播后启动服务去获取最新天气
 */

public class AutoUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, AutoUpdateService.class);
        context.startService(serviceIntent);
    }
}
