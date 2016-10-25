package com.runhuaoil.yyweater.util;

import android.text.TextUtils;
import android.util.Log;

import com.runhuaoil.yyweater.db.WeaterDB;
import com.runhuaoil.yyweater.model.City;
import com.runhuaoil.yyweater.model.County;
import com.runhuaoil.yyweater.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by RunHua on 2016/10/25.
 */

public class AreaRepHandle {

    public static boolean handleProvinceData(String response, WeaterDB db){
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


    public static boolean handleCityData(String response, WeaterDB db, int provId ){
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

    public static boolean handleCountyData(String response, WeaterDB db, int cityId){
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
