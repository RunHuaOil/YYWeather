package com.runhuaoil.yyweather.activity;

import android.content.SharedPreferences;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.runhuaoil.yyweather.R;
import com.runhuaoil.yyweather.dataAdapter.WeatherInfoRecyclerViewAdapter;
import com.runhuaoil.yyweather.util.HttpCallBack;
import com.runhuaoil.yyweather.util.HttpUtil;
import com.runhuaoil.yyweather.util.ResponseHandle;

import org.w3c.dom.Text;

/**
 * Created by RunHua on 2016/10/27.
 */

public class WeatherActivity extends AppCompatActivity {

    private WeatherInfoRecyclerViewAdapter weatherAdapter;
    private View view;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_activity_layout);

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
        
        String countyName = getIntent().getStringExtra("county_name");
        if (!TextUtils.isEmpty(countyName)){
            queryWeatherInfo(countyName);
            queryPublishTime(countyName);
        }else {
            showWeatherInfo();
        }

                
    }

    private void showWeatherInfo() {
        weatherAdapter.refreshData(5);
    }

    private void queryWeatherInfo(String countyName) {
        String address = "http://wthrcdn.etouch.cn/weather_mini?city=" + countyName;
        HttpUtil.sendHttpRequest(address, new HttpCallBack() {
            @Override
            public void onFinish(String responseData) {
                boolean result = ResponseHandle.handleWeatherData(responseData, WeatherActivity.this);
                if (result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
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
                        Snackbar.make(view, "加载失败,请检查网络连接", Snackbar.LENGTH_LONG)
                                .setAction("刷新", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                }).show();
                    }
                });
            }
        });

    }

    private void queryPublishTime(String countyName) {
        String address = "http://wthrcdn.etouch.cn/WeatherApi?city=" + countyName;
        HttpUtil.sendHttpRequest(address, new HttpCallBack() {
            @Override
            public void onFinish(String responseData) {
                boolean result = ResponseHandle.handlePulishTime(responseData,WeatherActivity.this);
                if (result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeatherInfo();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}
