package com.example.qimozuoye.SQL;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.qimozuoye.bean.MyMedia;
import com.example.qimozuoye.util.CreateMedia;

import java.util.LinkedList;
import java.util.List;

public class DataBaseManger {

    public static MyDatabaseHelper dbHelper = null;

    private static int[] loadNumber = {0,0,0};

    private DataBaseManger(){

    }

    public static void addDataFromDataBase(MyDatabaseHelper dbHelper, int type, int n) throws IllegalStateException {
        if( n <= 0 ){ return; }
        List<MyMedia> alllist = CreateMedia.allList[type];
        List<MyMedia> list = CreateMedia.lists[type];
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from media_table where type = ? limit ?,?",
                new String[]{""+type,""+loadNumber[type],""+(loadNumber[type] + n - 1)});
        loadNumber[type] += n;
        if ( cursor.moveToNext() ){
            MyMedia media;
            do{
                media = new MyMedia();
                media.setName(cursor.getString(cursor.getColumnIndex("name")));
                media.setType(type);
                media.setCoverPath(cursor.getString(cursor.getColumnIndex("coverPath")));
                media.setDataPath(cursor.getString(cursor.getColumnIndex("dataPath")));
                alllist.add(media);
                list.add(media);
            }while(cursor.moveToNext());
            cursor.close();
        }
        db.close();
    }

    public static void insertDataToDataBase(MyDatabaseHelper dbHelper, List<MyMedia> list){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        MyMedia media = null;
        for( int i = 0; i < list.size(); i++ ){
            media = list.get(i);
            db.execSQL("insert into media_table (name,type,coverPath,dataPath) values(?,?,?,?)",
                    new String[]{media.getName(),"" + media.getType(),media.getCoverPath(),media.getDataPath()});
        }
        db.close();
    }

    public static void select(MyDatabaseHelper dbHelper,List<MyMedia> list, String key){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from media_table where name like '%?%'",
                new String[]{key});
        if ( cursor.moveToNext() ){
            MyMedia media;
            do{
                media = new MyMedia();
                media.setName(cursor.getString(cursor.getColumnIndex("name")));
                media.setType(cursor.getInt(cursor.getColumnIndex("type")));
                media.setCoverPath(cursor.getString(cursor.getColumnIndex("coverPath")));
                media.setDataPath(cursor.getString(cursor.getColumnIndex("dataPath")));
                list.add(media);
            }while(cursor.moveToNext());
            cursor.close();
        }
        db.close();
    }

    public static void deleteAll(MyDatabaseHelper dbHelper){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM media_table");
        db.close();
    }

}
