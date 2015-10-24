package com.supercompany.alexeyp.github.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.supercompany.alexeyp.github.R;
import com.supercompany.alexeyp.github.tools.GithubConnector;
import com.supercompany.alexeyp.github.tools.GithubConnector.AuthenticationListener;

/**
 * Steps of splash screen working:
 * Success step 1. Try to get access code from {@link android.content.SharedPreferences}. If System
 * found access then go to step 4, else go to step 2.
 * Success step 2. Go to {@link GitHubActivity} and try to get verify code after success login.
 * Success step 3. Through {@link GithubConnector} try to get new access code.
 * Success step 4. Get access code and go to {@link UserInfoActivity}.
 */
public class SplashActivity extends BaseActivity {

    /**
     * Action for {@link GitHubActivity}.
     */
    private static final String ACTION_GITHUB = "com.supercompany.alexeyp.github";

    /**
     * Request code for result from {@link GitHubActivity}.
     */
    private static final int GIT_HUB_LOGIN_REQUEST = 1;

    /**
     * Key to extract verify code from {@link GitHubActivity}.
     */
    public static final String EXTRA_VERIFY_CODE = "VerifyCode";

    /**
     * Value to inform system is activity active at this moment.
     */
    private static boolean isSplashScreenActive = false;

    /**
     * Instance of github connector.
     */
    private GithubConnector githubConnector;

    /**
     * Implementation of {@link AuthenticationListener}.
     * Show progress bar when authentication request sent.
     * Hide progress bar when authentication is done (or failed).
     * After success run {@link UserInfoActivity} and close this.
     */
    private AuthenticationListener listener = new AuthenticationListener() {

        @Override
        public void onStart() {
            progressDialog.show();
        }

        @Override
        public void onSuccess() {
            Intent startUserInfoActivity = new Intent(SplashActivity.this, UserInfoActivity.class);
            startUserInfoActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startUserInfoActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(startUserInfoActivity);
            progressDialog.dismiss();
            finish();
        }

        @Override
        public void onFail(String error) {
            progressDialog.dismiss();
            messageDialog.setMessage(error).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        // Make delay for our splash screen.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isSplashScreenActive) {
                    if(!inetDetector.isConnectedToInternet()) {
                        messageDialog.setMessage("Internet connection not found.").show();
                    } else {
                        String accessToken = session.getAccessToken();
                        if (accessToken != null) {

                            Intent startLoginActivity = new Intent(SplashActivity.this,
                                    UserInfoActivity.class);
                            startLoginActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startLoginActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(startLoginActivity);
                            finish();

                        } else {
                            githubConnector = new GithubConnector(session);
                            githubConnector.setOauthListener(listener);

                            Intent getCodeIntent = new Intent(ACTION_GITHUB);
                            startActivityForResult(getCodeIntent, GIT_HUB_LOGIN_REQUEST);
                        }
                    }
                }
            }
        }, 3000);

    }

    @Override
    public void onStart() {
        super.onStart();
        isSplashScreenActive = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        isSplashScreenActive = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GIT_HUB_LOGIN_REQUEST) {
            if(resultCode == RESULT_OK) {
                githubConnector.getAccessToken(data.getStringExtra(EXTRA_VERIFY_CODE));
            } else {
                finish();
            }
        }
    }

}
