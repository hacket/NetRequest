package com.example.netlibrary.net;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.RequestFuture;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * Volley RequestQueue请求队列的初始化，添加，取消等
 */
class VolleyManager {

    private static final String TAG = "volley";

    public final String REQUEST_DEFAULT_TAG = "VolleyRequesterDefaultTag";

    /**
     * Default on-disk cache directory.
     */
    private static final String DEFAULT_CACHE_DIR = "volley";

    /**
     * Number of network requestAsync dispatcher threads to start.
     */
    private static final int DEFAULT_NETWORK_THREAD_POOL_SIZE = 4;

    private RequestQueue mRequestQueue;

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    private void initRequestQueue(@NonNull Context context) {
        if (mRequestQueue != null) {
            return;
        }
        Network network = new BasicNetwork(new OkHttpStack());
        File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);
        mRequestQueue = new RequestQueue(new DiskBasedCache(cacheDir), network, DEFAULT_NETWORK_THREAD_POOL_SIZE);
        mRequestQueue.start();
    }

    /**
     * 异步请求
     *
     * @param request Request
     * @param <T>     Request
     */
    public <T> void addRequest(Request<T> request) {
        addRequest(request, null);
    }

    /**
     * 异步请求
     *
     * @param request Request
     * @param tag     tag用于区分是否同一个请求
     * @param <T>     Request
     */
    public <T> void addRequest(Request<T> request, Object tag) {
        request.setTag(tag == null ? REQUEST_DEFAULT_TAG : tag);
        mRequestQueue.add(request);
    }

    /**
     * 同步请求
     *
     * @param request Request
     * @param <T>     Request
     *
     * @return T
     */
    public <T> T addSyncRequest(Request<T> request, Object tag) throws InterruptedException, ExecutionException, TimeoutException {
        return addSyncRequest(request, tag, 0);
    }

    /**
     * 同步请求
     *
     * @param request       Request
     * @param tag           tag用于区分是否同一个请求
     * @param timeoutmillis timeout , 毫秒
     * @param <T>           Request
     *
     * @return T
     */
    public <T> T addSyncRequest(Request<T> request, Object tag, long timeoutmillis)
            throws InterruptedException, ExecutionException, TimeoutException {
        request.setTag(tag == null ? REQUEST_DEFAULT_TAG : tag);
        RequestFuture<T> future = RequestFuture.newFuture();
        mRequestQueue.add(request);
        return future.get(timeoutmillis, TimeUnit.MILLISECONDS);
    }

    /**
     * 取消请求
     *
     * @param tag tag
     */
    public void cancelRequest(Object tag) {
        if (tag != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    /**
     * 取消请求，默认TAG{@link #REQUEST_DEFAULT_TAG}
     */
    public void cancelRequest() {
        mRequestQueue.cancelAll(REQUEST_DEFAULT_TAG);
    }

    /**
     * 请求缓存失效，还会用cache对象
     *
     * @param key        url
     * @param fullExpire fullExpire
     */
    public void invalidate(String key, boolean fullExpire) {
        if (mRequestQueue != null && !TextUtils.isEmpty(key)) {
            mRequestQueue.getCache().invalidate(key, fullExpire);
        }
    }

    public void invalidate(String key) {
        if (mRequestQueue != null && !TextUtils.isEmpty(key)) {
            mRequestQueue.getCache().invalidate(key, true);
        }
    }

    /**
     * 移除key的cache
     *
     * @param key url
     */
    public void remove(String key) {
        if (mRequestQueue != null && !TextUtils.isEmpty(key)) {
            mRequestQueue.getCache().remove(key);
        }
    }

    public void clearCache() {
        if (mRequestQueue != null) {
            mRequestQueue.getCache().clear();
        }
    }

    private static volatile VolleyManager mInstance;

    private VolleyManager(Context context) {
        initRequestQueue(context);
    }

    public static VolleyManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized(VolleyManager.class) {
                if (mInstance == null) {
                    mInstance = new VolleyManager(context);
                }
            }
        }
        return mInstance;
    }

}