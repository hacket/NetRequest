package com.example.netlibrary.net_new;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.netlibrary.util.RunningContext;

/**
 * Http请求
 * <p/>
 * Created by zengfansheng on 2016/7/21
 */
public class HttpClient {

    private Builder mBuilder;
    private final VolleyManagerNew mVolleyManagerNew;

    private void test() {
        HttpClient client = new Builder().build();

        client.get(new NetCallbackNew<BaseResponseNew<String>>() {
            @Override
            public void onSuccess(String url, BaseResponseNew<String> response) {

            }

            @Override
            public void onFailed(String url, String errorMsg) {

            }
        });
    }

    private HttpClient(Builder builder) {
        this.mBuilder = builder;
        mVolleyManagerNew = VolleyManagerNew.getInstance(RunningContext.getAppContext());
    }

    public <T> void get(final NetCallbackNew<BaseResponseNew<T>> callbackNew) {

        final String url = new ApiParamsNew().addCustomParam(mBuilder.params).buildUrl(mBuilder.urlPath,
                mBuilder.isNeedCommonParam);

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

        public Builder param(Map<String, String> params) {
            this.params = params;
            return this;
        }

        public Builder param(String key, String value) {
            if (params == null) {
                params = new HashMap<>();
            }
            params.put(key, value);
            return this;
        }

        public HttpClient build() {
            return new HttpClient(this);
        }
    }

}
