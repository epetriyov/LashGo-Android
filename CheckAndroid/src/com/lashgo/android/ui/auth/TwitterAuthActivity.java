package com.lashgo.android.ui.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.lashgo.android.R;
import com.lashgo.android.ui.BaseActivity;

/**
 * Created by Eugene on 14.09.2014.
 */
public class TwitterAuthActivity extends Activity {

    private String twitterUrl;

    public static Intent buildIntent(Context context, String twitterUrl) {
        Intent intent = new Intent(context, TwitterAuthActivity.class);
        intent.putExtra(BaseActivity.ExtraNames.TWITTER_URL.name(), twitterUrl);
        return intent;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(BaseActivity.ExtraNames.TWITTER_URL.name(), twitterUrl);
        super.onSaveInstanceState(outState);
    }

    private void initExtras(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {
            twitterUrl = intent.getStringExtra(BaseActivity.ExtraNames.TWITTER_URL.name());
        }
        if (savedInstanceState != null && twitterUrl == null) {
            twitterUrl = savedInstanceState.getString(BaseActivity.ExtraNames.TWITTER_URL.name());
        }
        if (TextUtils.isEmpty(twitterUrl)) {
            throw new IllegalStateException("Twitter url can't be empty");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initExtras(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.act_twitter_login);
        WebView webView = (WebView) findViewById(R.id.web_view);
        webView.setWebViewClient(new TwitterWebViewClient());
        webView.loadUrl(twitterUrl);
    }

    private class TwitterWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains(getResources().getString(R.string.twitter_callback_url))) {
                Uri uri = Uri.parse(url);
                Intent intent = getIntent();
                intent.setData(uri);
                setResult(RESULT_OK, intent);
                finish();
                return true;
            }
            return false;
        }
    }
}
