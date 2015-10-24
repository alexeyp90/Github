package com.supercompany.alexeyp.github.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.supercompany.alexeyp.github.R;
import com.supercompany.alexeyp.github.data.Repository;

/**
 * Allow create new repository on github.
 */
public class CreateRepositoryActivity extends BaseActivity<Repository> {

    /**
     * Url part to 'create repo' API.
     */
    private static final String REPOSITORIES_URL_PART = "/user/repos";

    /**
     * Successful message for creating.
     */
    private static final String REPO_WAS_CREATED_WITH_NAME = "Repo was created with name: ";

    /**
     * Create repo title and tag.
     */
    private static final String CREATE_REPO = "Create repo";

    /**
     * Error message when name is empty.
     */
    private static final String NAME_IS_REQUIRED = "Name is required.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_repository);

        setTitle(CREATE_REPO);

        apiConnector = new GenericAsyncTask<>(Repository.class, listener);

        findViewById(R.id.b_create_new_repo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new repo and fill 3 fields.
                Repository repository = new Repository();
                String name = ((EditText) findViewById(R.id.et_repository_name_value)).getText().toString();
                if (!name.isEmpty()) {
                    repository.setName(name);
                    repository.setDescription(((EditText) findViewById(R.id.et_repository_description_value)).getText().toString());
                    repository.setHomepage(((EditText) findViewById(R.id.et_repository_homepage_value)).getText().toString());

                    Gson gson = new Gson();
                    String json = gson.toJson(repository);

                    apiConnector.execute(REPOSITORIES_URL_PART, POST_METHOD, json);
                } else {
                    // If name field is empty show error to user.
                    ((EditText) findViewById(R.id.et_repository_name_value)).setError(NAME_IS_REQUIRED);
                }
            }
        });
    }

    /**
     * Implementation of {@link BaseActivity.AsyncTaskListener}.
     * After finishing fill data fields.
     */
    private BaseActivity.AsyncTaskListener listener = new BaseActivity.AsyncTaskListener() {

        @Override
        public void onFinishTask() {
            try {
                // Get repo from api connector.
                Repository repository = apiConnector.get();
                if (repository != null) {
                    messageDialog.setMessage(REPO_WAS_CREATED_WITH_NAME + repository.getName()).show();
                    setResult(RESULT_OK);
                    Log.i(CREATE_REPO, REPO_WAS_CREATED_WITH_NAME + repository.getName());
                }
            } catch (Exception exception) {
                messageDialog.setMessage(SOME_SYSTEM_ERROR).show();
                exception.printStackTrace();
            }
        }
    };
}
