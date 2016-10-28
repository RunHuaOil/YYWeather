package com.runhuaoil.yyweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.runhuaoil.yyweather.db.WeatherDB;
import com.runhuaoil.yyweather.model.City;
import com.runhuaoil.yyweather.model.County;
import com.runhuaoil.yyweather.model.Province;
import com.runhuaoil.yyweather.model.WeatherInfo;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

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

    public static boolean handleWeatherData(String response, Context context){

        try{
            JSONObject jsonObject = new JSONObject(response);

            String desc = jsonObject.getString("desc");
            if (desc.equals("OK")) {
                JSONObject dataObject = jsonObject.getJSONObject("data");
                String currentTemp = dataObject.getString("wendu");
                String countyName = dataObject.getString("city");

                JSONArray forecastArray = dataObject.getJSONArray("forecast");

                Gson gson = new Gson();
                List<WeatherInfo> weatherInfosList = gson.fromJson(forecastArray.toString(), new TypeToken<List<WeatherInfo>>() {}.getType());


                saveWeatherInfo( currentTemp, countyName, weatherInfosList, context);

            }else{
                return false;
            }
            return true;
        }catch (Exception e){

            e.printStackTrace();
            return false;
        }

    }

    public static void saveWeatherInfo( String currentTemp, String countyName, List<WeatherInfo> weatherInfosList, Context context) {
        SharedPreferences sharedPre = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPre.edit();

        editor.putString("currentTemp", currentTemp);
        editor.putBoolean("city_selected", true);
        editor.putString("countyName", countyName);

        for (int i = 0; i < weatherInfosList.size(); i++){
            WeatherInfo info = weatherInfosList.get(i);
            //名称有 0 结尾的是当天的天气
            editor.putString("windPower" + i, info.getfengli());
            editor.putString("windDir" + i, info.getfengxiang());
            editor.putString("date" + i, info.getDate());
            editor.putString("highTemp" + i, info.gethigh());
            editor.putString("lowTemp" + i, info.getlow());
            editor.putString("weatherType" + i, info.gettype());

        }
        editor.commit();

    }

    public static boolean handlePulishTime(String response, Context context){
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            InputStream inputStream = new ByteArrayInputStream(response.getBytes("UTF-8"));
            parser.setInput(new InputStreamReader(inputStream));

            int eventType = parser.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String nodeName = parser.getName();
                        if ("updatetime".equals(nodeName)) {
                            String pulishTime = parser.nextText();

                            SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(context).edit();
                            editor.putString("pulishTime", pulishTime);
                            editor.commit();
                            return true;
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
            return false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }




}
