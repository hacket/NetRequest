package com.example.netrequest;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import com.android.volley.Request;
import com.example.netlibrary.net.BaseResponse;
import com.example.netlibrary.net.NetCallback;
import com.example.netlibrary.net.NetUtil;
import com.example.netlibrary.util.LogUtil;
import com.example.netrequest.model.ArrayData;
import com.example.netrequest.model.ObjData;
import com.google.gson.reflect.TypeToken;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VolleyActivity extends AppCompatActivity {

    private static final String TAG = "hacket";

    private static final String REQUEST_TAG = "request_tag";

    @Bind(R.id.tv_result)
    TextView tvResult;

    String url_obj = "http://192.168.221.105:8080/demo_obj.json";
    String url_array = "http://192.168.221.105:8080/demo_array.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volley);
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "onDestroy : cancelRequest " + REQUEST_TAG);
        NetUtil.getInstance().cancelRequest(REQUEST_TAG);
    }

    public Map<String, String> buildParams() {
        Map<String, String> params = new HashMap<>();
        params.put("user", "hacket");
        params.put("pwd", "123456");
        return params;
    }

    public void setResult(String result) {
        tvResult.setText(result);
    }

    @OnClick(R.id.btn_normal_get_obj_sync_callback)
    public void normalGetObjSyncCallback() {

        LogUtil.i(TAG, "--------------normalGetObjSync callback--------------");
        NetUtil.getInstance()
                .requestSync(NetUtil.Method.GET, url_obj, buildParams(), true, ObjData.class, REQUEST_TAG,
                        Request.Priority.NORMAL, 4000, new NetCallback<BaseResponse<ObjData>>() {
                            @Override
                            public void onSuccess(String url, BaseResponse<ObjData> response) {
                                ObjData data = response.data;
                                String md5 = response.md5;
                                LogUtil.i(TAG, "md5:" + md5);
                                LogUtil.i(TAG, "data:" + data);
                                setResult(data.toString());
                            }

                            @Override
                            public void onFailed(String url, String errorMsg) {
                                LogUtil.e(TAG, url + " : " + errorMsg);
                            }
                        });
    }

    @OnClick(R.id.btn_normal_get_obj_sync_return)
    public void normalGetObjSyncReturn() {

        LogUtil.i(TAG, "--------------normalGetObjSync return--------------");

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    BaseResponse<Object> response = NetUtil.getInstance()
                            .requestSync(NetUtil.Method.GET, url_obj, buildParams(), true, ObjData.class, REQUEST_TAG,
                                    Request.Priority.NORMAL, 4000);
                    LogUtil.i(TAG, "data : " + response.data);
                } catch (InterruptedException e) {
                    LogUtil.printStackTrace(e);
                } catch (ExecutionException e) {
                    LogUtil.printStackTrace(e);
                } catch (TimeoutException e) {
                    LogUtil.printStackTrace(e);
                }
            }
        }.start();

    }

    @OnClick(R.id.btn_high_get_obj)
    public void highGetObj() {

        normalGetObj();
        normalGetObj();
        normalGetObj();
        normalGetObj();
        normalGetObj();
        normalGetObj();
        normalGetObj();

        Map<String, String> params = buildParams();
        NetUtil.getInstance().requestAsyncGetHigh(url_obj, params, true, ObjData.class, REQUEST_TAG,
                new NetCallback<BaseResponse<ObjData>>() {
                    @Override
                    public void onSuccess(String url, BaseResponse<ObjData> response) {
                        LogUtil.e(TAG, "===============------------highGetObj---------------===========");
                        ObjData data = response.data;
                        String md5 = response.md5;
                        LogUtil.i(TAG, "md5:" + md5);
                        LogUtil.i(TAG, "data:" + data);
                        setResult(data.toString());
                    }

                    @Override
                    public void onFailed(String url, String errorMsg) {
                        LogUtil.e(TAG, url + " : " + errorMsg);
                    }
                });
    }

    @OnClick(R.id.btn_normal_get_obj_cancel)
    public void cancelNrmalGetObj() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Map<String, String> params = buildParams();
                NetUtil.getInstance().requestAsyncGet(url_obj, params, true, ObjData.class, REQUEST_TAG,
                        new NetCallback<BaseResponse<ObjData>>() {
                            @Override
                            public void onSuccess(String url, BaseResponse<ObjData> response) {
                                ObjData data = response.data;
                                String md5 = response.md5;
                                LogUtil.i(TAG, "md5:" + md5);
                                LogUtil.i(TAG, "data:" + data);
                                setResult(data.toString());
                            }

                            @Override
                            public void onFailed(String url, String errorMsg) {
                                LogUtil.e(TAG, url + " : " + errorMsg);
                            }
                        });
            }
        }, 2000);

    }

    @OnClick(R.id.btn_normal_get_obj)
    public void normalGetObj() {

        Map<String, String> params = buildParams();

        NetUtil.getInstance().requestAsyncGet(url_obj, params, true, ObjData.class, REQUEST_TAG,
                new NetCallback<BaseResponse<ObjData>>() {
                    @Override
                    public void onSuccess(String url, BaseResponse<ObjData> response) {
                        LogUtil.i(TAG, "------------normalGetObj---------------");
                        ObjData data = response.data;
                        String md5 = response.md5;
                        LogUtil.i(TAG, "md5:" + md5);
                        LogUtil.i(TAG, "data:" + data);
                        setResult(data.toString());
                    }

                    @Override
                    public void onFailed(String url, String errorMsg) {
                        LogUtil.e(TAG, url + " : " + errorMsg);
                    }
                });
    }

    @OnClick(R.id.btn_normal_get_array)
    public void normalGetArray() {

        Map<String, String> params = buildParams();
        Type type = new TypeToken<List<ArrayData>>() {
        }.getType();

        NetUtil.getInstance().requestAsyncGet(url_array, params, true, type, REQUEST_TAG,
                new NetCallback<BaseResponse<List<ArrayData>>>() {
                    @Override
                    public void onSuccess(String url, BaseResponse<List<ArrayData>> response) {
                        List<ArrayData> data = response.data;
                        String md5 = response.md5;
                        LogUtil.i(TAG, "md5:" + md5);
                        LogUtil.i(TAG, "data size:" + data.size() + " :  data :" + data);
                        setResult(data.toString());
                    }

                    @Override
                    public void onFailed(String url, String errorMsg) {
                        LogUtil.e(TAG, url + " : " + errorMsg);
                    }
                });
    }

    @OnClick(R.id.btn_normal_post_obj)
    public void normalPostObj() {
        Map<String, String> params = buildParams();

        NetUtil.getInstance().requestAsyncPost(url_obj, params, true, ObjData.class, REQUEST_TAG,
                new NetCallback<BaseResponse<ObjData>>() {
                    @Override
                    public void onSuccess(String url, BaseResponse<ObjData> response) {
                        ObjData data = response.data;
                        String md5 = response.md5;
                        LogUtil.i(TAG, "md5:" + md5);
                        LogUtil.i(TAG, "data:" + data);
                        setResult(data.toString());
                    }

                    @Override
                    public void onFailed(String url, String errorMsg) {
                        LogUtil.e(TAG, url + " : " + errorMsg);
                    }
                });
    }

    @OnClick(R.id.btn_normal_post_array)
    public void normalPostArray() {
        Map<String, String> params = buildParams();
        Type type = new TypeToken<List<ArrayData>>() {
        }.getType();

        NetUtil.getInstance().requestAsyncPost(url_array, params, true, type, REQUEST_TAG,
                new NetCallback<BaseResponse<List<ArrayData>>>() {
                    @Override
                    public void onSuccess(String url, BaseResponse<List<ArrayData>> response) {
                        List<ArrayData> data = response.data;
                        String md5 = response.md5;
                        LogUtil.i(TAG, "md5:" + md5);
                        LogUtil.i(TAG, "data size:" + data.size() + " :  data :" + data);
                        setResult(data.toString());
                    }

                    @Override
                    public void onFailed(String url, String errorMsg) {
                        LogUtil.e(TAG, url + " : " + errorMsg);
                    }
                });
    }

}