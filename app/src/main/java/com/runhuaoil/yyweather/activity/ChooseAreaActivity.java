package com.runhuaoil.yyweather.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.runhuaoil.yyweather.R;
import com.runhuaoil.yyweather.dataAdapter.AreaRecyclerViewAdapter;
import com.runhuaoil.yyweather.db.WeatherDB;
import com.runhuaoil.yyweather.model.City;
import com.runhuaoil.yyweather.model.County;
import com.runhuaoil.yyweather.model.Province;
import com.runhuaoil.yyweather.util.ResponseHandle;
import com.runhuaoil.yyweather.util.CardViewOnClickListener;
import com.runhuaoil.yyweather.util.HttpCallBack;
import com.runhuaoil.yyweather.util.HttpUtil;


import java.util.ArrayList;
import java.util.List;

public class ChooseAreaActivity extends AppCompatActivity implements CardViewOnClickListener{

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private View view;
    private RecyclerView areaRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private AreaRecyclerViewAdapter mAdapter;
    private ActionBar actionBar;
    private TextView textView;
    private ProgressDialog progressDialog;
    private WeatherDB weatherDB;

    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    private List<String> itemlist = new ArrayList<>();

    private Province selectedProvince;

    private City selectedCity;

    private int currentLevel = -1;//记录当前处于哪个阶段，用于 Back 键后退判定;SnackBar的OnClick获取当前该更新哪个;RecycleView回调 onClick 判定下一步做什么

    private boolean isFromWeatherActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFromWeatherActivity = getIntent().getBooleanExtra("isFromWeatherActivity", false);

        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(this);
        if (pre.getBoolean("city_selected", false) && !isFromWeatherActivity){
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.choose_activity_layout);

        weatherDB = WeatherDB.getInstance(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        textView = (TextView) findViewById(R.id.toolbar_text);
        view = findViewById(R.id.choose_activity_layout_id);
        areaRecyclerView = (RecyclerView) findViewById(R.id.area_recycler_view);

        areaRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        areaRecyclerView.setLayoutManager(mLayoutManager);


        mAdapter = new AreaRecyclerViewAdapter(itemlist, this);
        areaRecyclerView.setItemAnimator(new DefaultItemAnimator());
        areaRecyclerView.setAdapter(mAdapter);


        queryProvince();

    }

    private void queryProvince(){
        provinceList = weatherDB.loadProvinces();
        if (provinceList.size() > 0){
            itemlist.clear();
            for (Province p : provinceList) {
                itemlist.add(p.getProvName());//获取省份直辖市进要显示的List
            }
            mAdapter.notifyUpData(itemlist);
            currentLevel = LEVEL_PROVINCE;
        }else{
            queryFromServer(null, "Province");
        }
    }

    private void queryCity() {
        cityList = weatherDB.loadCities(selectedProvince.getDbId());
        if (cityList.size() > 0){
            itemlist.clear();
            for (City c : cityList) {
                itemlist.add(c.getCityName());
            }
            mAdapter.notifyUpData(itemlist);
            currentLevel = LEVEL_CITY;
        }else{
            queryFromServer(selectedProvince.getProveCode(), "City");
        }
    }

    private void queryCounty() {
        countyList = weatherDB.loadCounties(selectedCity.getDbId());
        if (countyList.size() > 0){
            itemlist.clear();
            for (County c : countyList) {
                itemlist.add(c.getCountyName());
            }
            mAdapter.notifyUpData(itemlist);
            currentLevel = LEVEL_COUNTY;
        }else{
            queryFromServer(selectedProvince.getProveCode() + selectedCity.getCityCode(), "County");
        }

    }

    private void queryFromServer(final String code,final String type) {
        String address = null;
        switch (type){
            case "Province":
                address = "http://gd.weather.com.cn/data/city3jdata/china.html";
                break;
            case "City":
                address = "http://gd.weather.com.cn/data/city3jdata/provshi/"+ code +".html";
                break;
            case "County":
                address = "http://gd.weather.com.cn/data/city3jdata/station/"+ code + ".html ";
                break;
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallBack() {
            @Override
            public void onFinish(String responseData) {
                boolean result = false;
                switch (type){
                    case "Province":
                        result = ResponseHandle.handleProvinceData(responseData, weatherDB);
                        break;
                    case "City":
                        result = ResponseHandle.handleCityData(responseData, weatherDB, selectedProvince.getDbId());
                        break;
                    case "County":
                        result = ResponseHandle.handleCountyData(responseData, weatherDB, selectedCity.getDbId());
                        break;
                }

                if (result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if (type.equals("Province")){
                                queryProvince();
                            }else if(type.equals("City")){
                                queryCity();
                            }else{
                                queryCounty();
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
                        closeProgressDialog();
                        Snackbar.make(view, "加载失败,请检查网络连接", Snackbar.LENGTH_INDEFINITE)
                                .setAction("刷新", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        switch (currentLevel){
                                            case LEVEL_PROVINCE:
                                                queryCity();
                                                break;
                                            case LEVEL_CITY:
                                                queryCounty();
                                                break;
                                            case LEVEL_COUNTY:

                                                break;
                                            default:
                                                queryProvince();
                                                break;
                                        }
                                    }
                                })
                                .show();
                        //Toast.makeText(ChooseAreaActivity.this, "加载失败,请检查网络连接", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


    @Override
    public void onClick(int position) {

        switch (currentLevel){
            case LEVEL_PROVINCE:
                selectedProvince = provinceList.get(position);
                textView.setText(selectedProvince.getProvName());
                queryCity();
                break;
            case LEVEL_CITY:
                selectedCity = cityList.get(position);
                textView.setText(selectedCity.getCityName());
                queryCounty();
                break;
            case LEVEL_COUNTY:
                String county_name = countyList.get(position).getCountyName();

                Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                intent.putExtra("county_name", county_name);
                startActivity(intent);
                finish();
                break;
        }

    }

    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_COUNTY){
            textView.setText("请选择城市");
            queryCity();
        }else if(currentLevel == LEVEL_CITY){
            textView.setText("请选择地区");
            queryProvince();
        }else {
            if (isFromWeatherActivity) {
                Intent intent = new Intent(this, WeatherActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }
}
