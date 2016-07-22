package com.example.netlibrary.net_new;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.netlibrary.net_new.callback.NetCallbackNew;
import com.example.netlibrary.net_new.request.GsonRequestNew;
import com.example.netlibrary.net_new.response.BaseResponseNew;
import com.example.netlibrary.util.RunningContext;

import android.text.TextUtils;

/**
 * Http请求
 * <p/>
 * Created by zengfansheng on 2016/7/21
 */
public class HttpClient {

    private Builder mBuilder;
    private final VolleyManagerNew mVolleyManagerNew;

    private HttpClient(Builder builder) {
        this.mBuilder = builder;
        mVolleyManagerNew = VolleyManagerNew.getInstance(RunningContext.getAppContext());
    }

    public <T> void get(final String url, final NetCallbackNew<BaseResponseNew<T>> callbackNew) {

//        final String url = new RequestParams().addCustomParam(mBuilder.params).buildUrl(mBuilder.urlPath,
//                mBuilder.isNeedCommonParam);

        Response.Listener<BaseResponseNew<T>> listener = new Response.Listener<BaseResponseNew<T>>() {
            @Override
            public void onResponse(BaseResponseNew<T> response) {
                if (callbackNew != null) {
                    callbackNew.onSuccess(url, response);
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (callbackNew != null) {
                    callbackNew.onFailed(url, error.getMessage());
                }
            }
        };

        GsonRequestNew requestNew = new GsonRequestNew.Builder<T>()
                .method(mBuilder.method)
                .headers(mBuilder.headers)
                .params(mBuilder.params)
                .url(url)
                .type(mBuilder.type)
                .listener(listener)
                .errorListener(errorListener)
                .priority(mBuilder.priority)
                .build();

        mVolleyManagerNew.addRequest(requestNew, mBuilder.requestTag);
    }

    public <T> void post() {

    }

    public final static class Builder {

        // 必须
        Type type;
        String urlPath;
        String requestTag;

        // 可选
        int method = Request.Method.GET;
        Map<String, String> params;
        Map<String, String> headers;
        boolean isNeedCommonParam = true;
        Request.Priority priority = Request.Priority.NORMAL;
        String baseUrl;

        public Builder priority(Request.Priority priority) {
            if (priority == null) {
                return this;
            }
            this.priority = priority;
            return this;
        }

        public Builder method(int method) {
            this.method = method;
            return this;
        }

        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder urlPath(String urlPath) {
            this.urlPath = urlPath;
            return this;
        }

        public Builder isNeedCommonParam(boolean isNeedCommonParam) {
            this.isNeedCommonParam = isNeedCommonParam;
            return this;
        }

        public Builder requestTag(String requestTag) {
            this.requestTag = requestTag;
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            if (this.headers == null) {
                this.headers = new HashMap<>();
            }
            this.headers = headers;
            return this;
        }

        public Builder header(String key, String value) {
            if (this.headers == null) {
                this.headers = new HashMap<>();
            }
            if (!TextUtils.isEmpty(key)) {
                this.headers.put(key, value);
            }
            return this;
        }

        public Builder params(Map<String, String> params) {
            if (this.params == null) {
                this.params = new HashMap<>();
            }
            this.params = params;
            return this;
        }

        public Builder param(String key, String value) {
            if (this.params == null) {
                this.params = new HashMap<>();
            }
            this.params.put(key, value);
            return this;
        }

        public HttpClient build() {
            return new HttpClient(this);
        }
    }

}
