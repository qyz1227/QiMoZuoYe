package com.example.qimozuoye.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.qimozuoye.R;
import com.example.qimozuoye.bean.MyMedia;
import com.example.qimozuoye.collector.ActivityCollector;
import com.example.qimozuoye.util.CreateMedia;

import java.io.File;

public class VideoActivity extends AppCompatActivity {

    int media_number;
    private VideoView videoView;
    private MyMedia media;
    private MediaController mediaController;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ActivityCollector.addActivity(this);

        //结束其他声音
        Intent intentservice = new Intent("com.example.qimozuoye.Media");
        sendBroadcast(intentservice);

        gestureDetector = new GestureDetector(onGestureListener);

        Intent intent = getIntent();
        media_number = (int) intent.getIntExtra("media_number",0);

        videoView = (VideoView) findViewById(R.id.video_layout);

        mediaController = new MediaController(this);

        videoView.setMediaController(mediaController);
        videoView.requestFocus();


        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                media_number++;
                media_number%=(CreateMedia.allvideoList.size()-1);
                media = CreateMedia.allvideoList.get(media_number);
                initVideoPath();
            }
        });

        if (ContextCompat.checkSelfPermission(VideoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(VideoActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        } else {
            initVideoPath();
        }

    }

    private void initVideoPath(){

        media = CreateMedia.allvideoList.get(media_number);
        CreateMedia.recordList.addFirst(media);
        videoView.setVideoPath(media.getDataPath());
        videoView.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case 1:{
                if( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                    initVideoPath();
                } else {
                    Toast.makeText(this,"Your permission request is denied",Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            }
            default:{
                break;
            }
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(videoView != null){
            videoView.suspend();
        }
        ActivityCollector.removeActivity(this);
    }

    //当Activity被触摸时回调
    public boolean onTouchEvent(MotionEvent event){
        return gestureDetector.onTouchEvent(event);
    }
    //自定义GestureDetector的手势识别监听器
    private GestureDetector.OnGestureListener onGestureListener
            = new GestureDetector.SimpleOnGestureListener(){
        //当识别的手势是滑动手势时回调onFinger方法
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
            //得到手触碰位置的起始点和结束点坐标 x , y ，并进行计算
            float x = e2.getX()-e1.getX();
            float y = e2.getY()-e1.getY();
            //通过计算判断是向左还是向右滑动
            if(y > 0){
                media_number++;
                media_number%=(CreateMedia.allvideoList.size()-1);
            }else if(y < 0){
                media_number--;
                media_number=(media_number+(CreateMedia.allvideoList.size()-1))%(CreateMedia.allvideoList.size()-1);
            }
            initVideoPath();
            return true;
        }
    };

}
