package com.runhuaoil.yyweater.model;

/**
 * Created by RunHua on 2016/10/25.
 */

public class City {

    private int dbId;
    private String cityCode;
    private String cityName;
    private int provId;//该城市所属的省份或直辖市ID

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getProvId() {
        return provId;
    }

    public void setProvId(int provId) {
        this.provId = provId;
    }

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }
}
