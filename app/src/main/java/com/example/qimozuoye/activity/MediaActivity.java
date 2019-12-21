package com.example.qimozuoye.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qimozuoye.R;
import com.example.qimozuoye.adapter.MediaAdapter;
import com.example.qimozuoye.bean.MyMedia;
import com.example.qimozuoye.collector.ActivityCollector;
import com.example.qimozuoye.util.CreateMedia;
import com.example.qimozuoye.util.User;
import com.google.android.material.navigation.NavigationView;

import java.util.LinkedList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MediaActivity extends AppCompatActivity {

    private LinkedList<MyMedia> myMediaList;
    private MediaAdapter adapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private DrawerLayout mDrawerLayout;

    private Context mcontext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local);
        ActivityCollector.addActivity(this);
        mcontext = this;
        myMediaList = CreateMedia.recordList;

        Toolbar toolbar = (Toolbar) findViewById(R.id.local_toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        View header = navView.inflateHeaderView(R.layout.nav_header);
        CircleImageView circleImageView = (CircleImageView)header.findViewById(R.id.icon_image);
        Bitmap bm = BitmapFactory.decodeFile(User.getImagePath());
        circleImageView.setImageBitmap(bm);
        TextView textView = (TextView) header.findViewById(R.id.icon_username);
        textView.setText(User.getName());
        ActionBar actionBar = getSupportActionBar();
        if( actionBar != null ){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        navView.setCheckedItem(R.id.nav_playback_record);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:{
                        Intent intent = new Intent(mcontext, LocalActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case R.id.nav_playback_record:{
                        mDrawerLayout.closeDrawers();
                        break;
                    }
                    default:{}
                }
                return true;
            }
        });


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MediaAdapter(myMediaList);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

    }

    private void refresh(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myMediaList = CreateMedia.recordList;
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu){
//        getMenuInflater().inflate(R.menu.delete,menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            }
//            case R.menu.delete:{
//                for( int i = 0; i < CreateMedia.recordList.size(); i++ ){
//                    CreateMedia.recordList.remove();
//                }
//                adapter.notifyDataSetChanged();
//                break;
//            }
            default:{}
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

}
