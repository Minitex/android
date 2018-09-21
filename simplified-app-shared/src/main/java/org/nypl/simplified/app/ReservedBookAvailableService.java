package org.nypl.simplified.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import org.nypl.simplified.app.LoginActivity;

public class ReservedBookAvailableService extends JobService {

    public final int REQUEST_CODE_ASK_PERMISSIONS = 1001;
    @Override
    public boolean onStartJob(JobParameters params) {
       boolean bookIsAvailable = true;

        if(bookIsAvailable) {
            postBookIsAvailableNotification();
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    private void postBookIsAvailableNotification() {

        //define resource R.string.book_available_id_channel for android ) and above
        //this.getResources().getString(R.string.book_available_id_channel)

        //implement >=O (Oreo) and < O (Oreo) logic.  >= O (Oreo) uses channels, previous versions do not.
        //this will solve the deprecation below and make it work on >= O (Oreo)
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("Your reserved book is now available!")
                        .setContentText("BookXYZ is now available for checkout.");


        Intent notificationIntent = new Intent(this, LoginActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());

    }

}

