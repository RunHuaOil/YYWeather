package com.runhuaoil.yyweather.dataAdapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.runhuaoil.yyweather.R;
import com.runhuaoil.yyweather.util.MySharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by RunHua on 2016/10/27.
 * WeatherActivity中 RecyclerView Adapter的实现
 * 主要根据当前系统日期，来选择使用哪种布局
 * RecyclerView.Adapter 根据 getItemViewType() 来决定 Item 要显示的布局
 * RecyclerView.Adapter 根据 getItemCount() 来决定要显示的 Item 数目
 */

public class WeatherInfoRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //以下两个值是 getItemViewType()的返回值，决定使用哪种布局
    private static final int TODAY_ITEM = 1;
    private static final int FORECAST_ITEM = 2;

    private SharedPreferences sharedPre;
    private int displayNumber = 0;//要显示的 Item 数目
    private String[] today;
    private Context mContext;

    public WeatherInfoRecyclerViewAdapter(Context context) {
        this.mContext = context;
        sharedPre = MySharedPreferences.getInstance(mContext);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d", Locale.CHINA);
        String nowDate = sdf.format(new Date());
        today = nowDate.split("月");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TODAY_ITEM){
            view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_today_item_layout, parent, false);

            return new TodayViewHolder(view);

        }else if (viewType == FORECAST_ITEM){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_forecast_item_layout, parent, false);

            return new ForecastViewHolder(view);
        }
        return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TodayViewHolder){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月", Locale.CHINA);
            String todayYearMonth = sdf.format(new Date());
            TodayViewHolder todayViewHolder = (TodayViewHolder) holder;

            todayViewHolder.cityName.setText(sharedPre.getString("countyName",""));
            todayViewHolder.currentTempText.setText(mContext.getString(R.string.tempMark, sharedPre.getString("currentTemp","")));
            todayViewHolder.publishText.setText(mContext.getString(R.string.publishTimeMark, sharedPre.getString("pulishTime","")));
//            todayViewHolder.currentTempText.setText(sharedPre.getString("currentTemp","")  +"℃");
//            todayViewHolder.publishText.setText(sharedPre.getString("pulishTime","") + "发布");

            todayViewHolder.highTempText.setText(sharedPre.getString("highTemp" + position, "  ").split(" ")[1]);
            todayViewHolder.lowTempText.setText(sharedPre.getString("lowTemp" + position, "  ").split(" ")[1]);
            todayViewHolder.todayWeatherTypeText.setText(sharedPre.getString("weatherType" + position, ""));
            todayViewHolder.todayWindText.setText(sharedPre.getString("windDir" + position, "") + " " + sharedPre.getString("windPower" + position, ""));
            todayViewHolder.todayText.setText("今天 " + todayYearMonth + sharedPre.getString("date" + position, ""));
        }else if (holder instanceof  ForecastViewHolder){
            ForecastViewHolder forecastViewHolder = (ForecastViewHolder) holder;

            forecastViewHolder.forecastDayText.setText(sharedPre.getString("date" + position, ""));
            forecastViewHolder.highTempText.setText(sharedPre.getString("highTemp" + position, "  ").split(" ")[1]);
            forecastViewHolder.lowTempText.setText(sharedPre.getString("lowTemp" + position, "  ").split(" ")[1]);
            forecastViewHolder.weatherTypeText.setText(sharedPre.getString("weatherType" + position, ""));
            forecastViewHolder.windTypeText.setText(sharedPre.getString("windDir" + position, "") + " " + sharedPre.getString("windPower" + position, ""));
        }

    }

    @Override
    public int getItemCount() {
        return displayNumber;
    }

    @Override
    public int getItemViewType(int position) {
        String date = sharedPre.getString("date" + position,"");
        String[] day = date.split("日");

        if (today[1].equals(day[0])){
            return TODAY_ITEM;
        }else {
            return FORECAST_ITEM;
        }

    }

    private class TodayViewHolder extends RecyclerView.ViewHolder{

        TextView todayText;
        TextView publishText;
        TextView currentTempText;
        TextView highTempText;
        TextView lowTempText;
        TextView todayWeatherTypeText;
        TextView todayWindText;
        TextView cityName;

        TodayViewHolder(View itemView) {
            super(itemView);
            todayText = (TextView)itemView.findViewById(R.id.today_date_text_view);
            publishText = (TextView)itemView.findViewById(R.id.publish_text_view);
            currentTempText = (TextView)itemView.findViewById(R.id.current_temp_text);
            lowTempText = (TextView)itemView.findViewById(R.id.today_low_temp_text);
            highTempText = (TextView)itemView.findViewById(R.id.today_high_temp_text);
            todayWeatherTypeText = (TextView) itemView.findViewById(R.id.today_weather_type_text);
            todayWindText = (TextView)itemView.findViewById(R.id.today_wind_text);
            cityName = (TextView) itemView.findViewById(R.id.city_name_text);

        }
    }

    private class ForecastViewHolder extends RecyclerView.ViewHolder{


        TextView forecastDayText;
        TextView windTypeText;
        TextView weatherTypeText;
        TextView highTempText;
        TextView lowTempText;

        ForecastViewHolder(View itemView) {
            super(itemView);
            forecastDayText = (TextView)itemView.findViewById(R.id.fore_date_text_view);
            lowTempText = (TextView)itemView.findViewById(R.id.fore_low_temp_text);
            highTempText = (TextView)itemView.findViewById(R.id.fore_high_temp_text);
            weatherTypeText = (TextView)itemView.findViewById(R.id.fore_weather_type_text);
            windTypeText = (TextView)itemView.findViewById(R.id.fore_wind_text_view);

        }
    }

    public void refreshData(int displayNumber){
        this.displayNumber = displayNumber;
        notifyDataSetChanged();
    }
}
