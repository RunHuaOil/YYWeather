package com.runhuaoil.yyweather.model;

/**
 * Created by RunHua on 2016/10/27.
 */

public class WeatherInfo {
    private String fengxiang;
    private String fengli;
    private String high;
    private String type;
    private String low;
    private String date;

    public String getfengxiang() {
        return fengxiang;
    }

    public void setfengxiang(String fengxiang) {
        this.fengxiang = fengxiang;
    }

    public String getfengli() {
        return fengli;
    }

    public void setfengli(String fengli) {
        this.fengli = fengli;
    }

    public String gethigh() {
        return high;
    }

    public void sethigh(String high) {
        this.high = high;
    }

    public String gettype() {
        return type;
    }

    public void settype(String type) {
        this.type = type;
    }

    public String getlow() {
        return low;
    }

    public void setlow(String low) {
        this.low = low;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return fengxiang + fengli + high + low + date +type;
    }
}
