package com.lashgo.android.social;

import com.lashgo.android.ForActivity;
import com.lashgo.android.ui.auth.LoginActivity;
import com.facebook.Session;
import com.facebook.SessionState;
import com.lashgo.model.dto.SocialInfo;
import com.lashgo.model.dto.SocialNames;

import javax.inject.Inject;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Eugene
 * Date: 23.02.14
 * Time: 21:52
 * To change this template use File | Settings | File Templates.
 */
public class FacebookHelper {

    @Inject
    @ForActivity
    LoginActivity loginActivity;

    public FacebookHelper(LoginActivity loginActivity) {
        loginActivity.inject(this);
    }

    private Session.StatusCallback facebookCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            if (session.isOpened()) {
                /**
                 * successful login into facebook
                 */
                loginActivity.onSocialLogin(new SocialInfo(session.getAccessToken(),null,SocialNames.FACEBOOK));
            }
        }
    };

    public Session.StatusCallback getFacebookCallback() {
        return facebookCallback;
    }

    public void loginWithFacebook() {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(loginActivity)
                    .setPermissions(Arrays.asList("basic_info", "email", "user_about_me", "user_birthday"))
                    .setCallback(facebookCallback));
        } else {
            Session.openActiveSession(loginActivity, true, facebookCallback);
        }
    }


}
