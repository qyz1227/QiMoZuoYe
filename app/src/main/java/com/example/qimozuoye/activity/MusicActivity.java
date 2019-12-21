package com.example.qimozuoye.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qimozuoye.R;
import com.example.qimozuoye.bean.MyMedia;
import com.example.qimozuoye.collector.ActivityCollector;
import com.example.qimozuoye.service.MusicService;
import com.example.qimozuoye.util.CreateMedia;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MusicActivity extends AppCompatActivity {

    int media_number;

    ImageView imageView;
    TextView textView;
    SeekBar seekBar;
    TextView musicCur,musicLength;
    SimpleDateFormat format;
    Timer timer;
    int currentPosition = 0;
    private boolean isSeekBarChanging;//互斥变量，防止进度条与定时器冲突

    MyMedia media;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        ActivityCollector.addActivity(this);

        mediaPlayer = new MediaPlayer();
        //结束其他声音
        Intent intentservice = new Intent("com.example.qimozuoye.Media");
        sendBroadcast(intentservice);

        Intent intent = getIntent();
        media_number = intent.getIntExtra("media_number",0);
        currentPosition = intent.getIntExtra("currentPosition",0);

        initMusic();

        if (ContextCompat.checkSelfPermission(MusicActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MusicActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        } else {
            refreshMusic();
        }

    }

    public void initMusic(){


        imageView = (ImageView) findViewById(R.id.music_layout_image);
        textView = (TextView) findViewById(R.id.music_layout_text);
        seekBar = (SeekBar) findViewById(R.id.music_layout_seekbar);
        musicCur = (TextView) findViewById(R.id.music_layout_cur);
        musicLength = (TextView) findViewById(R.id.music_layout_length);
        format = new SimpleDateFormat("mm:ss");

        final ImageButton play = (ImageButton) findViewById(R.id.music_layout_play);
        final ImageButton next= (ImageButton) findViewById(R.id.music_layout_next);
        final ImageButton previous = (ImageButton) findViewById(R.id.music_layout_previous);
        final ImageButton forward = (ImageButton) findViewById(R.id.music_layout_forward);
        final ImageButton backward = (ImageButton) findViewById(R.id.music_layout_backward);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( !mediaPlayer.isPlaying() ) {
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.start();//开始播放
                    timer = new Timer();
                    timer.schedule(new TimerTask() {

                        Runnable updateUI = new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    musicCur.setText(format.format(mediaPlayer.getCurrentPosition()) + "");
                                } catch (NullPointerException e){

                                }
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
                } else {
                    mediaPlayer.pause();
                    play.setImageResource(R.drawable.play);
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                media_number++;
                media_number%=CreateMedia.allmusicList.size();
                refreshMusic();
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                media_number--;
                media_number=(media_number+CreateMedia.allmusicList.size())%CreateMedia.allmusicList.size();
                refreshMusic();
            }
        });
        backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.getCurrentPosition() - mediaPlayer.getDuration()/30 - 1 > 0 ){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - mediaPlayer.getDuration()/30 - 1);
                } else {
                    mediaPlayer.seekTo(0);
                }
            }
        });
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.getCurrentPosition() + mediaPlayer.getDuration()/30 + 1 < mediaPlayer.getDuration()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + mediaPlayer.getDuration()/30 + 1);
                } else {
                    mediaPlayer.seekTo(mediaPlayer.getDuration()-1);
                }
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                media_number++;
                media_number%=CreateMedia.allmusicList.size();
                refreshMusic();
            }
        });

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                seekBar.setMax(mediaPlayer.getDuration());
                musicLength.setText(format.format(mediaPlayer.getDuration()) + "");
                musicCur.setText("00:00");
            }
        });

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return true;
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekBarChanging = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeekBarChanging = false;
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

    }


    public void refreshMusic(){
        try {
            media = CreateMedia.allmusicList.get(media_number);
            CreateMedia.recordList.addFirst(media);
            textView.setText(media.getName());
            Bitmap bm = BitmapFactory.decodeFile(media.getCoverPath());
            if( bm != null ){
                imageView.setImageBitmap(bm);
            }
            mediaPlayer.reset();
            mediaPlayer.setDataSource(media.getDataPath());
            mediaPlayer.prepare();
            timer = new Timer();
            mediaPlayer.seekTo(currentPosition);
            currentPosition = 0;
            timer.schedule(new TimerTask() {
                Runnable updateUI = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            musicCur.setText(format.format(mediaPlayer.getCurrentPosition()) + "");
                        } catch (NullPointerException e){
                            musicCur.setText("00:00");
                        }
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
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:{
                if( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                    refreshMusic();
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
    protected void onDestroy() {
        super.onDestroy();
        isSeekBarChanging = true;

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (mediaPlayer != null) {
            if( mediaPlayer.isPlaying() ){
                mediaPlayer.stop();
                Intent intent = new Intent(this, MusicService.class);
                intent.putExtra("media_number",media_number);
                intent.putExtra("currentPosition",mediaPlayer.getCurrentPosition());
                startService(intent);
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        ActivityCollector.removeActivity(this);
    }
}
