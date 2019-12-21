package com.example.qimozuoye.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;

import com.example.qimozuoye.R;
import com.example.qimozuoye.activity.MusicActivity;
import com.example.qimozuoye.bean.MyMedia;
import com.example.qimozuoye.util.CreateMedia;

import java.io.IOException;

public class MusicService extends Service {
    int media_number = 0;
    MediaPlayer mediaPlayer = new MediaPlayer();
    MyMedia media = CreateMedia.allmusicList.get(0);
    MyReceiver myReceiver;
    int currentPosition;

    private static final String ACTION_ALL = "com.example.qimozuoye.Media";
    private static final String ACTION_TITLE = "com.example.qimozuoye.Media.title";
    private static final String ACTION_START = "com.example.qimozuoye.Media.start";
    private static final String ACTION_NEXT = "com.example.qimozuoye.Media.next";
    private static final String ACTION_PREVIOUS = "com.example.qimozuoye.Media.previous";
    private static final String ACTION_DELETE = "com.example.qimozuoye.Media.delete";
    String CHANNEL_ONE_ID = "CHANNEL_ONE_ID";
    String CHANNEL_ONE_NAME= "CHANNEL_ONE_ID";
    NotificationChannel notificationChannel= null;
    Notification notification;
    RemoteViews remoteViews;

    class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String flag = intent.getAction();
            switch (flag){
                case ACTION_ALL:{
                    if( mediaPlayer.isPlaying() ){
                        mediaPlayer.stop();
                    }
                    mediaPlayer.release();
                    mediaPlayer = null;
                    stopSelf();
                    break;
                }
                case ACTION_TITLE:{
                    if( mediaPlayer.isPlaying() ){
                        mediaPlayer.pause();
                    }
                    Intent intent1 = new Intent(context,MusicActivity.class);
                    intent1.putExtra("media_number",media_number);
                    intent1.putExtra("currentPosition",mediaPlayer.getCurrentPosition());
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent1);
                    stopSelf();
                    break;
                }
                case ACTION_START:{
                    if( mediaPlayer.isPlaying() ){
                        mediaPlayer.pause();
                        notification.contentView.setImageViewResource(R.id.notification_start,R.drawable.notification_play);
                        startForeground(1,notification);
                    } else {
                        mediaPlayer.start();
                        notification.contentView.setImageViewResource(R.id.notification_start,R.drawable.notification_pause);
                        startForeground(1,notification);
                    }
                    break;
                }
                case ACTION_NEXT:{
                    mediaPlayer.stop();
                    media_number++;
                    media_number%=CreateMedia.allmusicList.size();
                    currentPosition = 0;
                    refreshMusic();
                    break;
                }
                case ACTION_PREVIOUS:{
                    mediaPlayer.stop();
                    media_number--;
                    media_number=(media_number+CreateMedia.allmusicList.size())%CreateMedia.allmusicList.size();
                    currentPosition = 0;
                    refreshMusic();
                    break;
                }
                case ACTION_DELETE:{
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                    stopSelf();
                    break;
                }
                default:{}
            }
        }
    }

    public MusicService() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        myReceiver = new MyReceiver();
        //2.创建intent-filter对象
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_ALL);
        filter.addAction(ACTION_TITLE);
        filter.addAction(ACTION_START);
        filter.addAction(ACTION_NEXT);
        filter.addAction(ACTION_PREVIOUS);
        filter.addAction(ACTION_DELETE);

        //3.注册广播接收者
        registerReceiver(myReceiver, filter);

        //进行8.0的判断
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel= new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
        }
        setNotice();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        media_number = intent.getIntExtra("media_number",0);
        currentPosition = intent.getIntExtra("currentPosition",0);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                media_number++;
                media_number%=CreateMedia.allmusicList.size();
                currentPosition = 0;
                refreshMusic();
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return true;
            }
        });

        refreshMusic();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        unregisterReceiver(myReceiver);
        stopForeground(true);
        super.onDestroy();
    }


    public void refreshMusic(){
        try {
            media = CreateMedia.allmusicList.get(media_number);
            CreateMedia.recordList.addFirst(media);
            refresh();
            mediaPlayer.reset();
            mediaPlayer.setDataSource(media.getDataPath());
            mediaPlayer.prepare();
            mediaPlayer.seekTo(currentPosition);
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e){
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setNotice(){
        remoteViews = new RemoteViews(this.getPackageName(),R.layout.notification_layout);// 获取remoteViews（参数一：包名；参数二：布局资源）
        setOnClick(remoteViews);
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext()).setContent(remoteViews);// 设置自定义的Notification内容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ONE_ID);
        }
        notification = builder
                .setOnlyAlertOnce(true)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .build();
        notification.flags|= Notification.FLAG_NO_CLEAR;
        startForeground(1, notification);
    }

    public void setOnClick(RemoteViews remoteViews){

        Intent intentStart = new Intent(ACTION_START);// 指定操作意图--设置对应的行为ACTION
        PendingIntent pIntentStart = PendingIntent.getBroadcast(this,
                0, intentStart, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews. setOnClickPendingIntent(R.id.notification_start, pIntentStart);

        Intent intentNext = new Intent(ACTION_NEXT);// 指定操作意图--设置对应的行为ACTION
        PendingIntent pIntentNext = PendingIntent.getBroadcast(this,
                0, intentNext, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notification_next, pIntentNext);

        Intent intentPrevious = new Intent(ACTION_PREVIOUS);// 指定操作意图--设置对应的行为ACTION
        PendingIntent pIntentPrevious = PendingIntent.getBroadcast(this,
                0, intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notification_previous, pIntentPrevious);

        Intent intentDelete = new Intent(ACTION_DELETE);// 指定操作意图--设置对应的行为ACTION
        PendingIntent pIntentDelete = PendingIntent.getBroadcast(this,
                0, intentDelete, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notification_delete, pIntentDelete);

        Intent intentTitle = new Intent(ACTION_TITLE);// 指定操作意图--设置对应的行为ACTION
        PendingIntent pIntentTitle = PendingIntent.getBroadcast(this,
                0, intentTitle, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notification_title, pIntentTitle);

    }

    public void refresh(){
        notification.contentView.setTextViewText(R.id.notification_title, media.getName());
        startForeground(1,notification);
    }

}
