package com.example.qimozuoye.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.example.qimozuoye.R;
import com.example.qimozuoye.bean.MyMedia;
import com.example.qimozuoye.service.ProvideDataService;

import java.util.ArrayList;
import java.util.LinkedList;

public class CreateMedia {

    public static Context context = null;

    public static final ArrayList<MyMedia> allvideoList = new ArrayList<>();
    public static final ArrayList<MyMedia> allmusicList = new ArrayList<>();
    public static final ArrayList<MyMedia> allimageList = new ArrayList<>();
    public static final ArrayList<MyMedia>[] allList = new ArrayList[]{allvideoList,allmusicList,allimageList};

    public static final LinkedList<MyMedia> videoList = new LinkedList<>();
    public static final LinkedList<MyMedia> musicList = new LinkedList<>();
    public static final LinkedList<MyMedia> imageList = new LinkedList<>();
    public static final LinkedList<MyMedia>[] lists = new LinkedList[]{videoList, musicList, imageList};


    public static final LinkedList<MyMedia> listed = new LinkedList<>();

    public static final LinkedList<MyMedia> recordList = new LinkedList<>();

    private static final String[] intentPutName = {"a","b","c"};

    private CreateMedia(){}

    public static void createMusicList(Context context) {

        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        String coverpath = null;
        musicList.clear();
        allmusicList.clear();
        if (cursor.moveToFirst()) {
            do {
                MyMedia music = new MyMedia();
                music.setType(MyMedia.MEDIA_MUSIC);
                music.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                if( music.getName().equals("") ){
                    music.setName("音乐");
                }
                music.setDataPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                int albumId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                //根据专辑ID获取到专辑封面图
                coverpath = getAlbumArt(albumId,context);
                if( context.equals("") ){
                    music.setCoverPath("android.resource://com.example.qimozuoye/"+R.drawable.music);
                } else{
                    music.setCoverPath(coverpath);
                }
                musicList.add(music);
                allmusicList.add(music);
            } while (cursor.moveToNext());
        }
        cursor.close();

    }
    public static void createVideoList(Context context) {

        ContentResolver resolver = context.getContentResolver();
        String[] thumbColumns = {MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Thumbnails.VIDEO_ID};
        String[] mediaColumns = {MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,MediaStore.Video.Media.TITLE};
        Cursor cursor = resolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                mediaColumns, null, null, null);
        videoList.clear();
        allvideoList.clear();
        if (cursor.moveToFirst()) {
            do {
                MyMedia video = new MyMedia();
                video.setDataPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media
                        .DATA)));
                video.setType(MyMedia.MEDIA_VIDEO);
                video.setName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)));
                if( video.getName().equals("") ){
                    video.setName("视频");
                }
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                Cursor thumbCursor = resolver.query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                        thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID
                                + "=" + id, null, null);
                if (thumbCursor.moveToFirst()) {
                    video.setCoverPath(thumbCursor.getString(thumbCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA)));
                }
                videoList.add(video);
                allvideoList.add(video);
            } while (cursor.moveToNext());
        }
    }
    public static void createImageList(Context context) {

        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        imageList.clear();
        allimageList.clear();
        if( cursor.moveToFirst() ) {
            do {
                MyMedia image = new MyMedia();
                image.setType(MyMedia.MEDIA_IMAGE);
                image.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE)));
                if (image.getName().equals("")) {
                    image.setName("图片");
                }
                image.setDataPath(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                image.setCoverPath(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                imageList.add(image);
                allimageList.add(image);
            } while (cursor.moveToNext());
        }
        cursor.close();

    }

    private static String getAlbumArt(int album_id, Context context) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = context.getContentResolver().query(Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)), projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        return album_art;
    }
    public static Bitmap getVideoThumbnail(String filePath) {
        Bitmap b=null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            b=retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();

        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return b;
    }



    public static int getMedias(Context context,int n){
        for( int i = 0; i < n; i++ ){
            MyMedia media = null;
            int k = 0;
            boolean[] flag = {false,false,false};
            while( media == null ){
                k = (int)(Math.random()*3);
                LinkedList<MyMedia> list = lists[k];
                if( list.size()!=0 ) {
                    int l = (int)(Math.random()*list.size());
                    media = list.remove(l);
                    listed.addFirst(media);
                } else {
                    flag[k] = true;
                }
                if( flag[0] && flag[1] && flag[2] ){
                    return i;
                }
            }
        }
//        for( int i = 0; i < 3; i++ ){
//            Intent intent = new Intent(context, ProvideDataService.class);
//            if( lists[i].size() < 10 ){
//                intent.putExtra(intentPutName[i],10);
//            }
//            context.startService(intent);
//        }
        return 6;
    }

}
