package com.example.qimozuoye.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.qimozuoye.R;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

//public class FindActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_find);
//
//
//
//    }
//}
//public class FindActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_find);
//        VideoView videoView = (VideoView)findViewById(R.id.videoView);
//
//        //加载指定的视频文件
//        String path = Environment.getExternalStorageDirectory().getPath()+"/ZZZZ/2.mp4";
//        videoView.setVideoPath(path);
//
//        //创建MediaController对象
//        MediaController mediaController = new MediaController(this);
//
//        //VideoView与MediaController建立关联
//        videoView.setMediaController(mediaController);
//
//        //让VideoView获取焦点
//        videoView.requestFocus();
//
//    }
//}

public class FindActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton play, pause, stop, volume_plus, volume_decrease;
    private TextView musicName, musicLength, musicCur;
    private SeekBar seekBar;

    private MediaPlayer mediaPlayer = new MediaPlayer();

    private AudioManager audioManager;

    private Timer timer;

    int maxVolume, currentVolume;

    private boolean isSeekBarChanging;//互斥变量，防止进度条与定时器冲突。
    private int currentPosition;//当前音乐播放的进度

    SimpleDateFormat format;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        audioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);

        format = new SimpleDateFormat("mm:ss");

        play = (ImageButton) findViewById(R.id.play);
        pause = (ImageButton) findViewById(R.id.pause);
        stop = (ImageButton) findViewById(R.id.stop);
        volume_plus = (ImageButton) findViewById(R.id.volume_plus);
        volume_decrease = (ImageButton) findViewById(R.id.volume_decrease);

        musicName = (TextView) findViewById(R.id.music_name);
        musicLength = (TextView) findViewById(R.id.music_length);
        musicCur = (TextView) findViewById(R.id.music_cur);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new MySeekBar());

        play.setOnClickListener(FindActivity.this);
        pause.setOnClickListener(FindActivity.this);
        stop.setOnClickListener(FindActivity.this);
        volume_plus.setOnClickListener(FindActivity.this);
        volume_decrease.setOnClickListener(FindActivity.this);

        if (ContextCompat.checkSelfPermission(FindActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FindActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            initMediaPlayer();//初始化mediaplayer
        }

    }

    private void initMediaPlayer() {
        try {
            mediaPlayer.setDataSource("/storage/sdcard/kalimba.mp3");//指定音频文件的路径
            mediaPlayer.prepare();//让mediaplayer进入准备状态
            mediaPlayer.setLooping(true);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    seekBar.setMax(mediaPlayer.getDuration());
                    musicLength.setText(format.format(mediaPlayer.getDuration()) + "");
                    musicCur.setText("00:00");
                    musicName.setText("kalimba.mp3");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initMediaPlayer();
                } else {
                    Toast.makeText(FindActivity.this, "denied access", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play:
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();//开始播放
                    mediaPlayer.seekTo(currentPosition);

                    //监听播放时回调函数
                    timer = new Timer();
                    timer.schedule(new TimerTask() {

                        Runnable updateUI = new Runnable() {
                            @Override
                            public void run() {
                                musicCur.setText(format.format(mediaPlayer.getCurrentPosition()) + "");
                            }
                        };

                        @Override
                        public void run() {
                            if (!isSeekBarChanging) {
                                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                                runOnUiThread(updateUI);
                            }
                        }
                    }, 0, 50);
                }
                break;
            case R.id.pause:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();//暂停播放
                }
                break;
            case R.id.stop:
                Toast.makeText(FindActivity.this, "停止播放", Toast.LENGTH_SHORT).show();
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.reset();//停止播放
                    initMediaPlayer();
                }
                break;
            //音量加
            case R.id.volume_plus:
                maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                Toast.makeText(FindActivity.this, "音量增加,最大音量是：" + maxVolume + "，当前音量" + currentVolume,
                        Toast.LENGTH_SHORT).show();
                break;
            //音量减
            case R.id.volume_decrease:
                maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                Toast.makeText(FindActivity.this, "音量减小,最大音量是：" + maxVolume + "，当前音量" + currentVolume,
                        Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isSeekBarChanging = true;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /*进度条处理*/
    public class MySeekBar implements SeekBar.OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
        }

        /*滚动时,应当暂停后台定时器*/
        public void onStartTrackingTouch(SeekBar seekBar) {
            isSeekBarChanging = true;
        }

        /*滑动结束后，重新设置值*/
        public void onStopTrackingTouch(SeekBar seekBar) {
            isSeekBarChanging = false;
            mediaPlayer.seekTo(seekBar.getProgress());
        }
    }

}