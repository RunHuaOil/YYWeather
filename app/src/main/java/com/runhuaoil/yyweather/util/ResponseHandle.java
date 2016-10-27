package com.runhuaoil.yyweather.util;

import android.text.TextUtils;

import com.runhuaoil.yyweather.db.WeatherDB;
import com.runhuaoil.yyweather.model.City;
import com.runhuaoil.yyweather.model.County;
import com.runhuaoil.yyweather.model.Province;

import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by RunHua on 2016/10/25.
 */

public class ResponseHandle {

    public static boolean handleProvinceData(String response, WeatherDB db){
        JSONObject jsonObject = null;
        if (!TextUtils.isEmpty(response)){
            try {
                jsonObject = new JSONObject(response);
               // Log.d("Test",jsonObject.toString());
                Iterator<String> it = jsonObject.keys();
                String provCode;
                String provName;
                while(it.hasNext()) {
                    Province province = new Province();

                    provCode = it.next();
                    provName = jsonObject.getString(provCode);

                    province.setProveCode(provCode);
                    province.setProvName(provName);

                    db.saveProvinces(province);

//                Log.d("Test", provCode);
//                Log.d("Test",jsonObject.getString(provCode) );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }
        return false;

    }


    public static boolean handleCityData(String response, WeatherDB db, int provId ){
        JSONObject jsonObject = null;
        if (!TextUtils.isEmpty(response)){
            try {
                jsonObject = new JSONObject(response);
                //Log.d("Test",jsonObject.toString());
                Iterator<String> it = jsonObject.keys();
                String cityCode;
                String cityName;
                while(it.hasNext()) {
                    City city = new City();

                    cityCode = it.next();
                    cityName = jsonObject.getString(cityCode);

                    city.setProvId(provId);
                    city.setCityName(cityName);
                    city.setCityCode(cityCode);

                    db.saveCity(city);

//                Log.d("Test", provCode);
//                Log.d("Test",jsonObject.getString(provCode) );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }
        return false;
    }

    public static boolean handleCountyData(String response, WeatherDB db, int cityId){
        JSONObject jsonObject = null;
        if (!TextUtils.isEmpty(response)){
            try {
                jsonObject = new JSONObject(response);
                //Log.d("Test",jsonObject.toString());
                Iterator<String> it = jsonObject.keys();
                String countyCode;
                String countyName;
                while(it.hasNext()) {
                    County county = new County();

                    countyCode = it.next();
                    countyName = jsonObject.getString(countyCode);

                    county.setCountyName(countyName);
                    county.setCountyCode(countyCode);
                    county.setCityId(cityId);

                    db.saveCounty(county);

//                Log.d("Test", provCode);
//                Log.d("Test",jsonObject.getString(provCode) );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }
        return false;
    }
}
