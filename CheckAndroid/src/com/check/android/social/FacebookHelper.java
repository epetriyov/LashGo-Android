package com.check.android.social;

import android.app.Activity;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Eugene
 * Date: 23.02.14
 * Time: 21:52
 * To change this template use File | Settings | File Templates.
 */
public class FacebookHelper {

    private Session.StatusCallback facebookCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            if (session.isOpened()) {
                /**
                 * successful login into facebook
                 */
                Request.newMeRequest(session, new Request.GraphUserCallback() {

                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        //TODO inner login
                    }
                }).executeAsync();
            }
        }
    };

    public Session.StatusCallback getFacebookCallback() {
        return facebookCallback;
    }

    public void loginWithFacebook(Activity activity) {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(activity)
                    .setPermissions(Arrays.asList("basic_info"))
                    .setCallback(facebookCallback));
        } else {
            Session.openActiveSession(activity, true, facebookCallback);
        }
    }


}
