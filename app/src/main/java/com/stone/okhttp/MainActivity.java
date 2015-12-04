package com.stone.okhttp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.squareup.okhttp.Request;
import com.stone.okhttp.bean.City;
import com.stone.okhttp.util.OkHttpManager;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * author : stone
 * email  : aa86799@163.com
 * time   : 15/12/2 13 52
 */
public class MainActivity extends Activity {
    TextView tv_cityName;
    TextView tv_provinceName;
    TextView tv_cityCode;
    TextView tv_zipCode;
    TextView tv_telAreaCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.acti_main);

        tv_cityName = getViewById(R.id.tv_cityName);
        tv_provinceName = getViewById(R.id.tv_provinceName);
        tv_cityCode = getViewById(R.id.tv_cityCode);
        tv_zipCode = getViewById(R.id.tv_zipCode);
        tv_telAreaCode = getViewById(R.id.tv_telAreaCode);

        ExecutorService pool = Executors.newFixedThreadPool(3);
        pool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    getCityInfo();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getCityInfo() throws IOException {
        //该api获取城市信息
        OkHttpManager.getAsync("http://apistore.baidu.com/microservice/cityinfo?cityname=%E5%A4%A7%E7%AB%B9",
                new OkHttpManager.ResultCallback<City>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        System.out.println("error-url---->" + request.httpUrl());
                    }

                    @Override
                    public void onResponse(City response) {
                        System.out.println("success---->" + response);

                        tv_cityName.setText(response.getRetData().getCityName());
                        tv_cityCode.setText(response.getRetData().getCityCode());
                        tv_provinceName.setText(response.getRetData().getProvinceName());
                        tv_zipCode.setText(response.getRetData().getZipCode());
                        tv_telAreaCode.setText(response.getRetData().getTelAreaCode());
                    }
                });

        OkHttpManager.postAsync("http://apistore.baidu.com/microservice/cityinfo?cityname=%E5%A4%A7%E7%AB%B9",
                new OkHttpManager.ResultCallback<City>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        System.out.println("post--error-url---->" + request.httpUrl());
                    }

                    @Override
                    public void onResponse(City response) {
                        System.out.println("post--success---->" + response);
                    }
                });
    }

    private <T> T getViewById(int viewid) {
        return (T) findViewById(viewid);
    }
}
