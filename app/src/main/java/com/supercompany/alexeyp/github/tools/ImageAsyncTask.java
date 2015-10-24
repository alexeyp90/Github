package com.supercompany.alexeyp.github.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Image Loader.
 */
public class ImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

    /**
     * Tag for logger.
     */
    private static final String IMAGE_TASK_TAG = "Image task";

    ImageView imageView;

    public ImageAsyncTask(ImageView imageView) {
        this.imageView = imageView;
    }

    protected Bitmap doInBackground(String... urls) {
        String urlDisplay = urls[0];
        Bitmap avatar = null;
        try {
            InputStream in = new java.net.URL(urlDisplay).openStream();
            avatar = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e(IMAGE_TASK_TAG, e.getMessage());
            e.printStackTrace();
        }
        return avatar;
    }

    protected void onPostExecute(Bitmap result) {
        imageView.setImageBitmap(result);
    }
}
