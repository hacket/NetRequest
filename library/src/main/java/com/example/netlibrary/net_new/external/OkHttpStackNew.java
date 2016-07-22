package com.example.netlibrary.net_new.external;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.android.volley.toolbox.HurlStack;

import okhttp3.OkHttpClient;
import okhttp3.OkUrlFactory;

/**
 * OkHttpStack
 */
public class OkHttpStackNew extends HurlStack {

    private final OkUrlFactory mFactory;
    private static OkHttpClient internalClient;

    /**
     * 获取全局使用的OkHttpClient
     */
    public static OkHttpClient getOkHttpClient() {
        if (internalClient == null) {
            synchronized (OkHttpStackNew.class) {
                if (internalClient == null) {
                    internalClient = new OkHttpClient.Builder().build();
                }
            }
        }
        return internalClient;
    }

    public OkHttpStackNew() {
        this(getOkHttpClient());
    }

    public OkHttpStackNew(OkHttpClient client) {
        if (client == null) {
            throw new NullPointerException("Client must not be null.");
        }
        mFactory = new OkUrlFactory(client);
    }

    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {
        return mFactory.open(url);
    }

}
