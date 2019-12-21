package com.example.qimozuoye.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.qimozuoye.R;
import com.example.qimozuoye.collector.ActivityCollector;
import com.example.qimozuoye.util.User;

import java.util.Map;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private CheckBox rememberPass;

    private EditText accountEdit;
    private EditText passwordEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        ActivityCollector.addActivity(this);


        pref = PreferenceManager.getDefaultSharedPreferences(this);

        accountEdit = (EditText) findViewById(R.id.login_account);
        passwordEdit = (EditText) findViewById(R.id.login_password);

        rememberPass = (CheckBox) findViewById(R.id.remember_pass);
        boolean isRemember = pref.getBoolean("remember_password",false);
        if( isRemember ){
            String account = pref.getString("username","");
            String password = pref.getString("password","");
            accountEdit.setText(account);
            passwordEdit.setText(password);
            rememberPass.setChecked(true);
        }
        Button button1 = (Button) findViewById(R.id.regicter_button);
        Button button2 = (Button) findViewById(R.id.login_button);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_button:{
                String account = accountEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                int n = pref.getInt("user_count",0);
                for( int i = 0; i < n; i++ ){
                    if( account.equals(pref.getString("username_" + i,"")) ){
                        if( password.equals(pref.getString("password_" + i,"")) ){
                            editor = pref.edit();
                            editor.putString("username",account);
                            editor.putString("password",password);
                            editor.putBoolean("remember_password",true);
                            editor.apply();
                            User.setName(account);
                            User.setImagePath(pref.getString("user_image_" + i,""));
                            Intent intent = new Intent(this, LocalActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "密码错误",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            }
            case R.id.regicter_button:{
                Intent intent = new Intent(this, RegicterActivity.class);
                startActivity(intent);
                break;
            }
            default:{}
        }
    }
}
