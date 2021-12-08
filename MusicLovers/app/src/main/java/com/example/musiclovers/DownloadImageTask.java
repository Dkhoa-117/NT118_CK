package com.example.musiclovers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

/**
 * DONE
 */
//Download the image to display in the play_music, Do the AsyncTask to avoid NetworkOnMainThreadException
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView imageView;
    public DownloadImageTask(ImageView imageView) {
        this.imageView = imageView;
    }
    protected Bitmap doInBackground(String... urls) {
        String urlDisplay = urls[0];
        Bitmap bitmap = null;
        try {
            InputStream inputStream = new URL(urlDisplay).openStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        }catch (Exception e) {
        }
        return bitmap;
    }
    protected void onPostExecute(Bitmap result) {
        imageView.setImageBitmap(result);
    }
}