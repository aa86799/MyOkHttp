/*
 * 杭州当贝网络科技有限公司
 */

package com.stone.ok3;

import android.support.annotation.NonNull;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Challenge;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;
import okio.BufferedSink;

/**
 * desc   :
 * author : stone
 * email  : aa86799@163.com
 * time   : 21/03/2017 20 25
 */
public class OkHttpTest {
    /*
    OkHttp官方文档并不建议我们创建多个OkHttpClient，因此全局使用一个。
    如果有需要，可以使用clone方法，再进行自定义
     */
    private static final OkHttpClient mOkHttpClient = new OkHttpClient();

    static {
//        int i = mOkHttpClient.connectTimeoutMillis(30 * 1000L);
    }

    /**
     * 该不会开启异步线程。
     *
     * @param request
     * @return
     * @throws IOException
     */
    public static okhttp3.Response execute(Request request) throws IOException {
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 开启异步线程访问网络
     *
     * @param request
     * @param responseCallback
     */
    public static void enqueue(Request request, Callback responseCallback) {
        mOkHttpClient.newCall(request).enqueue(responseCallback);
    }

    /**
     * 开启异步线程访问网络, 且不在意返回结果（实现空callback）
     *
     * @param request
     */
    public static void enqueue(Request request) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {

            }
        });
    }

    public static String getStringFromServer(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        okhttp3.Response response = execute(request);
        if (response.isSuccessful()) {
            String responseUrl = response.body().string();
            return responseUrl;
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    public static byte[] getBytesFromServer(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        okhttp3.Response response = execute(request);
        if (response.isSuccessful()) {
            byte[] responseUrl = response.body().bytes();
            return responseUrl;
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    public static InputStream getByteStreamFromServer(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        okhttp3.Response response = execute(request);
        if (response.isSuccessful()) {
            InputStream responseUrl = response.body().byteStream();
            return responseUrl;
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }


    private static final String CHARSET_NAME = "UTF-8";

    /**
     * 这里使用了HttpClient的API。只是为了方便
     *
     * @param params
     * @return
     */
    public static String formatParams(List<BasicNameValuePair> params) {
        return URLEncodedUtils.format(params, CHARSET_NAME);
    }

    /**
     * 为HttpGet 的 url 方便的添加多个name value 参数。
     *
     * @param url
     * @param params
     * @return
     */
    public static String attachHttpGetParams(String url, List<BasicNameValuePair> params) {
        return url + "?" + formatParams(params);
    }

    /**
     * 为HttpGet 的 url 方便的添加1个name value 参数。
     *
     * @param url
     * @param name
     * @param value
     * @return
     */
    public static String attachHttpGetParam(String url, String name, String value) {
        return url + "?" + name + "=" + value;
    }


    public static Request createRequest(@NonNull String url) {
        Request.Builder builder = new Request.Builder().url(url);
        return builder.build();
    }

    //get请求
    public static Request get(@NonNull String url, @NonNull Map<String, String> headersMap) {
        Request.Builder builder = new Request.Builder().url(url);
        for (Map.Entry<String, String> entry : headersMap.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
        return builder.get().build();
    }

    //post提交String
    public static Request postString(@NonNull String url, @NonNull Map<String, String> headersMap) {
        Request.Builder builder = new Request.Builder().url(url);
        for (Map.Entry<String, String> entry : headersMap.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
        return builder.post(RequestBody.create(MediaType.parse("text/json"), "jsonString")).build();
    }

    public void postStream(@NonNull String url) {
        Request.Builder builder = new Request.Builder().url(url);
        RequestBody body = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.parse("text/x-markdown; charset=utf-8");
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.write("stone".getBytes()); //写入流
                sink.writeUtf8("stone"); //写入流
            }
        };
        builder.post(body);
    }

    //Post方式提交文件
    public void postFile() throws IOException {
        MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        File file = new File("README.md");

        Request request = new Request.Builder()
                .url("https://api.github.com/markdown/raw")
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, file))
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        System.out.println(response.body().string());
    }

    //post提交表单
    public void postForm() {
        FormBody formBuilder = new FormBody.Builder()
                .add("key", "value")
//                .addEncoded()
                .build();

        Request request = new Request.Builder()
                .url("http://index.php")
                .post(formBuilder)
                .build();
    }

    //post方式提交分块请求
    public void postMultipart() {
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(
                        Headers.of("Content-Disposition", "form-data; name=\"title\""),
                        RequestBody.create(null, "Square Logo"))
                .addPart(
                        Headers.of("Content-Disposition", "form-data; name=\"image\""),
                        RequestBody.create(MediaType.parse("image/png"), new File("website/static/logo-square.png")))
                .build();

        Request request = new Request.Builder()
                .header("Authorization", "Client-ID " + "...")
                .url("https://api.imgur.com/3/image")
                .post(requestBody)
                .build();
    }

    //响应缓存
    public void cacheResponse() {
        int cacheSize = 10 * 1024 * 1024; // 10 MB
        Cache cache = new Cache(new File("cacheDirectory"), cacheSize);

        OkHttpClient.Builder okBuilder = new  OkHttpClient().newBuilder();
        okBuilder.cache(cache);
        OkHttpClient client = okBuilder.build();

        Request request = new Request.Builder()
                //指示客户机可以接收超出 超时设定时间值的响应消息
                .addHeader("Cache-Control", "max-stale=3600")
                //指示客户机可以接收生存期不大于指定时间（以秒为单位）的响应
                .addHeader("Cache-Control", "max-age=3600")
                //指示请求或响应消息不能缓存
//                .addHeader("Cache-Control", "no-cache")
                .build();
    }

    //取消call
    public void cancelCall(String tag, Request request) {
//        new OkHttpClient.Builder().build().newCall(request).cancel();
        Call call = null;
        if (tag.equals(call.request().tag())) {
            call.cancel();
        }
    }

    //超时
    public void setProperty() {
        new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

    }

    private final OkHttpClient mClient = new OkHttpClient();//ok3不支client-clone() ，，需要自行重写 ，实现cloneable接口

    /*
    OkHttp会自动重试未验证的请求。当响应是401 Not Authorized时，Authenticator会被要求提供证书。
    Authenticator的实现中需要建立一个新的包含证书的请求。如果没有证书可用，返回null来跳过尝试。
     */
    public void testAuth() throws CloneNotSupportedException {
//        OkHttpClient c = (OkHttpClient)mClient.clone();
//        String s = "s".clone();
        new OkHttpClient.Builder().authenticator(new Authenticator() {
            @Override
            public Request authenticate(Route route, Response response) throws IOException {
//                route.
                /*
                response.challenges()在响应401时和407时有不同处理，详情见源码
                 */
                List<Challenge> challenges = response.challenges();
                for (int i = 0; i < challenges.size(); i++) {
                    Challenge challenge = challenges.get(i);
                    System.out.println(challenge.realm());
                    System.out.println(challenge.scheme());
                }
                String credential = Credentials.basic("stone", "password1");
                return response.request().newBuilder()
                        .header("Authorization", credential)
                        .build();
            }
        });
    }
}
