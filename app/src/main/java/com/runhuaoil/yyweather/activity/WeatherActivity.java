package com.runhuaoil.yyweather.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.runhuaoil.yyweather.R;
import com.runhuaoil.yyweather.dataAdapter.WeatherInfoRecyclerViewAdapter;
import com.runhuaoil.yyweather.service.AutoUpdateService;
import com.runhuaoil.yyweather.util.HttpCallBack;
import com.runhuaoil.yyweather.util.HttpUtil;
import com.runhuaoil.yyweather.util.MySharedPreferences;
import com.runhuaoil.yyweather.util.ResponseHandle;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;


/**
 * Created by RunHua on 2016/10/27.
 *
 */

public class WeatherActivity extends AppCompatActivity {

    private WeatherInfoRecyclerViewAdapter weatherAdapter;
    private View view;
    private MenuItem updateItem;
    private String selectCountyName;//接收从ChooseActivity的Intent存放的 所选择的天气城市

    //queryPublishTime 参数之一，该值用来是否弹出 SnackBar提示更新成功(用在ToolBar刷新按钮,以及网络请求 onError时刷新用)
    private static final int REFRESH_TYPE = 1;
    private static final int NO_REFRESH_TYPE = 2;

    //两种网络请求完成后的标记值，当两种请求都完成时，才通过 showWeatherInfo 更新天气
    private static boolean queryTimeIsOK = false;
    private static boolean queryWeatherIsOK = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_activity_layout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //在5.0以上的系统设置导航栏背景颜色和主题颜色一致。这里为蓝色
            getWindow().setNavigationBarColor(Color.rgb(41, 149, 233));
        }

        view = findViewById(R.id.weather_main_layout);//这里的view是给SnackBar用

        //设置ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.weather_toolbar_id);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_menu_white_24dp));
        setSupportActionBar(toolbar);

        //设置RecyclerView相关属性
        RecyclerView weatherRecyclerView = (RecyclerView) findViewById(R.id.weather_recycler_view);
        weatherRecyclerView.setHasFixedSize(true);
        weatherRecyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        weatherRecyclerView.setLayoutManager(mLayoutManager);
        weatherAdapter = new WeatherInfoRecyclerViewAdapter(this);
        weatherRecyclerView.setAdapter(weatherAdapter);

        //判断 直接显示天气 还是 更新天气
        SharedPreferences pre = MySharedPreferences.getInstance(this);
        selectCountyName = getIntent().getStringExtra("county_name");
        if (!TextUtils.isEmpty(selectCountyName)){
            queryWeatherInfo(selectCountyName);
            queryPublishTime(selectCountyName, NO_REFRESH_TYPE);
        }else{
            if (pre.getBoolean("city_selected", false)){
                queryTimeIsOK = true;
                queryWeatherIsOK = true;
                showWeatherInfo();
            }else{
                Toast.makeText(this, "未选择城市,请重新选择", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void queryWeatherInfo(final String countyName) {
        String address = null;
        try {
            address = "http://wthrcdn.etouch.cn/weather_mini?city=" + URLEncoder.encode(countyName,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpUtil.sendHttpRequest(address, new HttpCallBack() {
            @Override
            public void onFinish(String responseData) {
                //Log.d("Test", "queryWeatherInfo Thread: " + Thread.currentThread().getId() );
                boolean result = ResponseHandle.handleWeatherData(responseData, WeatherActivity.this);
                if (result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            queryWeatherIsOK = true;
                            showWeatherInfo();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(view, "获取天气信息失败,请检查网络连接", Snackbar.LENGTH_INDEFINITE)
                                .setAction("刷新", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        queryWeatherInfo(countyName);
                                        queryPublishTime(countyName, REFRESH_TYPE);
                                    }
                                }).show();
                    }
                });
            }
        });

    }

    private void queryPublishTime(final String countyName, final int type) {
        String address = null;
        try {
            address = "http://wthrcdn.etouch.cn/WeatherApi?city=" + URLEncoder.encode(countyName,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpUtil.sendHttpRequest(address, new HttpCallBack() {
            @Override
            public void onFinish(String responseData) {
                //Log.d("Test", "queryPublishTime Thread: " + Thread.currentThread().getId() );
                boolean result = ResponseHandle.handlePublishTime(responseData, WeatherActivity.this);
                if (result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            queryTimeIsOK = true;
                            showWeatherInfo();

                            if (type == REFRESH_TYPE){
                                Snackbar.make(view, "报告,已经是最新的天气啦", Snackbar.LENGTH_LONG)
                                        .setAction("我知道了", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Toast.makeText(WeatherActivity.this, "么么哒", Toast.LENGTH_SHORT).show();
                                            }
                                        }).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(view, "获取天气信息失败,请检查网络连接", Snackbar.LENGTH_INDEFINITE)
                                .setAction("刷新", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        queryWeatherInfo(countyName);
                                        queryPublishTime(countyName, REFRESH_TYPE);
                                    }
                                }).show();
                    }
                });
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        updateItem = menu.findItem(R.id.menu_item_openAutoUpdate);
        // serviceIsWork() 检查系统后台所有服务中是否有该 应用 的服务，从而确定菜单的标题
        if (serviceIsWork()){
            updateItem.setTitle("关闭后台自动更新");
        }else{
            updateItem.setTitle("开启后台自动更新");
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_refresh:
                SharedPreferences pre = MySharedPreferences.getInstance(this);
                String countyName;
                //防止 本地有储存上一次 地点 ，当网络不好不显示天气时,点击 刷新 会以上一次的地点来查询天气覆盖掉用户所选择的新城市
                if (!TextUtils.isEmpty(selectCountyName)){
                    countyName = selectCountyName;
                }else {
                    countyName = pre.getString("countyName","");
                }

                if (!TextUtils.isEmpty(countyName)){
                    queryWeatherInfo(countyName);
                    queryPublishTime(countyName, REFRESH_TYPE);
                }
                return true;
            case R.id.menu_item_place:
                Intent intent = new Intent(WeatherActivity.this, ChooseAreaActivity.class);
                intent.putExtra("isFromWeatherActivity", true);
                startActivity(intent);
                finish();
                return true;
            case R.id.menu_item_exit:
                finish();
                return true;
            case R.id.menu_item_openAutoUpdate:
                Intent intent1 = new Intent(this, AutoUpdateService.class);

                if (updateItem.getTitle().equals("关闭后台自动更新")){
                    stopService(intent1);
                    Toast.makeText(this, "已关闭后台自动更新", Toast.LENGTH_SHORT).show();
                    updateItem.setTitle("开启后台自动更新");
                }else{
                    startService(intent1);
                    Toast.makeText(this, "已开启后台自动更新", Toast.LENGTH_SHORT).show();
                    updateItem.setTitle("关闭后台自动更新");
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void showWeatherInfo() {
        if (queryTimeIsOK && queryWeatherIsOK){
            // 更新 5 条天气数据，现在也只有五条数据可以更新，后续可以添加其他cardView到RecycleView时可以动态改变该值
            weatherAdapter.refreshData(5);
            //重置 标记值 状态为 false
            queryTimeIsOK = false;
            queryWeatherIsOK = false;

            //发送广播更新桌面小部件的天气信息(切换城市时能实时更新桌面的小部件到对应城市)
            Intent intent = new Intent("com.runhuaoil.yyweather.WIDGET_UPDATE");
            sendBroadcast(intent);
        }

    }

    //检查 服务 的开启/关闭状态
    private boolean serviceIsWork(){
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> servicesList = manager.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo info : servicesList) {
            if (info.service.getClassName().equals("com.runhuaoil.yyweather.service.AutoUpdateService")){
                return true;
            }

        }
        return false;
    }


}
