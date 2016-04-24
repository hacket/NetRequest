package com.example.netlibrary.net;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.netlibrary.util.LogUtil;
import com.example.netlibrary.util.RunningContext;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

public class NetUtil {

    private static final String TAG = "volley";
    private static volatile NetUtil mInstance;
    private VolleyManager mVolleyManager;

    private NetUtil() {
        if (mVolleyManager == null) {
            mVolleyManager = VolleyManager.getInstance(RunningContext.getAppContext());
        }
    }

    public static NetUtil getInstance() {
        if (mInstance == null) {
            synchronized(NetUtil.class) {
                if (mInstance == null) {
                    mInstance = new NetUtil();
                }
            }
        }
        return mInstance;
    }

    // ================ requestAsyncGet GsonRequest 异步 ================ //

    /**
     * GET , 需要公共参数，无自定义参数
     */
    @UiThread
    public <T> void requestAsyncGet(@NonNull final String urlPath, @NonNull Type type, String tag,
                                    @Nullable final NetCallback<BaseResponse<T>> callback) {
        requestAsyncGet(urlPath, null, true, type, tag, callback);
    }

    /**
     * GET , 需要公共参数
     */
    @UiThread
    public <T> void requestAsyncGet(@NonNull final String urlPath, @Nullable Map<String, String> params,
                                    @NonNull Type type,
                                    String tag, @Nullable final NetCallback<BaseResponse<T>> callback) {
        requestAsyncGet(urlPath, params, true, type, tag, callback);
    }

    /**
     * GET
     */
    @UiThread
    public <T> void requestAsyncGet(@NonNull final String urlPath, @Nullable Map<String, String> params,
                                    boolean isNeedCommonParam, @NonNull Type type, String tag,
                                    @Nullable final NetCallback<BaseResponse<T>> callback) {
        requestAsync(Method.GET, urlPath, params, isNeedCommonParam, type, tag, null, callback);
    }

    /**
     * GET , HIGH priority
     */
    @UiThread
    public <T> void requestAsyncGetHigh(@NonNull final String urlPath, @Nullable Map<String, String> params,
                                        boolean isNeedCommonParam, @NonNull Type type, String tag,
                                        @Nullable final NetCallback<BaseResponse<T>> callback) {
        requestAsync(Method.GET, urlPath, params, isNeedCommonParam, type, tag, Request.Priority.HIGH, callback);
    }

    /**
     * POST , 需要公共参数，无自定义参数
     */
    @UiThread
    public <T> void requestAsyncPost(@NonNull final String urlPath, @NonNull Type type, String tag,
                                     @Nullable final NetCallback<BaseResponse<T>> callback) {
        requestAsyncPost(urlPath, null, true, type, tag, callback);
    }

    /**
     * POST , 需要公共参数
     */
    @UiThread
    public <T> void requestAsyncPost(@NonNull final String urlPath, @Nullable Map<String, String> params,
                                     @NonNull Type type,
                                     String tag, @Nullable final NetCallback<BaseResponse<T>> callback) {
        requestAsyncPost(urlPath, params, true, type, tag, callback);
    }

    /**
     * POST
     */
    @UiThread
    public <T> void requestAsyncPost(@NonNull final String urlPath, @Nullable Map<String, String> params,
                                     boolean isNeedCommonParam, @NonNull Type type, String tag,
                                     @Nullable final NetCallback<BaseResponse<T>> callback) {
        requestAsync(Method.POST, urlPath, params, isNeedCommonParam, type, tag, null, callback);
    }

    /**
     * POST , HIGH priority
     */
    @UiThread
    public <T> void requestAsyncPostHigh(@NonNull final String urlPath, @Nullable Map<String, String> params,
                                         boolean isNeedCommonParam, @NonNull Type type, String tag,
                                         @Nullable final NetCallback<BaseResponse<T>> callback) {
        requestAsync(Method.POST, urlPath, params, isNeedCommonParam, type, tag, Request.Priority.HIGH, callback);
    }

