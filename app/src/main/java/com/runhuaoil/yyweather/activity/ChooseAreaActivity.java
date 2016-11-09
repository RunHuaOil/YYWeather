package com.runhuaoil.yyweather.activity;

import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
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
import com.runhuaoil.yyweather.util.CompleteCallBack;
import com.runhuaoil.yyweather.util.MySharedPreferences;
import com.runhuaoil.yyweather.util.ResponseHandle;
import com.runhuaoil.yyweather.util.CardViewOnClickListener;
import com.runhuaoil.yyweather.util.HttpCallBack;
import com.runhuaoil.yyweather.util.HttpUtil;
import com.runhuaoil.yyweather.util.UpdateUtil;
import com.runhuaoil.yyweather.widgets.WeatherWidgetProvider;

import java.util.ArrayList;
import java.util.List;

public class ChooseAreaActivity extends AppCompatActivity implements CardViewOnClickListener{

    //标记当前的选择是处于 省还是市还是区县
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    public static final int LEVEL_NO = -1;
    //记录当前处于哪个阶段，用于 Back 键后退判定;SnackBar的OnClick获取当前该更新哪个;RecycleView回调 onClick 判定下一步做什么
    private int currentLevel = LEVEL_NO;

    private View view;
    private AreaRecyclerViewAdapter mAdapter;
    private TextView titleText;
    private ProgressDialog progressDialog;
    private WeatherDB weatherDB;

    //将查询的地点存储起来
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    //传给 RecycleView 更新列表用的 List
    private List<String> itemlist = new ArrayList<>();

    //当前选择的 省份/城市
    private Province selectedProvince;
    private City selectedCity;

    //是否从 WeatherActivity 跳转本活动
    private boolean isFromWeatherActivity;

    //以该值来判断是否是appWidget进入的
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private AppWidgetManager appWidgetManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //是否已经 选择了城市
        SharedPreferences pre = MySharedPreferences.getInstance(this);
        boolean isCitySelected = pre.getBoolean("city_selected", false);
        //是否从 WeatherActivity 跳转过来重新选择城市的
        isFromWeatherActivity = getIntent().getBooleanExtra("isFromWeatherActivity", false);

        /*
        * 以下几步判断是否从 Widget 进入该 Activity，如果不是则什么都不做
        * 如果是，再判断 isCitySelected ，如果已经选择了，直接退出，
        * 如果没有选择城市，则正常显示 城市选择过程，直到最后选中了城市
        */
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            mAppWidgetId = bundle.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID){
            setResult(RESULT_CANCELED);
            appWidgetManager = AppWidgetManager.getInstance(this);
            if (isCitySelected){
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
                return;
            }
        }

        //如果选择了 城市，且不是来自 WeatherActivity 则跳转到 WeatherActivity 直接显示天气
        //如果未选中城市，或者来自 WeatherActivity 重新选择天气城市则正常显示该 Activity
        if (isCitySelected && !isFromWeatherActivity){
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }


        //开始初始化 Activity
        setContentView(R.layout.choose_activity_layout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //在5.0以上的系统设置导航栏背景颜色和主题颜色一致。这里为蓝色
            getWindow().setNavigationBarColor(Color.rgb(41, 149, 233));
        }

        //获取 数据库 实例
        weatherDB = WeatherDB.getInstance(this);

        //设置 ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayShowTitleEnabled(false);
        }


        titleText = (TextView) findViewById(R.id.toolbar_text);//ToolBar 上显示已选择的省份，城市，提示
        view = findViewById(R.id.choose_activity_layout_id);// SnackBar参数

        // 设置 RecyclerView
        RecyclerView areaRecyclerView = (RecyclerView) findViewById(R.id.area_recycler_view);
        areaRecyclerView.setHasFixedSize(true);
        areaRecyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        areaRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new AreaRecyclerViewAdapter(itemlist, this);
        areaRecyclerView.setAdapter(mAdapter);

        //开始 查询 并 显示省份
        queryProvince();
    }

    private void queryProvince(){
        provinceList = weatherDB.loadProvinces();//假如为空则发起网络请求得到省份数据，如果不为空则载入数据
        if (provinceList.size() > 0){
            itemlist.clear();
            for (Province p : provinceList) {
                itemlist.add(p.getProvName());//获取省份直辖市名字存进要显示的List
            }
            mAdapter.notifyUpdate(itemlist);//载入 省份数据 更新 RecycleView
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
            mAdapter.notifyUpdate(itemlist);
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
            mAdapter.notifyUpdate(itemlist);
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
                            switch (type) {
                                case "Province":
                                    queryProvince();
                                    break;
                                case "City":
                                    queryCity();
                                    break;
                                case "County":
                                    queryCounty();
                                    break;
                                default:
                                    break;
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
                                        switch (type){
                                            case "Province":
                                                queryProvince();
                                                break;
                                            case "City":
                                                queryCity();
                                                break;
                                            case "County":
                                                queryCounty();
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                })
                                .show();
                    }
                });
            }
        });
    }


    //当 RecycleView 中的 CardView 被点击时回调该方法
    @Override
    public void onClick(int position) {

        switch (currentLevel){
            case LEVEL_PROVINCE:
                selectedProvince = provinceList.get(position);
                titleText.setText(selectedProvince.getProvName());
                queryCity();
                break;
            case LEVEL_CITY:
                selectedCity = cityList.get(position);
                titleText.setText(selectedCity.getCityName());
                queryCounty();
                break;
            case LEVEL_COUNTY:
                String county_name = countyList.get(position).getCountyName();

                if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID){
                    //如果从 Widget 进来，到这里将通过 getWeatherData 查询天气数据，完成时更新 Widget，然后结束界面
                    UpdateUtil.getWeatherData(county_name, this, new CompleteCallBack() {
                        @Override
                        public void onComplete() {
                            WeatherWidgetProvider.updateWidget(ChooseAreaActivity.this, appWidgetManager, mAppWidgetId);

                            Intent resultValue = new Intent();
                            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                            setResult(RESULT_OK, resultValue);
                            finish();
                        }
                    });

                }else {
                    //启动 WeatherActivity，并且传入所选择的 county_name
                    Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                    intent.putExtra("county_name", county_name);
                    startActivity(intent);
                    finish();
                }
                break;
        }

    }

    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_COUNTY){
            titleText.setText("请选择城市");
            queryCity();
        }else if(currentLevel == LEVEL_CITY){
            titleText.setText("请选择地区");
            queryProvince();
        }else {
            if (isFromWeatherActivity) {

                Intent intent = new Intent(this, WeatherActivity.class);
                startActivity(intent);
            }
            finish();
        }
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
}
