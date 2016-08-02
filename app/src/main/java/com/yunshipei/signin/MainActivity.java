package com.yunshipei.signin;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static DBAdapter db;

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

        Cursor cursor = MainActivity.db.getAllContacts("ALL");
        Toast.makeText(this, "总表有" + String.valueOf(cursor.getCount()) + "条记录", Toast.LENGTH_SHORT).show();
        if (cursor.getCount() != 0) {
            findViewById(R.id.thisin).setEnabled(true);
            findViewById(R.id.thissign).setEnabled(true);
            findViewById(R.id.thisout).setEnabled(true);
            findViewById(R.id.query).setEnabled(true);
        } else {
            findViewById(R.id.thisin).setEnabled(false);
            findViewById(R.id.thissign).setEnabled(false);
            findViewById(R.id.thisout).setEnabled(false);
            findViewById(R.id.query).setEnabled(false);
        }
    }

    @Override
    public void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
