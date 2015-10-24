package com.supercompany.alexeyp.github.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.supercompany.alexeyp.github.R;
import com.supercompany.alexeyp.github.data.UserInfo;
import com.supercompany.alexeyp.github.tools.ImageAsyncTask;

/**
 * Load data from git hub by this /user
 * Show screen with user information.
 */
public class UserInfoActivity extends BaseActivity<UserInfo> {

    /**
     * Url part to 'get user' API.
     */
    private static final String USER_URL_PART = "/user";

    /**
     * Start title for this activity.
     */
    private static final String USER_INFO = "User info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_information_activity);

        setTitle(USER_INFO);

        findViewById(R.id.button_repositories).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfoActivity.this, RepositoryActivity.class);
                startActivity(intent);
            }
        });

        if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
            findViewById(R.id.button_logout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    session.resetAccessToken(UserInfoActivity.this);
                    finish();
                }
            });
        } else {
            findViewById(R.id.button_logout).setVisibility(View.GONE);
        }


        apiConnector = new GenericAsyncTask<>(UserInfo.class, listener);
        apiConnector.execute(USER_URL_PART, GET_METHOD);
    }

    /**
     * Implementation of {@link BaseActivity.AsyncTaskListener}.
     * After finishing fill data fields.
     */
    BaseActivity.AsyncTaskListener listener = new BaseActivity.AsyncTaskListener() {

        @Override
        public void onFinishTask() {
            try {
                UserInfo userInfo = apiConnector.get();
                if (userInfo != null) {
                    // Save login into system.
                    session.saveLogin(userInfo.getLogin());

                    setTitle(userInfo.getLogin());

                    // Fill all fields in activity.
                    ((TextView) findViewById(R.id.login_value)).setText(userInfo.getLogin());
                    ((TextView) findViewById(R.id.id_value)).setText(userInfo.getId());
                    ((TextView) findViewById(R.id.name_value)).setText(userInfo.getName());
                    ((TextView) findViewById(R.id.company_value)).setText(userInfo.getCompany());
                    ((TextView) findViewById(R.id.location_value)).setText(userInfo.getLocation());
                    ((CheckBox) findViewById(R.id.hireable_value)).setChecked(userInfo.getHireable() != null ? userInfo.getHireable() : false);
                    ((TextView) findViewById(R.id.public_repos_value)).setText(String.valueOf(userInfo.getPublicRepos()));
                    ((TextView) findViewById(R.id.public_gists_value)).setText(String.valueOf(userInfo.getPublicGists()));
                    ((TextView) findViewById(R.id.followers_value)).setText(String.valueOf(userInfo.getFollowers()));
                    ((TextView) findViewById(R.id.followering_value)).setText(String.valueOf(userInfo.getFollowing()));
                    ((TextView) findViewById(R.id.created_at_value)).setText(userInfo.getCreatedAt().toString());
                    ((TextView) findViewById(R.id.updated_at_value)).setText(userInfo.getUpdatedAt().toString());

                    // Load image if exists.
                    if (userInfo.getAvatarUrl() != null) {
                        new ImageAsyncTask((ImageView) findViewById(R.id.avatar))
                                .execute(userInfo.getAvatarUrl());
                    }
                }
            } catch (Exception ex) {
                messageDialog.setMessage(SOME_SYSTEM_ERROR);
                ex.printStackTrace();
            }
        }
    };

}
