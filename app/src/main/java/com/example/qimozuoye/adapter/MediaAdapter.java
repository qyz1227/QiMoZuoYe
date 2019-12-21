package com.example.qimozuoye.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.qimozuoye.R;
import com.example.qimozuoye.activity.FindActivity;
import com.example.qimozuoye.activity.ImageActivity;
import com.example.qimozuoye.activity.MusicActivity;
import com.example.qimozuoye.activity.VideoActivity;
import com.example.qimozuoye.bean.MyMedia;
import com.example.qimozuoye.util.CreateMedia;

import java.io.File;
import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {

    private Context mContext;
    private List<MyMedia> myMediaList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView imageView;
        TextView textView;
        View myView;

        public ViewHolder(View view){
            super(view);
            myView = view;
            cardView = (CardView) view;
            imageView = (ImageView) view.findViewById(R.id.item_media_image);
            textView = (TextView) view.findViewById(R.id.item_media_name);
        }
    }

    public MediaAdapter(List<MyMedia> myMedia){
        this.myMediaList = myMedia;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        if( mContext == null ){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.media_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        View.OnClickListener myClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                MyMedia media = myMediaList.get(position);
                switch (media.getType()){
                    case MyMedia.MEDIA_IMAGE:{
                        Intent intent = new Intent(mContext,ImageActivity.class);
                        int media_number = CreateMedia.allimageList.indexOf(media);
                        intent.putExtra("media_number",media_number);
                        mContext.startActivity(intent);
                        break;
                    }
                    case MyMedia.MEDIA_MUSIC:{
                        Intent intent = new Intent(mContext,MusicActivity.class);
                        int media_number = CreateMedia.allmusicList.indexOf(media);
                        intent.putExtra("media_number",media_number);
                        mContext.startActivity(intent);
                        break;
                    }
                    case MyMedia.MEDIA_VIDEO:{
                        Intent intent = new Intent(mContext,VideoActivity.class);
                        int media_number = CreateMedia.allvideoList.indexOf(media);
                        intent.putExtra("media_number",media_number);
                        mContext.startActivity(intent);
                        break;
                    }
                    default:{}
                }
            }
        };
        holder.myView.setOnClickListener(myClick);
        holder.imageView.setOnClickListener(myClick);
        return holder;
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyMedia media = myMediaList.get(position);
        holder.textView.setText(media.getName());
        switch (media.getType()){
            case MyMedia.MEDIA_VIDEO:{
                Bitmap bm = BitmapFactory.decodeFile(media.getCoverPath());
                if( bm == null ){
                    bm = CreateMedia.getVideoThumbnail(media.getDataPath());
                }
                holder.imageView.setImageBitmap(bm);
                break;
            }
            case MyMedia.MEDIA_MUSIC:{
                Bitmap bm = BitmapFactory.decodeFile(media.getCoverPath());
                if( bm == null ){
                    holder.imageView.setImageResource(R.drawable.music);
                } else{
                    holder.imageView.setImageBitmap(bm);
                }
//                Glide.with(mContext).load(media.getCoverPath()).into(holder.imageView);
                break;
            }
            case MyMedia.MEDIA_IMAGE:{
                Glide.with(mContext).load(media.getCoverPath()).into(holder.imageView);
                break;
            }
            default:{}
        }
    }


    @Override
    public int getItemCount() {
        return myMediaList.size();
    }

}
