package com.runhuaoil.yyweather.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.runhuaoil.yyweather.R;
import com.runhuaoil.yyweather.util.CompleteCallBack;
import com.runhuaoil.yyweather.util.MySharedPreferences;
import com.runhuaoil.yyweather.util.UpdateUtil;

/**
 * Created by RunHua on 2016/11/7.
 *  天气Widget的重要类
 */

public class WeatherWidgetProvider extends AppWidgetProvider {

    private static final String UPDATE_ACTION = "com.runhuaoil.yyweather.WIDGET_UPDATE";
    private static final int NO_WIDGET_ID = -1;

    public static void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        SharedPreferences pre = MySharedPreferences.getInstance(context);
        String currentTemp = pre.getString("currentTemp", "") + "°";
        String countyName = pre.getString("countyName", "");
        String highTemp = pre.getString("highTemp" + 0, "  ").split(" ")[1];
        String lowTemp = pre.getString("lowTemp" + 0, "  ").split(" ")[1];
        String weatherType =  pre.getString("weatherType" + 0, "");


        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget_layout);
        views.setTextViewText(R.id.widget_currentTemp_text, currentTemp);
        views.setTextViewText(R.id.widget_county_text, countyName);
        views.setTextViewText(R.id.widget_high_temp_text, highTemp);
        views.setTextViewText(R.id.widget_low_temp_text, lowTemp);
        views.setTextViewText(R.id.widget_weather_type_text, weatherType);

        if (appWidgetId != NO_WIDGET_ID){
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }else {
            appWidgetManager.updateAppWidget(new ComponentName(context, WeatherWidgetProvider.class), views);
        }


    }

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        SharedPreferences pre = MySharedPreferences.getInstance(context);
        String countyName = pre.getString("countyName","");
        if (!TextUtils.isEmpty(countyName)){
            //系统定时调用 widget 的onUpdate方法，在此方法更新最新天气并显示
            UpdateUtil.getWeatherData(countyName, context, new CompleteCallBack() {
                @Override
                public void onComplete() {
                    for (int widgetId : appWidgetIds) {
                        updateWidget(context, appWidgetManager, widgetId);
                    }
                }
            });
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        //当在 WeatherActivity 中重新选择城市后 实时 更新桌面小部件的信息
        if (UPDATE_ACTION.equals(intent.getAction())){
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            updateWidget(context, appWidgetManager, NO_WIDGET_ID);
        }
    }
}
