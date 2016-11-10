package com.runhuaoil.yyweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.runhuaoil.yyweather.receiver.AutoUpdateReceiver;
import com.runhuaoil.yyweather.util.MySharedPreferences;
import com.runhuaoil.yyweather.util.UpdateUtil;




/**
 * Created by RunHua on 2016/10/31.
 *  后台更新服务
 */

public class AutoUpdateService extends Service {

    private AlarmManager alarmMgr;
    private PendingIntent pi;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();

        //更新间隔
        long lifeTime = SystemClock.elapsedRealtime() + 6 * 60 * 60 * 1000;

        Intent i = new Intent(this, AutoUpdateReceiver.class);
        pi = PendingIntent.getBroadcast(this, 0, i,0);

        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmMgr.set(AlarmManager.ELAPSED_REALTIME, lifeTime, pi);

        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather(){
        SharedPreferences pre = MySharedPreferences.getInstance(this);
        String countyName = pre.getString("countyName", "");

        UpdateUtil.getWeatherData(countyName, this, null);

    }

    @Override
    public void onDestroy() {
        if (alarmMgr != null){
            alarmMgr.cancel(pi);
        }
        super.onDestroy();
    }
}
