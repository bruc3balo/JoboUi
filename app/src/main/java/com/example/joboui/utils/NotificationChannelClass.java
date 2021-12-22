package com.example.joboui.utils;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import com.example.joboui.R;


public class NotificationChannelClass extends Application {

    //Channels
    public static final String MEDIA_NOTIFICATION_CHANNEL = "Media Content";
    public static final String USER_NOTIFICATION_CHANNEL = "User Content";
    public static final String SYNCH_NOTIFICATION_CHANNEL = "Synchronization Channel";

    //Priority
    public static final String LOW_PRIORITY = "Low";
    public static final String MID_PRIORITY = "Mid";
    public static final String HIGH_PRIORITY = "High";

    //Category


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();

    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            //Channel Group
            NotificationChannelGroup mediaGroup = new NotificationChannelGroup(MEDIA_NOTIFICATION_CHANNEL, MEDIA_NOTIFICATION_CHANNEL);
            NotificationChannelGroup userGroup = new NotificationChannelGroup(USER_NOTIFICATION_CHANNEL, USER_NOTIFICATION_CHANNEL);
            NotificationChannelGroup synchGroup = new NotificationChannelGroup(SYNCH_NOTIFICATION_CHANNEL, SYNCH_NOTIFICATION_CHANNEL);


            //MediaChannel
            // Creating an Audio Attribute


            NotificationChannel mediaChannel = new NotificationChannel(MEDIA_NOTIFICATION_CHANNEL, "Media Channel", NotificationManager.IMPORTANCE_HIGH);
            mediaChannel.setDescription(getString(R.string.media_channel_description));
            mediaChannel.setSound(getPopUri(this).getKey(0), getPopUri(this).getValue(0));
            mediaChannel.setGroup(MEDIA_NOTIFICATION_CHANNEL);


            //UserChannel
            NotificationChannel userChannel = new NotificationChannel(USER_NOTIFICATION_CHANNEL, "User Channel", NotificationManager.IMPORTANCE_HIGH);
            userChannel.setDescription(getString(R.string.user_channel_description));
            userChannel.setGroup(USER_NOTIFICATION_CHANNEL);
            userChannel.setSound(getPopUri(this).getKey(0), getPopUri(this).getValue(0));

            //Sync Channel
            NotificationChannel synchChannel = new NotificationChannel(SYNCH_NOTIFICATION_CHANNEL, " Synchronization Channel", NotificationManager.IMPORTANCE_DEFAULT);
            synchChannel.setDescription(getString(R.string.synch_notification_description));
            synchChannel.setGroup(SYNCH_NOTIFICATION_CHANNEL);
            synchChannel.setSound(getPopUri(this).getKey(0), getPopUri(this).getValue(0));


            //Notification Manager
            NotificationManager manager = getSystemService(NotificationManager.class);

            manager.createNotificationChannelGroup(userGroup);
            manager.createNotificationChannelGroup(mediaGroup);
            manager.createNotificationChannelGroup(synchGroup);

            manager.createNotificationChannel(mediaChannel);
            manager.createNotificationChannel(userChannel);
            manager.createNotificationChannel(synchChannel);

        }
    }

    public static MyLinkedMap<Uri, AudioAttributes> getPopUri(Context context) {
        MyLinkedMap<Uri, AudioAttributes> audioAttributesMyLinkedMap = new MyLinkedMap<>();

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();


        audioAttributesMyLinkedMap.put(Uri.parse("android.resource://" + context.getApplicationContext().getPackageName() + "/" + R.raw.pop), audioAttributes);
        return audioAttributesMyLinkedMap;
    }



}
