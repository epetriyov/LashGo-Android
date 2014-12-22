package com.lashgo.android.social;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import com.lashgo.android.R;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.auth.TwitterAuthActivity;
import com.lashgo.android.ui.dialogs.ErrorDialog;
import com.lashgo.model.dto.ErrorDto;
import com.lashgo.model.dto.SocialInfo;
import com.lashgo.model.dto.SocialNames;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: Eugene
 * Date: 23.02.14
 * Time: 20:56
 * To change this template use File | Settings | File Templates.
 */
public class TwitterHelper {

    public static final String KEY_REQUEST_TOKEN = "key_request_token";
    public static final int TWITTER_AUTH = 3;
    private static final String ERROR_DIALOG = "error_dialog";

    private RequestToken requestToken;

    private BaseActivity loginActivity;

    @Inject
    public TwitterHelper(BaseActivity baseActivity) {
        this.loginActivity = baseActivity;
        loginActivity.inject(this);
    }

    public void onCreate(Bundle requestTokenBundle) {
        if (requestTokenBundle != null) {
            this.requestToken = (RequestToken) requestTokenBundle.getSerializable(KEY_REQUEST_TOKEN);
        }
    }

    public void loginWithTwitter() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Twitter twitter = new TwitterFactory().getInstance();
                try {
                    twitter.setOAuthConsumer(loginActivity.getString(R.string.twitter_consumerKey), loginActivity.getString(R.string.twitter_consumerSecret));
                    String callbackURL = loginActivity.getString(R.string.twitter_callback_url);
                    requestToken = twitter.getOAuthRequestToken(callbackURL);
                } catch (Exception e) {
                    loginActivity.onDisplayError(e.toString());
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
                if (requestToken != null && !TextUtils.isEmpty(requestToken.getAuthenticationURL())) {
                    loginActivity.startActivityForResult(
                            TwitterAuthActivity.buildIntent(loginActivity, requestToken.getAuthenticationURL()), TWITTER_AUTH);
                } else {
                    ErrorDto errorDto = new ErrorDto();
                    errorDto.setErrorMessage(loginActivity.getString(R.string.twitter_error));
                    loginActivity.showDialog(ErrorDialog.newInstance(errorDto), ERROR_DIALOG);
                }
            }
        }.execute();
    }

    public void handleCallbackUrl(final Uri uri) {
        if (uri.toString().startsWith(loginActivity.getString(R.string.twitter_callback_url))) {
            new AsyncTask<Void, Void, Void>() {

                private SocialInfo socialInfo = new SocialInfo();

                @Override
                protected Void doInBackground(Void... params) {
                    String verifier = uri.getQueryParameter("oauth_verifier");
                    try {
                        Twitter twitter = new TwitterFactory().getInstance();
                        twitter.setOAuthConsumer(loginActivity.getResources().getString(R.string.twitter_consumerKey), loginActivity.getResources().getString(R.string.twitter_consumerSecret));
                        AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
                        if (accessToken != null) {
                            socialInfo = new SocialInfo(accessToken.getToken(), accessToken.getTokenSecret(), SocialNames.TWITTER);
                        }
                    } catch (TwitterException e) {
                        loginActivity.onDisplayError(e.toString());
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void v) {

                    loginActivity.onSocialLogin(socialInfo);
                }
            }.execute();
        }
    }

    public RequestToken getRequestToken() {
        return requestToken;
    }
}
