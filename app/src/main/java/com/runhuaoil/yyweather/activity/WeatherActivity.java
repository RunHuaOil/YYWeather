package com.runhuaoil.yyweather.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.runhuaoil.yyweather.R;
import com.runhuaoil.yyweather.dataAdapter.WeatherInfoRecyclerViewAdapter;
import com.runhuaoil.yyweather.service.AutoUpdateService;
import com.runhuaoil.yyweather.util.HttpCallBack;
import com.runhuaoil.yyweather.util.HttpUtil;
import com.runhuaoil.yyweather.util.ResponseHandle;

import java.util.List;


/**
 * Created by RunHua on 2016/10/27.
 *
 */

public class WeatherActivity extends AppCompatActivity {

    private WeatherInfoRecyclerViewAdapter weatherAdapter;
    private View view;
    private static final int REFRESH_TYPE = 1;
    private static final int NO_REFRESH_TYPE = 2;
    private String selectCountyName;
    private static boolean queryTimeIsOK = false;
    private static boolean queryWeatherIsOK = false;
    private MenuItem updateItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_activity_layout);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.rgb(41, 149, 233));
        }

        view = findViewById(R.id.weather_main_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.weather_toolbar_id);
        toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_menu_white_24dp));
        setSupportActionBar(toolbar);


        RecyclerView weatherRecyclerView = (RecyclerView) findViewById(R.id.weather_recycler_view);
        weatherRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        weatherRecyclerView.setLayoutManager(mLayoutManager);
        weatherAdapter = new WeatherInfoRecyclerViewAdapter(this);
        weatherRecyclerView.setItemAnimator(new DefaultItemAnimator());
        weatherRecyclerView.setAdapter(weatherAdapter);
        
        selectCountyName = getIntent().getStringExtra("county_name");
        if (!TextUtils.isEmpty(selectCountyName)){
            queryWeatherInfo(selectCountyName);
            queryPublishTime(selectCountyName, NO_REFRESH_TYPE);
        }else {
            queryTimeIsOK = true;
            queryWeatherIsOK = true;
            showWeatherInfo();
        }

    }

    private void queryWeatherInfo(final String countyName) {
        String address = "http://wthrcdn.etouch.cn/weather_mini?city=" + countyName;
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
        String address = "http://wthrcdn.etouch.cn/WeatherApi?city=" + countyName;
        HttpUtil.sendHttpRequest(address, new HttpCallBack() {
            @Override
            public void onFinish(String responseData) {

                //Log.d("Test", "queryPublishTime Thread: " + Thread.currentThread().getId() );
                boolean result = ResponseHandle.handlePulishTime(responseData, WeatherActivity.this);
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
                SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(this);
                String countyName = pre.getString("countyName","");

                if (selectCountyName != null){//防止 本地有储存上一次 地点 ，当网络不好不显示天气时，点击 刷新 会以上一次的地点来查询天气覆盖掉用户所选择的新城市
                    countyName = selectCountyName;
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
            weatherAdapter.refreshData(5);
            queryTimeIsOK = false;
            queryWeatherIsOK = false;
        }

    }

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
