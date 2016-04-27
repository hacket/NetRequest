package com.example.netlibrary.net;

/**
 * 网络请求回调
 * <p/>
 * Created by zengfansheng on 2016/4/14.
 */
public interface NetCallback<T> {

    void onSuccess(String url, T response);

    void onFailed(String url, String errorMsg);
}
