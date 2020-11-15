package com.zivapp.notes.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.zivapp.notes.R;
import com.zivapp.notes.model.GroupNote;
import com.zivapp.notes.views.mainmenu.MenuNotesActivity;

public class AppNotifications {
    private static final String CHANNEL_ID = "101";
    //TODO: change the ID
    private int notificationId = 202;

    private final Context context;

    public AppNotifications(Context context) {
        this.context = context;
    }

    public void notifyNewMessages(GroupNote note) {
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(context, MenuNotesActivity.class);
//        intent.putExtra("id_note", note.getUid());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                //TODO: change the picture
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle(note.getMember())
                .setContentText(note.getMessage())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        showNotification(builder);
    }

    private void showNotification(NotificationCompat.Builder builder) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        // Remember to save the notification ID that you pass to NotificationManagerCompat.notify()
        // because you'll need it later if you want to update or remove the notification.
        notificationManager.notify(notificationId, builder.build());
    }

    public void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //TODO: change the strings in getString methods below
            CharSequence name = context.getString(R.string.app_name);
            String description = context.getString(R.string.hint_message);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
