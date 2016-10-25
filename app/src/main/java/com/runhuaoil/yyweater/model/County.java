package com.runhuaoil.yyweater.model;

/**
 * Created by RunHua on 2016/10/25.
 */

public class County {

    private int dbId;
    private String countyCode;
    private String countyName;
    private int cityId;//该县该区所属的城市ID

    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }
}
