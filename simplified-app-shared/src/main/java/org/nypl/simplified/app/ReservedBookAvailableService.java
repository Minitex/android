package org.nypl.simplified.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class ReservedBookAvailableService extends JobService {

    public final int REQUEST_CODE_ASK_PERMISSIONS = 1001;
    @Override
    public boolean onStartJob(JobParameters params) {
       boolean bookIsAvailable = true;

        if(bookIsAvailable) {
            postBookIsAvailableNotification();
        }
        return false; //this indicates that the scheduled job is done, true will burn battery if not handled correctly
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    private void postBookIsAvailableNotification() {

        String idChannel = "NYPL_BOOK_AVAILABLE_CHANNEL_ID";
        Intent mainIntent;

        mainIntent = new Intent(this, LoginActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mainIntent, 0);

        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel mChannel = null;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, null);

        //TODO: .setSmallIcon(getNotificationIcon()) get an icon for notification
        builder.setContentTitle(this.getString(R.string.app_name))
                .setContentIntent(pendingIntent)
                .setContentText(this.getString(R.string.book_available_alarm_msg));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            mChannel = new NotificationChannel(idChannel, this.getString(R.string.app_name), importance);
            // Configure the notification channel.
            mChannel.setDescription(this.getString(R.string.book_available_alarm_msg));
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);
        } else {
            builder.setContentTitle(this.getString(R.string.app_name))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setColor(ContextCompat.getColor(this, R.color.app_primary_color))
                    .setVibrate(new long[]{100, 250})
                    .setLights(Color.YELLOW, 500, 5000)
                    .setAutoCancel(true);
        }

        try {
            mNotificationManager.notify(1, builder.build());
        } catch(RuntimeException re) {
            Log.d("Notification", "Filed to send reserved book available notification: " + re.getMessage());
        }

    }

}

