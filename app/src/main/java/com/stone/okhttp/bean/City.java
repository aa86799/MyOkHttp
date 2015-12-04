package com.stone.okhttp.bean;

/**
 * author : stone
 * email  : aa86799@163.com
 * time   : 15/12/4 15 21
 */
public class City {

    private int errNum;
    private String retMsg;
    private RetData retData;

    public int getErrNum() {
        return errNum;
    }

    public void setErrNum(int errNum) {
        this.errNum = errNum;
    }

    public RetData getRetData() {
        return retData;
    }

    public void setRetData(RetData retData) {
        this.retData = retData;
    }

    public String getRetMsg() {
        return retMsg;
    }

    public void setRetMsg(String retMsg) {
        this.retMsg = retMsg;
    }

    public static class RetData {
        private String cityName;
        private String provinceName;
        private String cityCode;
        private String zipCode;
        private String telAreaCode;

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

        public String getProvinceName() {
            return provinceName;
        }

        public void setProvinceName(String provinceName) {
            this.provinceName = provinceName;
        }

        public String getTelAreaCode() {
            return telAreaCode;
        }

        public void setTelAreaCode(String telAreaCode) {
            this.telAreaCode = telAreaCode;
        }

        public String getZipCode() {
            return zipCode;
        }

        public void setZipCode(String zipCode) {
            this.zipCode = zipCode;
        }

        @Override
        public String toString() {
            return "RetData{" +
                    "cityCode='" + cityCode + '\'' +
                    ", cityName='" + cityName + '\'' +
                    ", provinceName='" + provinceName + '\'' +
                    ", zipCode='" + zipCode + '\'' +
                    ", telAreaCode='" + telAreaCode + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "City{" +
                "errNum=" + errNum +
                ", retMsg='" + retMsg + '\'' +
                ", retData=" + retData +
                '}';
    }
}
