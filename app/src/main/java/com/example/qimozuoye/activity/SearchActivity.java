package com.example.qimozuoye.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.qimozuoye.R;
import com.example.qimozuoye.SQL.DataBaseManger;
import com.example.qimozuoye.adapter.MediaAdapter;
import com.example.qimozuoye.bean.MyMedia;
import com.example.qimozuoye.collector.ActivityCollector;
import com.example.qimozuoye.util.CreateMedia;

import java.util.LinkedList;

import static android.widget.Toast.LENGTH_SHORT;

public class SearchActivity extends AppCompatActivity {

    private LinkedList<MyMedia> myMedias = new LinkedList<>();
    private MediaAdapter adapter;
    private Context mcontext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ActivityCollector.addActivity(this);
        mcontext = this;
        SearchView searchView = (SearchView) findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                myMedias.clear();
                for( int n = 0; n < CreateMedia.allList.length; n++ ){
                    for( int i = 0; i < CreateMedia.allList[n].size(); i++ ){
                        if( CreateMedia.allList[n].get(i).getName().indexOf(query) != -1 ){
                            myMedias.add(CreateMedia.allList[n].get(i));
                        }

                    }
                }
                adapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.search_recyclerview);
        GridLayoutManager layoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MediaAdapter(myMedias);
        recyclerView.setAdapter(adapter);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

}
