package com.supercompany.alexeyp.github.tools;

import android.os.AsyncTask;
import android.util.Log;

import com.supercompany.alexeyp.github.activities.BaseActivity;
import com.supercompany.alexeyp.github.activities.BaseActivity.*;
import com.supercompany.alexeyp.github.data.ApplicationData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Used for get access token from git hub and save it through {@link Session}.
 * TODO Seems like this Connector can be merged with {@link GenericAsyncTask} and then removed.
 */
public class GithubConnector {

    private static final String ACCESS_TOKEN = "access_token";

    /**
     * Path to access token.
     */
    private static final String REQUEST_ACCESS_TOKEN_URL = "https://github.com/login/oauth/access_token?client_id=" + ApplicationData.CLIENT_ID
            + "&client_secret=" + ApplicationData.CLIENT_SECRET
            + "&redirect_uri=" + ApplicationData.CALLBACK_URL;

    /**
     * Listener for main events.
     */
    private AuthenticationListener oauthListener;

    /**
     * Instance of session. Allow save access token.
     */
    private Session session;

    public void setOauthListener(AuthenticationListener oauthListener) {
        this.oauthListener = oauthListener;
    }

    public GithubConnector(Session session) {
        this.session = session;
    }

    public void getAccessToken(final String code) {
        oauthListener.onStart();
        new ConnectorAsyncTask().execute(code);
    }

    /**
     * Trasfer stream to String response.
     * @param inputStream stream for reading.
     * @return String from stream.
     * @throws IOException when system can't get String from stream.
     */
    private String streamToString(InputStream inputStream) throws IOException {
        if (inputStream != null) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                reader.close();
            } finally {
                inputStream.close();
            }
            return stringBuilder.toString();
        }
        return "";
    }

    /**
     * Async task to get access code.
     */
    class ConnectorAsyncTask extends AsyncTask<String, Void, String> {

        private static final String GH_CONNECTOR_TAG = "Git hub connector";

        private static final String FAILED_TO_GET_ACCESS_TOKEN = "Failed to get access token.";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // params[0] = code
        @Override
        protected String doInBackground(String... params)
        {
            try {
                URL url = new URL(REQUEST_ACCESS_TOKEN_URL + "&code=" + params[0]);
                Log.i(GH_CONNECTOR_TAG, "Try to open URL " + url.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url
                        .openConnection();
                urlConnection.setRequestMethod(BaseActivity.GET_METHOD);
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.connect();
                String response = streamToString(urlConnection
                        .getInputStream());
                Log.i(GH_CONNECTOR_TAG, "response " + response);

                Map<String, String> parameters = new HashMap<String, String>();
                for(String pair : response.split("&")) {
                    String separatedPair[] = pair.split("=");
                    if(separatedPair.length > 1) {
                        parameters.put(separatedPair[0], separatedPair[1]);
                    }
                }

                String accessToken = parameters.get(ACCESS_TOKEN);
                session.saveAccessToken(accessToken);
                Log.i(GH_CONNECTOR_TAG, "Got access token: " + accessToken);
                return accessToken;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null && !response.isEmpty()) {
                oauthListener.onSuccess();
            } else {
                oauthListener.onFail(FAILED_TO_GET_ACCESS_TOKEN);
            }
        }
    }


    /**
     * Created to report about actions when try to get access token.
     */
    public interface AuthenticationListener {

        /**
         * Start connecting to get access token.
         */
        void onStart();

        /**
         * Finished without errors and access code was saved.
         */
        void onSuccess();

        /**
         * Get access token implementation was failed.
         * @param error fail message.
         */
        void onFail(String error);
    }

}
