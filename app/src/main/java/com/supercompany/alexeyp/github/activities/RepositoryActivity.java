package com.supercompany.alexeyp.github.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.supercompany.alexeyp.github.R;
import com.supercompany.alexeyp.github.com.supercompany.alexeyp.github.adapters.RepositoryAdapter;
import com.supercompany.alexeyp.github.data.Repository;

import java.util.ArrayList;

/**
 * Allow get all repositories of user.
 */
public class RepositoryActivity extends BaseActivity<ArrayList<Repository>> {

    /**
     * Extra key to get repository which need to remove.
     */
    public static final String REPO_TO_REMOVE = "RepoToRemove";

    /**
     * Url part to 'get repositories' API.
     */
    private static final String REPOSITORIES_URL_PART = "/user/repos";

    /**
     * Request code to create new repo.
     */
    private static final int CREATE_NEW_REPO_CODE = 1;

    /**
     * Request code to delete new repo.
     */
    private static final int DELETE_REPO_CODE = 2;

    /**
     * Title and tag.
     */
    private static final String REPOSITORIES = "Repositories";

    /**
     * Message to user when he click to other repo.
     */
    private static final String NO_RIGHTS_TO_REMOVE = "You don't have rights to remove it.";

    /**
     * Used for set delay after deleting repo.
     * I don't know is this value will be enough.
     */
    private static final int DELAY_DELETE_MILLIS = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repository);

        setTitle(REPOSITORIES);

        apiConnector = new GenericAsyncTask<>(new TypeToken<ArrayList<Repository>>() {}.getType(), listener);
        apiConnector.execute(REPOSITORIES_URL_PART, GET_METHOD);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_new_repo) {
            Intent createRepoIntent = new Intent(RepositoryActivity.this, CreateRepositoryActivity.class);
            startActivityForResult(createRepoIntent, CREATE_NEW_REPO_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case CREATE_NEW_REPO_CODE:
                    apiConnector = new GenericAsyncTask<>(new TypeToken<ArrayList<Repository>>() {}.getType(), listener);
                    apiConnector.execute(REPOSITORIES_URL_PART, GET_METHOD);
                    break;
                case DELETE_REPO_CODE:
                    // Git hub removing slowly then create. So handler with delay.
                    progressDialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            apiConnector = new GenericAsyncTask<>(new TypeToken<ArrayList<Repository>>() {}.getType(), listener);
                            apiConnector.execute(REPOSITORIES_URL_PART, GET_METHOD);
                        }
                    }, DELAY_DELETE_MILLIS);
                    break;
                default: break;
            }
        }
    }

    /**
     * Implementation of {@link BaseActivity.AsyncTaskListener}.
     * After finishing fill data fields.
     */
    private BaseActivity.AsyncTaskListener listener = new BaseActivity.AsyncTaskListener() {

        @Override
        public void onFinishTask() {
            try {
                // Get repositories and set them to List.
                ArrayList<Repository> repositories = apiConnector.get();
                if (repositories != null) {
                    final ListView lvRepository = ((ListView) findViewById(R.id.repository_list));
                    lvRepository.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Repository repo = ((Repository)lvRepository.getItemAtPosition(position));
                            // Don't give access to remove, if owner other guy.
                            if(repo.getUserInfo().getLogin().equals(session.getLogin())) {
                                Intent intent = new Intent(RepositoryActivity.this, DeleteRepoActivity.class);
                                intent.putExtra(REPO_TO_REMOVE, repo);
                                startActivityForResult(intent, DELETE_REPO_CODE);
                            } else {
                                Toast.makeText(RepositoryActivity.this,
                                        NO_RIGHTS_TO_REMOVE, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    lvRepository.setAdapter(new RepositoryAdapter(RepositoryActivity.this, repositories));
                }
            } catch (Exception ex) {
                messageDialog.setMessage(SOME_SYSTEM_ERROR).show();
                ex.printStackTrace();
            }
        }
    };
}
