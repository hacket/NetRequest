package com.example.netlibrary.net_new;

import java.io.File;

import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.example.netlibrary.net.OkHttpStack;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * Volley RequestQueue请求队列的初始化，添加，取消，删除缓存，清空缓存
 */
class VolleyManagerNew {

    private static final String TAG = "volley";

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
        if (tag != null) {
            request.setTag(tag);
        }
        mRequestQueue.add(request);
    }

    public void cancelRequest(Request request) {
        if (request != null) {
            request.cancel();
        }
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
     * 请求缓存失效，还会用cache对象
     *
     * @param key        url
     * @param fullExpire fullExpire
     */
    public void invalidateCache(String key, boolean fullExpire) {
        if (mRequestQueue != null && !TextUtils.isEmpty(key)) {
            mRequestQueue.getCache().invalidate(key, fullExpire);
        }
    }

    public void invalidateCache(String key) {
        if (mRequestQueue != null && !TextUtils.isEmpty(key)) {
            mRequestQueue.getCache().invalidate(key, true);
        }
    }

    /**
     * 移除key的cache
     *
     * @param key url
     */
    public void removeCache(String key) {
        if (mRequestQueue != null && !TextUtils.isEmpty(key)) {
            mRequestQueue.getCache().remove(key);
        }
    }

    public void clearCache() {
        if (mRequestQueue != null) {
            mRequestQueue.getCache().clear();
        }
    }

    private static volatile VolleyManagerNew mInstance;

    private VolleyManagerNew(Context context) {
        initRequestQueue(context);
    }

    public static VolleyManagerNew getInstance(Context context) {
        if (mInstance == null) {
            synchronized (VolleyManagerNew.class) {
                if (mInstance == null) {
                    mInstance = new VolleyManagerNew(context);
                }
            }
        }
        return mInstance;
    }

}