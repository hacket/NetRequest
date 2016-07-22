package com.example.netlibrary.net_new.request;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.example.netlibrary.net_new.response.BaseResponseNew;
import com.example.netlibrary.util.LogUtil;
import com.google.gson.Gson;

import android.support.annotation.NonNull;

/**
 * GsonRequest
 * <br/>
 * 解析形如
 * <pre>
 * {
 *      "s" : 0/1/2,
 *       "n": "xxxx",
 *       "d": {} / []
 * }
 * </pre>
 *
 * @param <T> T
 *            <br/>
 *            Created by zengfansheng on 2016/4/14.
 */
public class GsonRequestNew<T> extends Request<BaseResponseNew<T>> {

    private static final String TAG = "volley";
    private Builder mBuilder;


    public GsonRequestNew(@NonNull Builder builder) {
        super(builder.mMethod, builder.mUrl, builder.mErrorListener);
        this.mBuilder = builder;
    }


    @Override
    protected Response<BaseResponseNew<T>> parseNetworkResponse(NetworkResponse networkResponse) {
        try {
            String jsonString =
                    new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers));

            JSONObject jsonObject = new JSONObject(jsonString);

            String md5 = jsonObject.optString(BaseResponseNew.MD5);

            @BaseResponseNew.RESPONSE_STATE
            int state = jsonObject.optInt(BaseResponseNew.STATE);
            T data;

            if (mBuilder.mType != null) {
                if (mBuilder.mType instanceof Class) {
                    JSONObject jsonDataObj = jsonObject.optJSONObject(BaseResponseNew.DATA);
                    if (null != jsonDataObj) {
                        data = mBuilder.mGson.fromJson(jsonDataObj.toString(), mBuilder.mType);
                    } else {
                        data = null;
                    }
                } else {
                    JSONArray jsonDataArray = jsonObject.optJSONArray(BaseResponseNew.DATA);
                    if (null != jsonDataArray) {
                        data = mBuilder.mGson.fromJson(jsonDataArray.toString(), mBuilder.mType);

                    } else {
                        data = null;
                    }
                }

                BaseResponseNew<T> response = new BaseResponseNew<>();
                response.state = state;
                response.md5 = md5;
                response.data = data;
                return Response.success(response, HttpHeaderParser.parseCacheHeaders(networkResponse));
            } else {
                return Response.success(null, HttpHeaderParser.parseCacheHeaders(networkResponse));
            }

        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mBuilder.mHeaders != null ? mBuilder.mHeaders : super.getHeaders();
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> params = new HashMap<>();
        if (mBuilder.mParams != null) {
            LogUtil.i(TAG, "getParams() :" + mBuilder.mParams.size());
            // 在这里设置需要post的参数
            params.putAll(mBuilder.mParams);
        }
        return params;
    }

    @Override
    protected void deliverResponse(BaseResponseNew<T> response) {
        if (mBuilder.mListener != null) {
            mBuilder.mListener.onResponse(response);
        }
    }

    @Override
    public void deliverError(VolleyError error) {
        super.deliverError(error);
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return super.getBody();
    }

    @Override
    public Priority getPriority() {
        return mBuilder.mPriority;
    }

    @Override
    public String getBodyContentType() {
        return super.getBodyContentType();
    }

    public final static class Builder<T> {

        private Gson mGson = new Gson();
        private int mMethod;
        private String mUrl;
        private Response.Listener<BaseResponseNew<T>> mListener;
        private Response.ErrorListener mErrorListener;
        private Map<String, String> mHeaders;
        private Map<String, String> mParams;
        private Type mType;

        /**
         * 设置优先级
         */
        private Priority mPriority = Priority.NORMAL;

        public Builder gson(Gson gson) {
            this.mGson = gson;
            return this;
        }

        public Builder method(int method) {
            this.mMethod = method;
            return this;
        }

        public Builder url(String url) {
            this.mUrl = url;
            return this;
        }

        public Builder errorListener(Response.ErrorListener errorListener) {
            this.mErrorListener = errorListener;
            return this;
        }

        public Builder listener(Response.Listener<BaseResponseNew<T>> listener) {
            this.mListener = listener;
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            this.mHeaders = headers;
            return this;
        }

        public Builder params(Map<String, String> params) {
            this.mParams = params;
            return this;
        }

        public Builder type(Type type) {
            this.mType = type;
            return this;
        }

        public Builder priority(Priority priority) {
            this.mPriority = priority;
            return this;
        }

        public GsonRequestNew build() {
            return new GsonRequestNew(this);
        }

    }

}