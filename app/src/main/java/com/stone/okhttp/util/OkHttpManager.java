package com.stone.okhttp.util;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.$Gson$Types;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Map;
import java.util.Set;

/**
 * author : stone
 * email  : aa86799@163.com
 * time   : 15/12/2 20 59
 */
public class OkHttpManager {

    private OkHttpClient mOkHttpClient;
    private Handler mHandler;
    private Gson mGson;

    private OkHttpManager() {
        mOkHttpClient = new OkHttpClient();
        //启用Cookie   //null使用默认的CookieStore
        mOkHttpClient.setCookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
        mHandler = new Handler(Looper.getMainLooper());

//        mGson = new Gson();
        GsonBuilder builder = new GsonBuilder()
                .excludeFieldsWithModifiers(//不包含这些修饰的成员
                        Modifier.FINAL,
                        Modifier.TRANSIENT,
                        Modifier.STATIC)
                .enableComplexMapKeySerialization() //支持Map的key为复杂对象的形式
                .setVersion(1.0)//有的字段不是一开始就有的,会随着版本的升级添加进来,那么在进行序列化和返序列化的时候就会根据版本号来选择是否要序列化.
        ;
        mGson = builder.create();

    }

    private static class OkHttpManagerBuilder {
        private static OkHttpManager instance = new OkHttpManager();
    }

    //静态内部类 实现单例， 私有化返回单例对象
    private static OkHttpManager getInstance() {
        return OkHttpManagerBuilder.instance;
    }

    /**
     * 同步的Get请求，返回Response
     *
     * @param url
     * @return {@link com.squareup.okhttp.Response}
     * @throws IOException
     */
    private Response _getSyncRtResponse(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 同步的Get请求 返回String
     *
     * @param url
     * @return String
     * @throws IOException
     */
    private String _getSyncRtString(String url) throws IOException {
        return _getSyncRtResponse(url).body().string();
    }

    /**
     * 异步的Get请求
     *
     * @param url
     * @param callback
     * @throws IOException
     */
    private void _getAsync(String url, ResultCallback callback) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        dispatchRequest(request, callback);

    }

