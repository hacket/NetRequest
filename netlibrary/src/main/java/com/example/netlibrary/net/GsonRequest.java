package com.example.netlibrary.net;

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
class GsonRequest<T> extends Request<BaseResponse<T>> {

    private static final String TAG = "volley";

    private Map<String, String> mHeaders;
    private Map<String, String> mParams;
    private Type mType;
    private Gson mGson = new Gson();

    private Priority mPriority = Priority.NORMAL;

    private Response.Listener<BaseResponse<T>> mListener;

    public void setListener(Response.Listener<BaseResponse<T>> listener) {
        this.mListener = listener;
    }

    /**
     * /**
     * Make a  request and return a parsed object from JSON.
     *
     * @param url           URL of the request to make
     * @param headers       Map of request headers
     * @param params        Map of request params
     * @param type          Relevant type object, for Gson's reflection
     * @param listener      listener
     * @param errorListener errorListener
     */
    public GsonRequest(int method, Map<String, String> headers, Map<String, String> params,
                       @NonNull String url, @NonNull Type type, Response.Listener<BaseResponse<T>> listener,
                       Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mHeaders = headers;
        mParams = params;
        mType = type;
        mListener = listener;
    }

    /**
     * 无headers
     */
    public GsonRequest(int method, Map<String, String> params, @NonNull String url, @NonNull Type type,
                       Response.Listener<BaseResponse<T>> listener, Response.ErrorListener errorListener) {
        this(method, null, params, url, type, listener, errorListener);
    }

    /**
     * Get，无headers
     */
    public GsonRequest(Map<String, String> params, @NonNull String url, @NonNull Type type,
                       Response.Listener<BaseResponse<T>> listener, Response.ErrorListener errorListener) {
        this(Method.GET, params, url, type, listener, errorListener);
    }

    /**
     * Get，无headers，无参数
     */
    public GsonRequest(@NonNull String url, @NonNull Type type,
                       Response.Listener<BaseResponse<T>> listener, Response.ErrorListener errorListener) {
        this(Method.GET, null, url, type, listener, errorListener);
    }

    @Override
    protected Response<BaseResponse<T>> parseNetworkResponse(NetworkResponse networkResponse) {
        try {
            String jsonString =
                    new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers));

            JSONObject jsonObject = new JSONObject(jsonString);

            String md5 = jsonObject.optString(BaseResponse.MD5);

            @BaseResponse.RESPONSE_STATE
            int state = jsonObject.optInt(BaseResponse.STATE);
            T data;

            if (mType != null) {
                if (mType instanceof Class) {
                    JSONObject jsonDataObj = jsonObject.optJSONObject(BaseResponse.DATA);
                    if (null != jsonDataObj) {
                        data = mGson.fromJson(jsonDataObj.toString(), mType);
                    } else {
                        data = null;
                    }
                } else {
                    JSONArray jsonDataArray = jsonObject.optJSONArray(BaseResponse.DATA);
                    if (null != jsonDataArray) {
                        data = mGson.fromJson(jsonDataArray.toString(), mType);

                    } else {
                        data = null;
                    }
                }

                BaseResponse<T> response = new BaseResponse<>();
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
        return mHeaders != null ? mHeaders : super.getHeaders();
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> params = new HashMap<>();
        if (mParams != null) {
            LogUtil.i(TAG, "getParams() :" + mParams.size());
            // 在这里设置需要post的参数
            params.putAll(mParams);
        }
        return params;
    }

    @Override
    protected void deliverResponse(BaseResponse<T> response) {
        if (mListener != null) {
            mListener.onResponse(response);
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
        return mPriority;
    }

    /**
     * 设置优先级
     *
     * @param priority {@link Priority}
     */
    public void setPriority(@NonNull Priority priority) {
        this.mPriority = priority;
    }

    @Override
    public String getBodyContentType() {
        return super.getBodyContentType();
    }

}