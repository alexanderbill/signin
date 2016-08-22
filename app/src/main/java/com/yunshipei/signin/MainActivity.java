package com.yunshipei.signin;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    public static DBAdapter db;
    private SharedPreferences sp;

    public MainActivity() {
        super();
        db = new DBAdapter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PinyinHelper.getInstance().init(this);
        db.open();
    }

    @Override
    public void onResume() {
        super.onResume();
        findViewById(R.id.total).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, TotalActivity.class);
                startActivity(it);
            }
        });
        findViewById(R.id.thisin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, InActivity.class);
                startActivity(it);
            }
        });
        findViewById(R.id.thissign).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, SignActivity.class);
                startActivity(it);
            }
        });
        findViewById(R.id.thisout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, OutActivity.class);
                startActivity(it);
            }
        });
        findViewById(R.id.query).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, QueryActivity.class);
                startActivity(it);
            }
        });
    }

    @Override
    public void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
