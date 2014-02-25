package com.check.android.service;

import retrofit.RetrofitError;

/**
 * Created with IntelliJ IDEA.
 * User: Eugene
 * Date: 24.02.14
 * Time: 22:49
 * To change this template use File | Settings | File Templates.
 */
public class AuthorizedException extends Throwable {
    public AuthorizedException(RetrofitError retrofitError) {
        super(retrofitError);
    }
}
