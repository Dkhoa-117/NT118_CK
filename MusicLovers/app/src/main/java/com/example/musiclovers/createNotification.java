package com.example.musiclovers;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.musiclovers.services.NotificationActionService;

import java.io.InputStream;
import java.net.URL;

/**
 * DONE
 */
public class createNotification extends AsyncTask<String, Void, Bitmap> {

    public static final String CHANNEL_ID_1 = "channel1";
    public static final String CHANNEL_NAME_1 = "FIRST_CHANNEL";

    public static final String ACTION_PREVIOUS = "actionprevious";
    public static final String ACTION_PLAY = "actionplay";
    public static final String ACTION_NEXT = "actionnext";
    public static final String ACTION_LIKE = "actionlike";


    Context context;
    String artistName, songName;
    int btnPlayOrPause, position, size;

    public createNotification(Context context, String songName, String artistName, int btnPlayOrPause, int position, int size) {
        super();
        this.context = context;
        this.songName = songName;
        this.artistName = artistName;
        this.btnPlayOrPause = btnPlayOrPause;
        this.position = position;
        this.size = size;
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        String urlDisplay = urls[0];
        try {
            InputStream inputStream = new URL(urlDisplay).openStream();
            return BitmapFactory.decodeStream(inputStream);

        }catch (Exception e) {
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        PendingIntent pendingIntentPrevious;
        int btnBackward;
        if (position == 0){
            pendingIntentPrevious = null;
            btnBackward = R.drawable.ic_backward_off;
        } else {
            Intent intentPrevious = new Intent(context, NotificationActionService.class)
                    .setAction(ACTION_PREVIOUS);
            pendingIntentPrevious = PendingIntent.getBroadcast(context, 0,
                    intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);
            btnBackward = R.drawable.ic_backward;
        }

        Intent intentPlay = new Intent(context, NotificationActionService.class)
                .setAction(ACTION_PLAY);
        PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context, 0,
                intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentLike = new Intent(context, NotificationActionService.class)
                .setAction(ACTION_LIKE);
        PendingIntent pendingIntentLike = PendingIntent.getBroadcast(context, 0,
                intentLike, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentBackToApp = new Intent(context, MainActivity.class)
                .setAction(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntentBackToApp = PendingIntent.getActivity(context, 0,
                intentBackToApp, 0);

        PendingIntent pendingIntentNext;
        int btnForward;
        if (position == (size - 1)){
            pendingIntentNext = null;
            btnForward = R.drawable.ic_forward_off;
        } else {
            Intent intentNext = new Intent(context, NotificationActionService.class)
                    .setAction(ACTION_NEXT);
            pendingIntentNext = PendingIntent.getBroadcast(context, 0,
                    intentNext, PendingIntent.FLAG_UPDATE_CURRENT);
            btnForward = R.drawable.ic_forward;
        }
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID_1)
                .setSmallIcon(R.drawable.ic_music)
                .setContentTitle(songName)
                .setContentText(artistName)
                .addAction(btnBackward, "previous", pendingIntentPrevious)
                .addAction(btnPlayOrPause, "play", pendingIntentPlay)
                .addAction(btnForward, "next", pendingIntentNext)
                .addAction(R.drawable.ic_unfill_heart, "like", pendingIntentLike)
                .setContentIntent(pendingIntentBackToApp)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2))
                .setLargeIcon(bitmap)
                .setOnlyAlertOnce(true)
                .setShowWhen(false)
                .build();
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(1, notification);
    }
}

