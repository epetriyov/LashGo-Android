package com.check.android.social;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import com.check.android.ForActivity;
import com.check.android.ui.BaseActivity;
import com.check.android.ui.auth.LoginActivity;
import com.check.android.ui.auth.RegisterActivity;
import com.check.model.SocialTypes;
import com.check.model.dto.SocialInfo;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import junit.framework.Assert;
import org.holoeverywhere.widget.Toast;

import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    Handler handler;

    @Inject @ForActivity
    Context context;

    public FacebookHelper(LoginActivity loginActivity)
    {
        loginActivity.inject(this);
    }

    private Session.StatusCallback facebookCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            if (session.isOpened()) {
                /**
                 * successful login into facebook
                 */
                Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {

                    @Override
                    public void onCompleted(GraphUser user, final Response response) {
                        if (user != null) {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
                            final SocialInfo socialInfo = new SocialInfo();
                            socialInfo.setSocialType(SocialTypes.FACEBOOK);
                            socialInfo.setSurname(user.getLastName());
                            socialInfo.setName(user.getName());
                            if (!TextUtils.isEmpty(user.getBirthday()))
                                try {
                                    socialInfo.setBirthDay(simpleDateFormat.parse(user.getBirthday()));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    context.startActivity(RegisterActivity.buildIntent(context, socialInfo));
                                }
                            });
                        } else {
                            if (response.getError() != null && !TextUtils.isEmpty(response.getError().getErrorMessage())) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, response.getError().getErrorMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                Assert.fail();
                            }
                        }
                    }
                });
                request.executeAsync();
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
                    .setPermissions(Arrays.asList("basic_info", "email", "user_about_me", "user_birthday"))
                    .setCallback(facebookCallback));
        } else {
            Session.openActiveSession(activity, true, facebookCallback);
        }
    }


}