    /**
     * 异步请求数据
     *
     * @param method            {@link Method#GET } , {@link Method#POST}
     * @param urlPath           host+path
     * @param params            自定义参数
     * @param isNeedCommonParam 是否需要公共参数  {@link ApiParams#buildCommonParamToString}
     * @param type              JSONObject传个xxx.class ; JSONArray传type ; 具体使用参考UserRepository#getUserEntry}
     * @param tag               tag , 可用于取消请求 {@link #cancelRequest(String)}
     * @param priority          priority 优先级 {@link com.android.volley.Request.Priority}
     * @param callback          callback
     * @param <T>               T
     */
    @UiThread
    public <T> void requestAsync(@REQUEST_METHOD int method, @NonNull final String urlPath,
                                 @Nullable Map<String, String> params, boolean isNeedCommonParam, @NonNull Type type,
                                 String tag, Request.Priority priority,
                                 @Nullable final NetCallback<BaseResponse<T>> callback) {

        final String url;
        if (method == Method.GET) {
            url = new ApiParams().addCustomParam(params).buildUrl(urlPath, isNeedCommonParam);
        } else {
            url = urlPath;
            params = new ApiParams().addCustomParam(params).buildParam(isNeedCommonParam);
            mVolleyManager.removeCache(url);  // POST 412 ?
        }

        LogUtil.d(TAG, "requestAsync url : " + url);

        if (priority == null) {
            priority = Request.Priority.NORMAL;
        }

        GsonRequest<T> gsonRequest =
                new GsonRequest<>(method, params, url, type,
                        new Response.Listener<BaseResponse<T>>() {
                            @Override
                            public void onResponse(BaseResponse<T> response) {
                                if (callback != null) {
                                    callback.onSuccess(url, response);
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (callback != null) {
                            callback.onFailed(url, error.getMessage());
                        }
                    }
                });

        gsonRequest.setPriority(priority);

        mVolleyManager.addRequest(gsonRequest, tag);
    }

    // ================ requestAsyncGet GsonRequest 异步 ================ //

    // ================ requestAsyncGet GsonRequest 同步 ================ //

    /**
     * 同步，POST，带公共参数，timeout:0，normal priority
     */
    public <T> void requestSyncPost(@NonNull final String urlPath, @Nullable Map<String, String> params,
                                    @NonNull Type type, String tag,
                                    @Nullable final NetCallback<BaseResponse<T>> callback) {
        requestSyncPost(urlPath, params, true, type, tag, null, 0, callback);
    }

    /**
     * 同步，POST
     */
    public <T> void requestSyncPost(@NonNull final String urlPath, @Nullable Map<String, String> params,
                                    boolean isNeedCommonParam, @NonNull Type type, String tag,
                                    Request.Priority priority, long timeoutmills,
                                    @Nullable final NetCallback<BaseResponse<T>> callback) {
        requestSync(Method.POST, urlPath, params, isNeedCommonParam, type, tag, priority, timeoutmills, callback);
    }

    /**
     * 同步，GET，带公共参数，timeout:0，normal priority
     */
    public <T> void requestSyncGet(@NonNull final String urlPath, @Nullable Map<String, String> params,
                                   @NonNull Type type, String tag,
                                   @Nullable final NetCallback<BaseResponse<T>> callback) {
        requestSyncGet(urlPath, params, true, type, tag, null, 0, callback);
    }

    /**
     * 同步，GET
     */
    public <T> void requestSyncGet(@NonNull final String urlPath,
                                   @Nullable Map<String, String> params, boolean isNeedCommonParam,
                                   @NonNull Type type, String tag, Request.Priority priority, long timeoutmills,
                                   @Nullable final NetCallback<BaseResponse<T>> callback) {
        requestSync(Method.GET, urlPath, params, isNeedCommonParam, type, tag, priority, timeoutmills, callback);
    }

    /**
     * 同步请求
     *
     * @param method            {@link Method#GET} , {@link Method#POST}
     * @param urlPath           url+path
     * @param params            自定义参数
     * @param isNeedCommonParam 是否需要公共参数
     * @param type              type
     * @param tag               tag , 可用于取消请求 {@link #cancelRequest(String)}
     * @param priority          优先级 {@link com.android.volley.Request.Priority}
     * @param callback          callback
     * @param timeoutmills      timeout , 毫秒
     * @param <T>               T
     */
    @WorkerThread
    public <T> void requestSync(@REQUEST_METHOD int method, @NonNull final String urlPath,
                                @Nullable Map<String, String> params, boolean isNeedCommonParam,
                                @NonNull Type type, String tag, Request.Priority priority, long timeoutmills,
                                @Nullable final NetCallback<BaseResponse<T>> callback) {
        final String url;
        if (method == Method.GET) {
            url = new ApiParams().addCustomParam(params).buildUrl(urlPath, isNeedCommonParam);
        } else {
            url = urlPath;
            params = new ApiParams().addCustomParam(params).buildParam(isNeedCommonParam);
            mVolleyManager.removeCache(url);  // POST 412 ?
        }

        try {
            GsonRequest<T> gsonRequest = new GsonRequest<>(method, params, url, type, null, null);

            if (priority == null) {
                priority = Request.Priority.NORMAL;
            }
            gsonRequest.setPriority(priority);

            BaseResponse<T> re = mVolleyManager.addSyncRequest(gsonRequest, tag, timeoutmills);
            if (callback != null) {
                callback.onSuccess(url, re);
            }
        } catch (Exception e) {
            LogUtil.printStackTrace(e);
            if (callback != null) {
                callback.onFailed(url, e.getMessage());
            }
        }
    }

    /**
     * 同步请求
     *
     * @param method            {@link Method#GET} , {@link Method#POST}
     * @param urlPath           url+path
     * @param params            自定义参数
     * @param isNeedCommonParam 是否需要公共参数
     * @param type              type
     * @param tag               tag , 可用于取消请求 {@link #cancelRequest(String)}
     * @param priority          优先级 {@link com.android.volley.Request.Priority}
     * @param timeoutmills      timeout , 毫秒
     * @param <T>               T
     *
     * @return BaseResponse<T>
     *
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    @WorkerThread
    public <T> BaseResponse<T> requestSync(@REQUEST_METHOD int method, @NonNull final String urlPath,
                                           @Nullable Map<String, String> params, boolean isNeedCommonParam,
                                           @NonNull Type type, String tag, Request.Priority priority, long timeoutmills)
            throws InterruptedException, ExecutionException, TimeoutException {
        final String url;
        if (method == Method.GET) {
            url = new ApiParams().addCustomParam(params).buildUrl(urlPath, isNeedCommonParam);
        } else {
            url = urlPath;
            params = new ApiParams().addCustomParam(params).buildParam(isNeedCommonParam);
            mVolleyManager.removeCache(url);  // POST 412 ?
        }
        GsonRequest<T> gsonRequest = new GsonRequest<>(method, params, url, type, null, null);
        if (priority == null) {
            priority = Request.Priority.NORMAL;
        }
        gsonRequest.setPriority(priority);
        return mVolleyManager.addSyncRequest(gsonRequest, tag, timeoutmills);
    }

    // ================ requestAsyncGet GsonRequest 同步 ================ //

    // ================ requestAsyncGet JSONObject 异步 ================ //

    /**
     * POST  公共参数 无自定义参数
     */
    @UiThread
    public void requestJsonAsyncPost(@NonNull final String urlPath, String tag,
                                     @Nullable final NetCallback<JSONObject> callback) {
        requestJsonAsyncPost(urlPath, null, tag, callback);
    }

    /**
     * POST  公共参数
     */
    @UiThread
    public void requestJsonAsyncPost(@NonNull final String urlPath, @Nullable Map<String, String> params, String tag,
                                     @Nullable final NetCallback<JSONObject> callback) {
        requestJsonAsyncPost(urlPath, params, true, tag, callback);
    }

    /**
     * POST
     */
    @UiThread
    public void requestJsonAsyncPost(@NonNull final String urlPath, @Nullable Map<String, String> params,
                                     boolean isNeedCommonParam, String tag,
                                     @Nullable final NetCallback<JSONObject> callback) {
        requestJsonAsync(Method.POST, urlPath, params, isNeedCommonParam, tag, callback);
    }

    /**
     * GET  公共参数 无自定义参数
     */
    @UiThread
    public void requestJsonAsyncGet(@NonNull final String urlPath, String tag,
                                    @Nullable final NetCallback<JSONObject> callback) {
        requestJsonAsyncGet(urlPath, null, tag, callback);
    }

    /**
     * GET  公共参数
     */
    @UiThread
    public void requestJsonAsyncGet(@NonNull final String urlPath, @Nullable Map<String, String> params, String tag,
                                    @Nullable final NetCallback<JSONObject> callback) {
        requestJsonAsyncGet(urlPath, params, true, tag, callback);
    }

    /**
     * GET
     */
    @UiThread
    public void requestJsonAsyncGet(@NonNull final String urlPath, @Nullable Map<String, String> params,
                                    boolean isNeedCommonParam, String tag,
                                    @Nullable final NetCallback<JSONObject> callback) {
        requestJsonAsync(Method.GET, urlPath, params, isNeedCommonParam, tag, callback);
    }

    /**
     * 异步请求，回调json对象，处理不规范老接口
     *
     * @param urlPath           host+path
     * @param params            自定义参数
     * @param isNeedCommonParam 是否需要公共参数
     * @param tag               请求tag，可用于cancel
     * @param callback
     */
    @UiThread
    public void requestJsonAsync(@REQUEST_METHOD int method, @NonNull final String urlPath,
                                 @Nullable Map<String, String> params,
                                 boolean isNeedCommonParam, String tag,
                                 @Nullable final NetCallback<JSONObject> callback) {
        final String url;
        JSONObject jsonObjRequest;
        if (method == Method.GET) {
            url = new ApiParams().addCustomParam(params).buildUrl(urlPath, isNeedCommonParam);
            jsonObjRequest = null;
        } else {
            url = urlPath;
            jsonObjRequest = new JSONObject(new ApiParams().addCustomParam(params).buildParam(isNeedCommonParam));
            mVolleyManager.removeCache(url);  // POST 412 ?
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, jsonObjRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (callback != null) {
                            callback.onSuccess(url, response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (callback != null) {
                            callback.onFailed(url, error.getMessage());
                        }
                    }
                });

        mVolleyManager.addRequest(jsonObjectRequest, tag);
    }

    // ================ requestAsyncGet JSONObject 异步 ================ //

    public void cancelRequest(String tag) {
        LogUtil.d(TAG, "cancel requestAsync , tag:" + tag);
        mVolleyManager.cancelRequest(tag);
    }

    public void removeCache(String key) {
        mVolleyManager.removeCache(key);
    }

    public interface Method {
        int GET = 0;
        int POST = 1;
    }

    @IntDef({Method.GET, Method.POST})
    @Retention(RetentionPolicy.SOURCE)
    public @interface REQUEST_METHOD {
    }

}