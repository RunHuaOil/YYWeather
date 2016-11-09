package com.runhuaoil.yyweather.model;

/**
 * Created by RunHua on 2016/10/25.
 * Province Bean
 */

public class Province {

    private String proveCode;
    private String provName;
    private int dbId;

    public String getProveCode() {
        return proveCode;
    }

    public void setProveCode(String proveCode) {
        this.proveCode = proveCode;
    }

    public String getProvName() {
        return provName;
    }

    public void setProvName(String provName) {
        this.provName = provName;
    }

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }
}
