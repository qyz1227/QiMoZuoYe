package com.example.qimozuoye.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import com.example.qimozuoye.R;
import com.example.qimozuoye.SQL.DataBaseManger;
import com.example.qimozuoye.SQL.MyDatabaseHelper;
import com.example.qimozuoye.collector.ActivityCollector;
import com.example.qimozuoye.service.ProvideDataService;
import com.example.qimozuoye.util.CreateMedia;

public class CoverActivity extends AppCompatActivity {

    VideoView videoView;
    Context mcontext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover);
        ActivityCollector.addActivity(this);

        mcontext = this;
        CreateMedia.context = mcontext;
        DataBaseManger.dbHelper = new MyDatabaseHelper(this,"Medias.db",null,1);
        videoView = (VideoView) findViewById(R.id.cover_video);
        videoView.setVideoPath("android.resource://com.example.qimozuoye/"+R.raw.cover2);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Intent intent = new Intent(mcontext, LogInActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Button button = (Button) findViewById(R.id.cover_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcontext, LogInActivity.class);
                startActivity(intent);
                finish();
            }
        });
        init();

    }


    private void init(){

        if (ContextCompat.checkSelfPermission(CoverActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(CoverActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }else{
            videoView.start();
            Intent intent = new Intent(this, ProvideDataService.class);
            intent.putExtra("a",10000);
            intent.putExtra("b",10000);
            intent.putExtra("c",10000);
            startService(intent);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case 1:{
                if( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                    videoView.start();
                    Intent intent = new Intent(this, ProvideDataService.class);
                    intent.putExtra("a",50);
                    intent.putExtra("b",150);
                    intent.putExtra("c",100);
                    startService(intent);
                } else {
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
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

}
