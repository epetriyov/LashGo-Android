package com.check.android.service;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created with IntelliJ IDEA.
 * User: Eugene
 * Date: 24.02.14
 * Time: 22:46
 * To change this template use File | Settings | File Templates.
 */
public class CheckErrorHandler implements ErrorHandler {
    @Override
    public Throwable handleError(RetrofitError retrofitError) {
        Response r = retrofitError.getResponse();
        if (r != null && r.getStatus() == 409) {
            return new AuthorizedException(retrofitError);
        }
        return retrofitError;
    }
}
