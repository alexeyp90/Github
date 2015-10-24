package com.supercompany.alexeyp.github.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.supercompany.alexeyp.github.tools.InternetDetector;
import com.supercompany.alexeyp.github.tools.Session;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This activity contain asynctask, which have already prepared for load data from github API:
 * for GET, POST and DELETE methods.
 *
 * @param <T> data type associated with child.
 */
public class BaseActivity<T> extends ActionBarActivity {

    /**
     * 'GET' method for API.
     */
    public static final String GET_METHOD = "GET";

    /**
     * 'POST' method for API.
     */
    public static final String POST_METHOD = "POST";

    /**
     * 'DELETE' method for API.
     */
    public static final String DELETE_METHOD = "DELETE";

    /**
     * OK text.
     */
    public static final String OK = "OK";

    /**
     * Main system error message.
     */
    public static final String SOME_SYSTEM_ERROR = "Some system error.";

    /**
     * Authorization property.
     */
    private static final String AUTHORIZATION = "Authorization";

    /**
     * Git Hub API.
     */
    private static final String API_URL = "https://api.github.com";

    /**
     * Tag for logger here.
     */
    private static final String BASE_TAG = "Base task loading";

    /**
     * Loading text for progress dialog.
     */
    private static final String LOADING = "Loading...";
    public static final String INTERNET_CONNECTION_NOT_FOUND = "Internet connection not found";

    /**
     * Message dialog for system.
     */
    protected AlertDialog.Builder messageDialog;

    /**
     * Current user's session.
     */
    protected Session session;

    /**
     * Progress dialog. Shown while async task try to load any information.
     */
    protected ProgressDialog progressDialog;

    /**
     * Instance of loader.
     */
    protected GenericAsyncTask<T> apiConnector;

    /**
     * Internet Detector instance.
     */
    protected InternetDetector inetDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialization session.
        session = new Session(this);

        // Initialization of progress dialog.
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(LOADING);

        // Initialization of message dialog.
        messageDialog = new AlertDialog.Builder(BaseActivity.this);
        messageDialog.setNeutralButton(OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        // Initialization of internet detector.
        inetDetector = new InternetDetector(BaseActivity.this);
    }

    /**
     * Async listener. Say when download is finished.
     */
    public interface AsyncTaskListener {
        void onFinishTask();
    }

    /**
     * Load data from git hub api.
     * Retrieve it as Object.
     *
     * @param <T> date type that will be returned.
     */
    class GenericAsyncTask<T> extends AsyncTask<String, Void, T> {

        /**
         * Loader get path from input array by this id.
         */
        public static final int PATH_TO_URL = 0;

        /**
         * Loader get request method from input array by this id.
         */
        public static final int REQUEST_METHOD = 1;

        /**
         * Loader get json body POST from input array by this id.
         */
        public static final int POST_DATA = 2;

        /**
         * Http code for No content answer.
         */
        public static final int NO_CONTENT_CODE = 204;

        /**
         * Response message for logger.
         */
        public static final String RESPONSE_MESSAGE = "Response message: ";

        /**
         * Object loaded message for logger.
         */
        public static final String OBJECT_IS_LOADED = "Object is loaded: ";

        /**
         * Logger message when system try to open url.
         */
        public static final String TRY_TO_OPEN_URL = "Try to open URL ";

        /**
         * Auth token.
         */
        public static final String AUTH_TOKEN = "token ";

        /**
         * Content type key property.
         */
        public static final String CONTENT_TYPE = "Content-Type";

        /**
         * Content type - json.
         */
        public static final String APPLICATION_JSON = "application/json";

        /**
         * Charset key property.
         */
        public static final String CHARSET = "charset";

        /**
         * Type utf.
         */
        public static final String UTF_8 = "utf-8";

        /**
         * Content length key property.
         */
        public static final String CONTENT_LENGTH = "Content-Length";

        /**
         * Type that api connector return.
         */
        private Type type;

        /**
         * Instance of listener for this async task.
         */
        private AsyncTaskListener listener;

        public GenericAsyncTask(Type type, AsyncTaskListener listener) {
            this.type = type;
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Checking internet connection every time before send request.
            if(!inetDetector.isConnectedToInternet()) {
                // I don't know: Is cancel(true) in this case bad style or not, but it works... :-)
                cancel(true);
                // Show message to user and then close activity.
                messageDialog.setMessage(INTERNET_CONNECTION_NOT_FOUND).show();
            } else {
                progressDialog.show();
            }
        }

        /**
         * @param params params[0] = path to api,
         *               params[1] = request method 'GET', 'POST' etc...
         *               params[2] = body for post.
         * @return result from api.
         */
        @SuppressWarnings("unchecked")
        @Override
        protected T doInBackground(String... params) {
            try {

                HttpURLConnection urlConnection = getHttpURLConnection(params);
                urlConnection.connect();

                Log.i(BASE_TAG, RESPONSE_MESSAGE + urlConnection.getResponseMessage());

                // No content code from DELETE.
                if(urlConnection.getResponseCode() == NO_CONTENT_CODE) {
                    return (T) OK;
                }

                // Parse response and return object.
                Gson gson = new Gson();
                final BufferedReader reader = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream()));
                T response = gson.fromJson(reader, type);
                Log.i(BASE_TAG, OBJECT_IS_LOADED + response.getClass());

                return response;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        /**
         * Create new connection to API.
         * @param params look at {@link GenericAsyncTask#doInBackground(String...)}
         * @return http connection
         * @throws IOException when system can't parse stream.
         */
        @NonNull
        private HttpURLConnection getHttpURLConnection(String[] params) throws IOException {
            URL url = new URL(API_URL + params[PATH_TO_URL]);

            Log.d(BASE_TAG, TRY_TO_OPEN_URL + url.toString());

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(params[REQUEST_METHOD]);
            urlConnection.setRequestProperty(AUTHORIZATION, AUTH_TOKEN + session.getAccessToken());
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);

            switch (params[REQUEST_METHOD]) {
                case POST_METHOD:
                    urlConnection.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON);
                    urlConnection.setRequestProperty(CHARSET, UTF_8);
                    urlConnection.setRequestProperty(CONTENT_LENGTH, Integer.toString(params[POST_DATA].getBytes().length));
                    urlConnection.setDoOutput(true);
                    DataOutputStream stream = new DataOutputStream( urlConnection.getOutputStream());
                    try {
                        stream.write( params[POST_DATA].getBytes() );
                    } finally {
                        try {
                            stream.close();
                        } catch (Throwable unused) {
                            unused.printStackTrace();
                        }
                    }
                    break;
                case DELETE_METHOD:
                    break;
                case GET_METHOD:
                    break;
                default:
                    break;

            }
            return urlConnection;
        }

        @Override
        protected void onPostExecute(T response) {
            super.onPostExecute(response);
            progressDialog.dismiss();
            listener.onFinishTask();
        }
    }
}
