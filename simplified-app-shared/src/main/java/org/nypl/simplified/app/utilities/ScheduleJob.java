package org.nypl.simplified.app.utilities;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import org.nypl.simplified.app.ReservedBookAvailableService;

public final class ScheduleJob {

    private static final int RESERVED_BOOK_AVAILABLE_JOB_ID = 42;
    private static final long ONE_DAY_INTERVAL = 24 * 60 * 60 * 1000L; // 1 Day

    //TODO: review scheduling options with team (metered vs unmetered and charging vs not charging
    public static void scheduleReservedBookAvailableJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, ReservedBookAvailableService.class);
        JobInfo.Builder builder = new JobInfo.Builder(RESERVED_BOOK_AVAILABLE_JOB_ID, serviceComponent);

        builder.setPeriodic(ONE_DAY_INTERVAL);

        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY); // any type of network
        builder.setRequiresCharging(false); //we don't care if the device is charging or not
        //API 23 only JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        JobScheduler jobScheduler = (JobScheduler)context.getSystemService(context.JOB_SCHEDULER_SERVICE); //API 21 compat
        jobScheduler.schedule(builder.build());
    }

}
