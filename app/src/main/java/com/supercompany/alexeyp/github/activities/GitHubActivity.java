package com.supercompany.alexeyp.github.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.supercompany.alexeyp.github.R;
import com.supercompany.alexeyp.github.data.ApplicationData;

/**
 * This acitivty does not require a {@link BaseActivity}, because there we don't load any data.
 * This activity provide ability to load github page through {@link WebView} and retrieve verify
 * code to {@link SplashActivity}.
 */
public class GitHubActivity extends Activity {

    /**
     * Oauth url for autorization.
     */
    private static final String OAUTH_URL = "https://github.com/login/oauth/authorize?";

    /**
     * Git hub title and tag.
     */
    private static final String GITHUB = "Github";

    /**
     * Scope parameter to register app.
     */
    public static final String SCOPE_PARAM = "&scope=user,repo,public_repo,delete_repo";

    /**
     * Client id param.
     */
    public static final String CLIENT_ID = "client_id=";

    /**
     * Redirect uri param.
     */
    public static final String REDIRECT_URI = "&redirect_uri=";

    /**
     * Github url to connect.
     */
    private static final String URL_TO_GITHUB = OAUTH_URL + CLIENT_ID + ApplicationData.CLIENT_ID +
            REDIRECT_URI + ApplicationData.CALLBACK_URL + SCOPE_PARAM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_github);

        setTitle(GITHUB);

        // Init webview and run.
        WebView webView = (WebView)findViewById(R.id.github_web_view);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebViewClient(new OAuthWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(URL_TO_GITHUB);
    }

    private class OAuthWebViewClient extends WebViewClient {

        /**
         * Logger message - loading url.
         */
        public static final String LOADING_URL = "Loading URL: ";

        /**
         * Logger message - page error.
         */
        public static final String PAGE_ERROR = "Page error: ";

        /**
         * Logger message - redirect url.
         */
        public static final String REDIRECTING_URL = "Redirecting URL ";

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(GITHUB, REDIRECTING_URL + url);
            if (url.startsWith(ApplicationData.CALLBACK_URL)) {
                String urls[] = url.split("=");
                Intent intent = new Intent();
                intent.putExtra(SplashActivity.EXTRA_VERIFY_CODE, urls[1]);
                setResult(RESULT_OK, intent);
                finish();
                return true;
            }
            return false;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            Log.d(GITHUB, PAGE_ERROR + description);
            super.onReceivedError(view, errorCode, description, failingUrl);
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d(GITHUB, LOADING_URL + url);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

    }

}
