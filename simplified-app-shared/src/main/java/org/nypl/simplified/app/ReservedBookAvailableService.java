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

        //read this: https://stackoverflow.com/questions/45015803/android-o-notification-channels-and-notificationcompat
/*


String idChannel = "my_channel_01";
        Intent mainIntent;

        mainIntent = new Intent(context, LauncherActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, 0);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel mChannel = null;
        // The id of the channel.

        int importance = NotificationManager.IMPORTANCE_HIGH;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, null);
        builder.setContentTitle(context.getString(R.string.app_name))
                .setSmallIcon(getNotificationIcon())
                .setContentIntent(pendingIntent)
                .setContentText(context.getString(R.string.alarm_notification) + ManagementDate.getIstance().hourFormat.format(getAlarm(context, 0)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(idChannel, context.getString(R.string.app_name), importance);
            // Configure the notification channel.
            mChannel.setDescription(context.getString(R.string.alarm_notification));
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);
        } else {
            builder.setContentTitle(context.getString(R.string.app_name))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setColor(ContextCompat.getColor(context, R.color.transparent))
                    .setVibrate(new long[]{100, 250})
                    .setLights(Color.YELLOW, 500, 5000)
                    .setAutoCancel(true);
        }
        mNotificationManager.notify(1, builder.build());


 */
        //define resource R.string.book_available_id_channel for android ) and above
        //this.getResources().getString(R.string.book_available_id_channel)

        //implement >=O and < 0 logic.  >=O uses channels, previous versions do not.
        //this will solve the deprecation below and make it work on >= O
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

