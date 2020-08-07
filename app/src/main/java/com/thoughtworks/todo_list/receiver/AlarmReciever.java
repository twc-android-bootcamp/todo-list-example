package com.thoughtworks.todo_list.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.thoughtworks.todo_list.MainApplication;
import com.thoughtworks.todo_list.R;
import com.thoughtworks.todo_list.ui.task.TaskEditActivity;

public class AlarmReciever extends BroadcastReceiver {

    public static final String CHANNEL_ID = "todo";
    public static final String CHANNEL_NAME = "todo list";

    @Override
    @SuppressWarnings("all")
    public void onReceive(Context context, Intent intent) {
        long taskId = intent.getLongExtra("taskId", 0);
        if (taskId != 0) {
            ((MainApplication) context.getApplicationContext()).getTaskRepository().findById(taskId)
                    .subscribe(task -> {
                        if (task.isRemind() && !task.isDone()) {
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                            createNotificationChannel(context,notificationManager);

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
                            builder.setSmallIcon(R.mipmap.todo_list_logo);
                            builder.setContentTitle(task.getTitle());
                            builder.setContentText(task.getDescription());
                            builder.setAutoCancel(true);
                            Intent in = new Intent(context, TaskEditActivity.class);
                            in.putExtra(TaskEditActivity.EXTRAS_TASK_ID, taskId);
                            PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) task.getId(), in, 0);
                            builder.setContentIntent(pendingIntent);
                            notificationManager.notify((int) task.getId(), builder.build());
                        }
                    });
        }
    }

    private void createNotificationChannel(Context context,NotificationManagerCompat notificationManager) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
        }
    }

}
