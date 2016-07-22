package com.example.netlibrary.net_new.response;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import android.support.annotation.IntDef;

/**
 * BaseResponse
 *
 * @param <T>
 *
 *  Created by zengfansheng on 2016/4/14.
 */
public class BaseResponseNew<T> {

    public static final String MD5 = "n";
    public static final String STATE = "s";
    public static final String DATA = "d";

    public static final int RESPONSE_STATE_ERROR = 0;
    public static final int RESPONSE_STATE_OK = 1;
    public static final int RESPONSE_STATE_NOT_UPDATE = 2;

    @RESPONSE_STATE
    public int state;

    public String md5;

    public T data;

    @IntDef({RESPONSE_STATE_ERROR, RESPONSE_STATE_OK, RESPONSE_STATE_NOT_UPDATE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RESPONSE_STATE {

    }

}
