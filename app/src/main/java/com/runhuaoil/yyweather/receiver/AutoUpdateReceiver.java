package com.runhuaoil.yyweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.runhuaoil.yyweather.service.AutoUpdateService;

/**
 * Created by RunHua on 2016/10/31.
 */

public class AutoUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AutoUpdateService.class);

        context.startService(i);
    }
}
