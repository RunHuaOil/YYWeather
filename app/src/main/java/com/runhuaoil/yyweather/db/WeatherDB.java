package com.runhuaoil.yyweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.runhuaoil.yyweather.model.City;
import com.runhuaoil.yyweather.model.County;
import com.runhuaoil.yyweather.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RunHua on 2016/10/25.
 * 该类用来 存储 获取 地点数据
 */

public class WeatherDB {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "YYWeatherArea";

    private SQLiteDatabase db;
    private static WeatherDB weatherDB;

    //单例模式
    private WeatherDB(Context context){
        WeatherSQLiteOpenHelper dbHelper = new WeatherSQLiteOpenHelper(context, DB_NAME, null, DB_VERSION);
        db = dbHelper.getWritableDatabase();
    }

    public synchronized static WeatherDB getInstance(Context context){
        if (weatherDB == null){
            weatherDB = new WeatherDB(context);
        }
        return weatherDB;
    }

    public void saveProvinces(Province province){
        if (province != null){
            ContentValues values = new ContentValues();
            values.put("province_name",province.getProvName());
            values.put("province_code",province.getProveCode());
            db.insert("Province", null, values);
        }

    }

    public void saveCity(City city){
        if (city != null){
            ContentValues values = new ContentValues();
            values.put("city_name",city.getCityName());
            values.put("city_code",city.getCityCode());
            values.put("province_id",city.getProvId());
            db.insert("City", null, values);
        }

    }

    public void saveCounty(County county){
        if (county != null){
            ContentValues values = new ContentValues();
            values.put("county_name",county.getCountyName());
            values.put("county_code",county.getCountyCode());
            values.put("city_id",county.getCityId());
            db.insert("County", null, values);
        }
    }

    public List<Province> loadProvinces(){
        List<Province> provList = new ArrayList<>();
        Cursor cursor = db.query("Province", null, null, null, null, null, null);
        if (cursor.moveToFirst()){

            do {
                Province province = new Province();
                province.setDbId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProveCode(cursor.getString(cursor.getColumnIndex("province_code")));
                province.setProvName(cursor.getString(cursor.getColumnIndex("province_name")));
                provList.add(province);
            }while (cursor.moveToNext());

        }
        cursor.close();
        return provList;
    }



    public List<City> loadCities(int provId){
        List<City> cityList = new ArrayList<>();

        Cursor cursor = db.query("City", null, "province_id = ?", new String[]{String.valueOf(provId)}, null, null, null);

        if (cursor.moveToFirst()){

            do {
                City city = new City();
                city.setDbId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setProvId(cursor.getInt(cursor.getColumnIndex("province_id")));

                cityList.add(city);
            }while (cursor.moveToNext());

        }
        cursor.close();
        return cityList;
    }



    public List<County> loadCounties(int cityId){
        List<County> countyList = new ArrayList<>();
        Cursor cursor = db.query("County", null, "city_id = ?", new String[]{String.valueOf(cityId)}, null, null, null);

        if (cursor.moveToFirst()){
            do {
                County county = new County();
                county.setDbId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                countyList.add(county);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return countyList;
    }
}
