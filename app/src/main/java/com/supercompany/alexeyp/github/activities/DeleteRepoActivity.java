package com.supercompany.alexeyp.github.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.supercompany.alexeyp.github.R;
import com.supercompany.alexeyp.github.data.Repository;

/**
 * Allow delete repository from github.
 */
public class DeleteRepoActivity extends BaseActivity<String> {

    /**
     * Url part to 'delete repo' API.
     */
    private static final String DELETE_REPO_URL_PART = "/repos";

    /**
     * Used for tag in logs and as title.
     */
    private static final String DELETE_REPO = "Delete repo";
    /**
     * Successfull message.
     */
    private static final String REMOVE_SUCCESSFUL = "Remove successful";

    /**
     * Removing status message for logger.
     */
    private static final String REMOVING_STATUS = "Removing status: ";

    /**
     * Error deleting failed.
     */
    private static final String ERROR_REMOVE_REPOSITORY = "Error while you try to remove repository. You are lucky :-).";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_repo);

        setTitle(DELETE_REPO);

        final Repository repositoryToRemove = (Repository) getIntent().getSerializableExtra(RepositoryActivity.REPO_TO_REMOVE);
        apiConnector = new GenericAsyncTask<>(Repository.class, listener);

        final String repoName = repositoryToRemove.getName();

        ((TextView) findViewById(R.id.tv_repository_name_value)).setText(repoName);
        ((TextView) findViewById(R.id.tv_repository_description_value)).setText(repositoryToRemove.getDescription());
        ((TextView) findViewById(R.id.tv_repository_homepage_value)).setText(repositoryToRemove.getHomepage());

        findViewById(R.id.b_delete_repo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiConnector.execute(DELETE_REPO_URL_PART + "/" + session.getLogin() + "/" + repoName, DELETE_METHOD);
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
                String response = apiConnector.get();
                if(response != null && response.equals(OK)) {
                    messageDialog.setMessage(REMOVE_SUCCESSFUL).show();

                    setResult(RESULT_OK);
                    Log.i(DELETE_REPO, REMOVING_STATUS + response);
                } else {
                    Toast.makeText(DeleteRepoActivity.this, ERROR_REMOVE_REPOSITORY, Toast.LENGTH_LONG).show();
                }
            } catch (Exception exception) {
                messageDialog.setMessage(SOME_SYSTEM_ERROR).show();
                exception.printStackTrace();
            }
        }
    };
}
