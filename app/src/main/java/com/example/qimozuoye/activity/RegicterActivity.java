package com.example.qimozuoye.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qimozuoye.R;
import com.example.qimozuoye.collector.ActivityCollector;
import com.example.qimozuoye.service.ProvideDataService;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegicterActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int CHOOSE_PHOTO = 2;
    String user_image_path = "";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    CircleImageView circleImageView;
    EditText accountEdit,passwordEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regicter);
        ActivityCollector.addActivity(this);

        pref = PreferenceManager.getDefaultSharedPreferences(this);

        Button imageButton = (Button) findViewById(R.id.image_liulan);
        Button regicterButton = (Button) findViewById(R.id.regicter);
        circleImageView = (CircleImageView) findViewById(R.id.regicter_image);
        imageButton.setOnClickListener(this);
        regicterButton.setOnClickListener(this);


        accountEdit = (EditText) findViewById(R.id.regicter_account);
        passwordEdit = (EditText) findViewById(R.id.regicter_password);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.regicter:{
                String account = accountEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                int n = pref.getInt("user_count",0);
                int i;
                for( i = 0; i < n; i++ ){
                    if( account.equals(pref.getString("username_" + i,"")) ){
                        Toast.makeText(this, "昵称已存在",Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                if( i == n ){
                    editor = pref.edit();
                    editor.putInt("user_count",n + 1);
                    editor.putString("username_" + i,account);
                    editor.putString("password_" + i,password);
                    editor.putString("user_image_" + i,user_image_path);
                    editor.apply();
                    Intent intent = new Intent(this,LogInActivity.class);
                    startActivity(intent);
                }
                break;
            }
            case R.id.image_liulan: {
                if(ContextCompat.checkSelfPermission(RegicterActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(RegicterActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                } else {
                    openAlbum();
                }
                break;
            }
            default:{}
        }
    }

    public void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:{
                if( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                    openAlbum();
                } else {
                    Toast.makeText(this, "无法访问相册", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            default:{}
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case CHOOSE_PHOTO:{
                if( resultCode == RESULT_OK ){
                    if( Build.VERSION.SDK_INT >= 19 ){
                        handleImageOnKitKat(data);
                    } else {
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            }
            default:{}
        }

    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if( "com.android.providers.media.documents".equals(uri.getAuthority()) ){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            } else if( "com.android.providers.downloads.documents".equals(uri.getAuthority()) ){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        } else if( "content".equalsIgnoreCase(uri.getScheme()) ){
            imagePath = getImagePath(uri,null);
        } else if( "file".equalsIgnoreCase(uri.getScheme()) ){
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }

    private void handleImageBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection){
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if ( cursor != null ){
            if( cursor.moveToFirst() ){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath){
        if( imagePath != null ){
            user_image_path = imagePath;
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            circleImageView.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "图片获取失败", Toast.LENGTH_SHORT).show();
        }
    }

}
