package com.example.netlibrary.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.android.volley.toolbox.HurlStack;

import okhttp3.OkHttpClient;
import okhttp3.OkUrlFactory;

/**
 * OkHttpStack
 */
public class OkHttpStack extends HurlStack {

    private final OkUrlFactory mFactory;
    private static OkHttpClient internalClient;

    /**
     * 获取全局使用的OkHttpClient
     */
    public static OkHttpClient getOkHttpClient() {
        if (internalClient == null) {
            synchronized (OkHttpStack.class) {
                if (internalClient == null) {
                    internalClient = new OkHttpClient.Builder().build();
                }
            }
        }
        return internalClient;
    }

    public OkHttpStack() {
        this(getOkHttpClient());
    }

    public OkHttpStack(OkHttpClient client) {
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
