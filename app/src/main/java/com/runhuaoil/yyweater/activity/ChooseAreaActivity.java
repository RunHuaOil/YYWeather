package com.runhuaoil.yyweater.activity;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.runhuaoil.yyweater.R;
import com.runhuaoil.yyweater.dataAdapter.AreaRecyclerViewAdapter;
import com.runhuaoil.yyweater.db.WeaterDB;
import com.runhuaoil.yyweater.model.City;
import com.runhuaoil.yyweater.model.County;
import com.runhuaoil.yyweater.model.Province;
import com.runhuaoil.yyweater.util.AreaRepHandle;
import com.runhuaoil.yyweater.util.HttpCallBack;
import com.runhuaoil.yyweater.util.HttpUtil;


import java.util.ArrayList;
import java.util.List;

public class ChooseAreaActivity extends AppCompatActivity {

    private RecyclerView areaRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private AreaRecyclerViewAdapter mAdapter;
    private ActionBar actionBar;
    private TextView textView;
    private ProgressDialog progressDialog;
    private WeaterDB weaterDB;

    private List<Province> provinceList;

    private List<City> cityList;

    private List<County> countyList;
    private List<String> itemlist = new ArrayList<>();

    private Province selectedProvince;

    private City selectedCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_activity_layout);
        weaterDB = WeaterDB.getInstance(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        textView = (TextView) findViewById(R.id.toolbar_text);



        areaRecyclerView = (RecyclerView) findViewById(R.id.area_recycler_view);

        areaRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        areaRecyclerView.setLayoutManager(mLayoutManager);


        mAdapter = new AreaRecyclerViewAdapter(itemlist);
        areaRecyclerView.setItemAnimator(new DefaultItemAnimator());
        areaRecyclerView.setAdapter(mAdapter);

        queryProvince();

    }

    private void queryProvince(){
        provinceList = weaterDB.loadProvinces();
        if (provinceList.size() > 0){
            itemlist.clear();
            for (Province p : provinceList) {
                itemlist.add(p.getProvName());//获取省份直辖市进要显示的List
            }
            mAdapter.notifyDataSetChanged();
        }else{
            queryFromServer(null, "Province");
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
                        result = AreaRepHandle.handleProvinceData(responseData, weaterDB);
                        break;
                    case "City":
                        result = AreaRepHandle.handleCityData(responseData, weaterDB, selectedProvince.getDbId());
                        break;
                    case "County":
                        result = AreaRepHandle.handleCountyData(responseData, weaterDB, selectedCity.getDbId());
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

                            }else{

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
                        Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
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
}
