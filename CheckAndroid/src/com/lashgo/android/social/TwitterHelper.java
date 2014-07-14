package com.lashgo.android.social;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import com.lashgo.android.R;
import com.lashgo.android.ui.BaseActivity;
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

    private RequestToken requestToken;

    private BaseActivity loginActivity;

    @Inject
    public TwitterHelper(BaseActivity baseActivity) {
        this.loginActivity = baseActivity;
        baseActivity.inject(this);
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
                if (requestToken != null) {
                    loginActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));
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
                        socialInfo = new SocialInfo(accessToken.getToken(), accessToken.getTokenSecret(), SocialNames.TWITTER);
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
