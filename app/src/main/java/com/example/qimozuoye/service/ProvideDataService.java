package com.example.qimozuoye.service;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

import com.example.qimozuoye.SQL.DataBaseManger;
import com.example.qimozuoye.bean.MyMedia;
import com.example.qimozuoye.util.CreateMedia;
import com.example.qimozuoye.util.User;

public class ProvideDataService extends Service {
    public ProvideDataService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) throws IllegalStateException {
        final int a = intent.getIntExtra("a",0);
        final int b = intent.getIntExtra("b",0);
        final int c = intent.getIntExtra("c",0);
        final int e = intent.getIntExtra("e",0);
        SQLiteDatabase db = DataBaseManger.dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from media_table", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        if( count == 0 || e == 1 ){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d("QyzService","create start");
                    CreateMedia.createMusicList(CreateMedia.context);
                    CreateMedia.createVideoList(CreateMedia.context);
                    CreateMedia.createImageList(CreateMedia.context);
                    if( e == 1 ){
                        DataBaseManger.deleteAll(DataBaseManger.dbHelper);
                    }
                    DataBaseManger.insertDataToDataBase(DataBaseManger.dbHelper,CreateMedia.allvideoList);
                    DataBaseManger.insertDataToDataBase(DataBaseManger.dbHelper,CreateMedia.allmusicList);
                    DataBaseManger.insertDataToDataBase(DataBaseManger.dbHelper,CreateMedia.allimageList);
                    Log.d("QyzService","create end");
                    if( e == 1 ){
                        User.getProgressDialog().dismiss();
                    }
                    Intent intent1 = new Intent("com.example.qimozuoye.Data");
                    sendBroadcast(intent1);
                    stopSelf();
                }
            }).start();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d("QyzService","add start");
                    DataBaseManger.addDataFromDataBase(DataBaseManger.dbHelper, MyMedia.MEDIA_VIDEO,a);
                    DataBaseManger.addDataFromDataBase(DataBaseManger.dbHelper,MyMedia.MEDIA_MUSIC,b);
                    DataBaseManger.addDataFromDataBase(DataBaseManger.dbHelper,MyMedia.MEDIA_IMAGE,c);
                    Log.d("QyzService","add end");
                    Intent intent1 = new Intent("com.example.qimozuoye.Data");
                    sendBroadcast(intent1);
                }
            }).start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
