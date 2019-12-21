package com.example.qimozuoye.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qimozuoye.R;
import com.example.qimozuoye.adapter.MediaAdapter;
import com.example.qimozuoye.bean.MyMedia;
import com.example.qimozuoye.collector.ActivityCollector;
import com.example.qimozuoye.service.ProvideDataService;
import com.example.qimozuoye.util.CreateMedia;
import com.example.qimozuoye.util.User;
import com.google.android.material.navigation.NavigationView;

import java.util.LinkedList;

import de.hdodenhof.circleimageview.CircleImageView;

public class LocalActivity extends AppCompatActivity {


    private LinkedList<MyMedia> myMediaList = new LinkedList<>();
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
        myMediaList = CreateMedia.listed;
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
        navView.setCheckedItem(R.id.nav_home);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:{
                        mDrawerLayout.closeDrawers();
                        break;
                    }
                    case R.id.nav_playback_record:{
                        Intent intent = new Intent(mcontext, MediaActivity.class);
                        startActivity(intent);
                        break;
                    }
                    default:{}
                }
                return true;
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
//        swipeRefreshLayout.setColorSchemeColors(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        if(myMediaList.size() == 0){
            init();
        }
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MediaAdapter(myMediaList);
        recyclerView.setAdapter(adapter);

    }

    private void refresh(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        init();
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    private void init(){
        if( CreateMedia.getMedias(this,6) != 6 ){
            Toast.makeText(this,"本地媒体以空\n刷新将不会再获得媒体",Toast.LENGTH_LONG);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.toolbar_Search:{
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                break;
            }
            case android.R.id.home:{
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            }
            case R.id.toolbar_exit:{
                finish();
                Intent intent = new Intent(this, LogInActivity.class);
                startActivity(intent);
            }
            case R.id.toolbar_get:{
                ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("正在获取本地媒体文件");
                progressDialog.setMessage("获取中...");
                progressDialog.setCancelable(true);
                progressDialog.show();
                User.setProgressDialog(progressDialog);
                Intent intent = new Intent(this, ProvideDataService.class);
                intent.putExtra("a",10000);
                intent.putExtra("b",10000);
                intent.putExtra("c",10000);
                intent.putExtra("e",1);
                startService(intent);
                break;
            }
            default:{}
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
        ActivityCollector.finishAll();
    }


    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
