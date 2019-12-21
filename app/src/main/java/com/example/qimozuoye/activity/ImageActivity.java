package com.example.qimozuoye.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.qimozuoye.R;
import com.example.qimozuoye.bean.MyMedia;
import com.example.qimozuoye.collector.ActivityCollector;
import com.example.qimozuoye.util.CreateMedia;

import java.io.File;
import java.util.ArrayList;

import static com.example.qimozuoye.R.drawable.cover;
import static com.example.qimozuoye.R.drawable.login;

public class ImageActivity extends AppCompatActivity {

    int media_number;
    private MyMedia media;
    ImageView imageView;

    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ActivityCollector.addActivity(this);

        gestureDetector = new GestureDetector(onGestureListener);
        Intent intent = getIntent();
        media_number = (int) intent.getIntExtra("media_number",0);

        imageView = (ImageView) findViewById(R.id.image_layout);
        setBitmap();

    }

    public void setBitmap(){
        media = CreateMedia.allimageList.get(media_number);
        CreateMedia.recordList.addFirst(media);
        Bitmap bm = BitmapFactory.decodeFile(media.getDataPath());
        imageView.setImageBitmap(bm);
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
            if(x > 0){
                media_number++;
                media_number%=(CreateMedia.allimageList.size()-1);    //想显示多少图片，就把定义图片的数组长度-1
            }else if(x < 0){
                media_number--;
                media_number=(media_number+(CreateMedia.allimageList.size()-1))%(CreateMedia.allimageList.size()-1);
            }
            setBitmap();
            return true;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

}
