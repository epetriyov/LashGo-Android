package com.check.android.social;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import com.check.android.R;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

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


    private SocialErrorShower socialErrorShower;

    public TwitterHelper(SocialErrorShower socialErrorShower, Bundle requestTokenBundle) {
        this.socialErrorShower = socialErrorShower;
        if (requestTokenBundle != null) {
            this.requestToken = (RequestToken) requestTokenBundle.getSerializable(KEY_REQUEST_TOKEN);
        }
    }


    public void loginWithTwitter(final
                                 Context context) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Twitter twitter = new TwitterFactory().getInstance();
                try {
                    twitter.setOAuthConsumer(context.getString(R.string.twitter_consumerKey), context.getString(R.string.twitter_consumerSecret));
                    String callbackURL = context.getString(R.string.twitter_callback_url);
                    requestToken = twitter.getOAuthRequestToken(callbackURL);
                } catch (Exception e) {
                    socialErrorShower.onDisplayError(e.toString());
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
                if (requestToken != null) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));
                }
            }
        }.execute();
    }

    public void handleCallbackUrl(final Context context, final Uri uri) {
        if (uri.toString().startsWith(context.getString(R.string.twitter_callback_url))) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    String token = uri.getQueryParameter("oauth_token");
                    String verifier = uri.getQueryParameter("oauth_verifier");
                    try {
                        Twitter t = new TwitterFactory().getInstance();
                        t.setOAuthConsumer(context.getResources().getString(R.string.twitter_consumerKey), context.getResources().getString(R.string.twitter_consumerSecret));
                        AccessToken accessToken = t.getOAuthAccessToken(requestToken, verifier);
                        long userID = accessToken.getUserId();
                        User user = t.showUser(userID);
                        String name = user.getName();
                        //TODO inner login
                    } catch (TwitterException e) {
                        socialErrorShower.onDisplayError(e.toString());
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void v) {

                }
            }.execute();
        }
    }

    public RequestToken getRequestToken() {
        return requestToken;
    }
}