    /**
     * 同步的Post请求 返回Response
     *
     * @param url
     * @param params
     * @return {@link com.squareup.okhttp.Response}
     * @throws IOException
     */
    private Response _postSyncRtResponse(String url, Param... params) throws IOException {
        Request request = buildPostRequest(url, params);
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 同步的Post请求 返回String
     *
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    private String _postSyncRtString(String url, Param... params) throws IOException {
        return _postSyncRtResponse(url, params).body().string();
    }

    /**
     * 异步的Post请求
     *
     * @param url
     * @param callback
     * @param params
     */
    private void _postAsync(String url, ResultCallback callback, Param... params) throws IOException {
        Request request = buildPostRequest(url, params);
        dispatchRequest(request, callback);
    }

    /**
     * 异步的Post请求
     *
     * @param url
     * @param callback
     * @param params
     */
    private void _postAsync(String url, ResultCallback callback, Map<String,
            String> params) throws IOException {
        Param[] paramAry = map2Params(params);
        Request request = buildPostRequest(url, paramAry);
        dispatchRequest(request, callback);
    }

    /**
     * 同步基于Post的文件上传 多文件，有form参数
     *
     * @param params
     * @return
     */
    private Response _postFileSync(String url, File[] files, String[] fileKeys,
                                   Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, files, fileKeys, params);
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 同步基于Post的文件上传 单文件，无form参数
     *
     * @param url
     * @param file
     * @param fileKey
     * @return
     * @throws IOException
     */
    private Response _postFileSync(String url, File file, String fileKey) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, null);
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 同步基于Post的文件上传 单文件，有form参数
     *
     * @param url
     * @param file
     * @param fileKey
     * @param params
     * @return
     * @throws IOException
     */
    private Response _postFileSync(String url, File file, String fileKey,
                                   Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, params);
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 异步基于Post的文件上传 多文件，有form参数
     *
     * @param url
     * @param callback
     * @param files
     * @param fileKeys 要保存成的文件名
     * @param params
     * @throws IOException
     */
    private void _postFileASync(String url, ResultCallback callback, File[] files,
                                String[] fileKeys, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, files, fileKeys, params);
        dispatchRequest(request, callback);
    }

    /**
     * 异步基于Post的文件上传 单文件，无form参数
     *
     * @param url
     * @param callback
     * @param file
     * @param fileKeys
     * @throws IOException
     */
    private void _postFileASync(String url, ResultCallback callback, File file,
                                String[] fileKeys) throws IOException {
        _postFileASync(url, callback, new File[]{file}, fileKeys);
    }

    /**
     * 异步基于Post的文件上传 单文件，有form参数
     *
     * @param url
     * @param callback
     * @param file
     * @param fileKeys
     * @param params
     * @throws IOException
     */
    private void _postFileASync(String url, ResultCallback callback, File file, String[] fileKeys,
                                Param... params) throws IOException {
        _postFileASync(url, callback, new File[]{file}, fileKeys, params);
    }

    /**
     * 异步下载文件
     *
     * @param url
     * @param destFileDir 存放目录
     * @param callback
     */
    private void _downloadAsync(final String url, final String destFileDir,
                                final ResultCallback callback) {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                sendFailedCallback(request, e, callback);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[1024];
                int len;
                FileOutputStream fos = null;

                try {
                    is = response.body().byteStream();
                    File file = new File(destFileDir, getFileName(url));
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    sendSuccessResultCallback(file.getAbsolutePath(), callback);

                } catch (Exception ex) {
                    sendFailedCallback(request, ex, callback);
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {

                    }

                }

            }


        });
    }

    /*------------------------------------- 公共方法 start -------------------------------------*/
    public static Response getSyncRtResponse(String url) throws IOException {
        return getInstance()._getSyncRtResponse(url);
    }

    public static String getSyncRtString(String url) throws IOException {
        return getInstance()._getSyncRtString(url);
    }

    public static void getAsync(String url, ResultCallback callback) throws IOException {
        getInstance()._getAsync(url, callback);
    }

    /**
     * 同步的Post请求 返回Response
     *
     * @param url
     * @param params
     * @return {@link com.squareup.okhttp.Response}
     * @throws IOException
     */
    public static Response postSyncRtResponse(String url, Param... params) throws IOException {
        return getInstance()._postSyncRtResponse(url, params);
    }


    /**
     * 同步的Post请求 返回String
     *
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public static String postSyncRtString(String url, Param... params) throws IOException {
        return getInstance()._postSyncRtString(url, params);
    }

    /**
     * 异步的Post请求
     *
     * @param url
     * @param callback
     * @param params   Param型
     */
    public static void postAsync(String url, ResultCallback callback,
                                 Param... params) throws IOException {
        getInstance()._postAsync(url, callback, params);
    }

    /**
     * 异步的Post请求
     *
     * @param url
     * @param callback
     * @param params   Map型
     */
    public static void postAsync(String url, ResultCallback callback,
                                 Map<String, String> params) throws IOException {
        getInstance()._postAsync(url, callback, params);
    }

    /**
     * 同步基于Post的文件上传 多文件，有form参数
     *
     * @param params
     * @return
     */
    public static Response postFileSync(String url, File[] files, String[] fileKeys,
                                        Param... params) throws IOException {
        return getInstance()._postFileSync(url, files, fileKeys, params);
    }


    /**
     * 同步基于Post的文件上传 单文件，无form参数
     *
     * @param url
     * @param file
     * @param fileKey
     * @return
     * @throws IOException
     */
    public static Response postFileSync(String url, File file, String fileKey) throws IOException {
        return getInstance()._postFileSync(url, file, fileKey);
    }


    /**
     * 同步基于Post的文件上传 单文件，有form参数
     *
     * @param url
     * @param file
     * @param fileKey
     * @param params
     * @return
     * @throws IOException
     */
    public static Response postFileSync(String url, File file, String fileKey,
                                        Param... params) throws IOException {
        return getInstance()._postFileSync(url, file, fileKey, params);
    }

    /**
     * 异步基于Post的文件上传 多文件，有form参数
     *
     * @param url
     * @param callback
     * @param files
     * @param fileKeys 要保存成的文件名
     * @param params
     * @throws IOException
     */
    public static void postFileASync(String url, ResultCallback callback, File[] files,
                                     String[] fileKeys, Param... params) throws IOException {
        getInstance()._postFileASync(url, callback, files, fileKeys, params);
    }


    /**
     * 异步基于Post的文件上传 单文件，无form参数
     *
     * @param url
     * @param callback
     * @param file
     * @param fileKeys
     * @throws IOException
     */
    public static void postFileASync(String url, ResultCallback callback, File file,
                                     String[] fileKeys) throws IOException {
        getInstance()._postFileASync(url, callback, file, fileKeys);
    }

    /**
     * 异步基于Post的文件上传 单文件，有form参数
     *
     * @param url
     * @param callback
     * @param file
     * @param fileKeys
     * @param params
     * @throws IOException
     */
    public static void postFileASync(String url, ResultCallback callback, File file,
                                     String[] fileKeys, Param... params) throws IOException {
        getInstance()._postFileASync(url, callback, file, fileKeys, params);
    }


    /**
     * 异步下载文件
     *
     * @param url
     * @param destFileDir 存放目录
     * @param callback
     */
    public static void downloadAsync(final String url, final String destFileDir, final ResultCallback callback) {
        getInstance()._downloadAsync(url, destFileDir, callback);
    }

    /*------------------------------------- 公共方法 end -------------------------------------*/

    /**
     * 从path截取文件名
     *
     * @param path
     * @return
     */
    private String getFileName(String path) {
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex == -1) ? path : path.substring(separatorIndex + 1, path.length());
    }

    /**
     * 发送请求失败的回调到主线程
     *
     * @param request
     * @param e
     * @param callback
     */
    private void sendFailedCallback(final Request request, final Exception e,
                                    final ResultCallback callback) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onError(request, e);
                }
            }
        });
    }

    /**
     * 发送请求成功的回调到主线程
     *
     * @param object
     * @param callback
     */
    private void sendSuccessResultCallback(final Object object, final ResultCallback callback) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onResponse(object);
                }
            }
        });
    }


    /**
     * 构建 multipart/form-data 型的请求
     *
     * @param url
     * @param files
     * @param fileKeys
     * @param params
     * @return
     */
    private Request buildMultipartFormRequest(String url, File[] files,
                                              String[] fileKeys, Param[] params) {
        params = validateParam(params);

        MultipartBuilder builder = new MultipartBuilder()
                .type(MultipartBuilder.FORM);

        for (Param param : params) {
            builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + param.key + "\""),
                    RequestBody.create(null, param.value));
        }
        if (files != null) {
            RequestBody fileBody = null;
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                String fileName = file.getName();
                fileBody = RequestBody.create(MediaType.parse(getMimeType(fileName)), file);
                //TODO 根据文件名设置contentType
                builder.addPart(Headers.of("Content-Disposition",
                        "form-data; name=\"" + fileKeys[i] + "\"; filename=\"" + fileName + "\""),
                        fileBody);
            }
        }

        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }

    /**
     * 验证参数
     *
     * @param params
     * @return
     */
    private Param[] validateParam(Param[] params) {
        if (params == null)
            return new Param[0];
        else return params;
    }

    /**
     * 获取文件的MIME类型
     *
     * @param path
     * @return
     */
    private String getMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    /**
     * 将Map<String, String> 转化成 Params[]
     *
     * @param params
     * @return
     */
    private Param[] map2Params(Map<String, String> params) {
        if (params == null) {
            return new Param[0];
        }
        Set<Map.Entry<String, String>> entries = params.entrySet();
        Param[] paramAry = new Param[entries.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : entries) {
            paramAry[i++] = new Param(entry.getKey(), entry.getValue());
        }
        return paramAry;
    }

    /**
     * 根据传入的Param构建Post的Request
     *
     * @param url
     * @param params
     * @return
     */
    private Request buildPostRequest(String url, Param... params) {
        Request.Builder builder = new Request.Builder();
        FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
        for (int i = 0, len = params.length; i < len; i++) {
            formEncodingBuilder.add(params[i].key, params[i].value);
        }
        return builder.url(url).post(formEncodingBuilder.build()).build();
    }

    /**
     * 分发请求，传入回调处理
     *
     * @param request
     * @param callback
     */
    private void dispatchRequest(Request request, final ResultCallback callback) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                sendFailedCallback(request, e, callback);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    String str = response.body().string();
                    if (callback.mType == String.class) {
                        sendSuccessResultCallback(str, callback);
                    } else {
                        Object o = mGson.fromJson(str, callback.mType);
                        sendSuccessResultCallback(o, callback);
                    }
                } catch (Exception ex) {
                    sendFailedCallback(response.request(), ex, callback);
                }


            }
        });
    }

    public static abstract class ResultCallback<T> {
        Type mType;

        public ResultCallback() {
            mType = getSuperclassTypeParameter(getClass());
        }

        private static Type getSuperclassTypeParameter(Class<?> subclass) {
            //Type:Java 编程语言中所有类型的公共高级接口。它们包括原始类型、参数化类型、数组类型、类型变量和基本类型。
            //calss.getGenericSuperclass() 返回表示此 Class 所表示的实体（类、接口、基本类型或 void）的直接超类的Type
            //没有超类，返回自身类的带有泛型参数的type。 如 ResultCallback<User>
            Type superclass = subclass.getGenericSuperclass();

            if (superclass instanceof Class) {//只是普通的Class 不带泛型参数
                throw new RuntimeException("Missing generic type parameter.");
            }
            ParameterizedType parameterized = (ParameterizedType) superclass; //参数化类型
            /*
             * parameterized.getActualTypeArguments() 表示此类型的实际类型参数的Type对象的数组
             * 通过gson里的方法 返回 对应的Type
             */
            return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
        }

        public abstract void onError(Request request, Exception e);

        public abstract void onResponse(T response);
    }

    public static class Param {
        private String key;
        private String value;

        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

}
