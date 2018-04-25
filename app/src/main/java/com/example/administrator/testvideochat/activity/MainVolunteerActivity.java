package com.example.administrator.testvideochat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.example.administrator.testvideochat.R;

public class MainVolunteerActivity extends AppCompatActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_volunteer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        //如果需要给toolbar设置事件监听，需要将toolbar设置支持actionbar
        setSupportActionBar(toolbar);

        //toolbar的menu点击事件的监听
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(MainVolunteerActivity.this, Settings.class));
                return true;
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //加载menu文件到布局
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

}
