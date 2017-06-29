package com.yunshipei.signin;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ubuntu on 16-8-1.
 */
public class OutActivity extends Activity {

    private EditText mEditText;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out);

        mEditText = (EditText)findViewById(R.id.dates);
        findViewById(R.id.datepicker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mycalendar = Calendar.getInstance(Locale.CHINA);
                Date mydate = new Date(); //获取当前日期Date对象
                mycalendar.setTime(mydate);////为Calendar对象设置时间为当前日期

                int year = mycalendar.get(Calendar.YEAR); //获取Calendar对象中的年
                int month = mycalendar.get(Calendar.MONTH);//获取Calendar对象中的月
                final int day = mycalendar.get(Calendar.DAY_OF_MONTH);//获取Calendar对象中的月
                DatePickerDialog dialog = new DatePickerDialog(OutActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        date = String.format("%s-%s-%s", year, monthOfYear, dayOfMonth);
                    }
                }, year, month, day);
                dialog.show();
            }
        });

        findViewById(R.id.export).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                doWrite("0");
                doWrite("1");
            }
        });

        findViewById(R.id.export_to_file).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String value = doWrite("0");
                try{
                    String fileName = Environment.getExternalStorageDirectory() + "/" + date + "-0.txt";
                    File outFile = new File(fileName);
                    if (outFile.exists()) {
                        outFile.delete();
                    }
                    FileOutputStream fout = new FileOutputStream(outFile);
                    byte [] bytes = value.getBytes();

                    fout.write(bytes);
                    fout.close();
                    Toast.makeText(OutActivity.this, "写文件" + fileName + "成功", Toast.LENGTH_SHORT).show();
                } catch(Exception e){
                    e.printStackTrace();
                }

                value = doWrite("1");
                try{
                    String fileName = Environment.getExternalStorageDirectory() + "/" + date + "-1.txt";
                    File outFile = new File(fileName);
                    if (outFile.exists()) {
                        outFile.delete();
                    }
                    FileOutputStream fout = new FileOutputStream(outFile);
                    byte [] bytes = value.getBytes();

                    fout.write(bytes);
                    fout.close();
                    Toast.makeText(OutActivity.this, "写文件" + fileName + "成功", Toast.LENGTH_SHORT).show();
                } catch(Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    private String doWrite(String type) {
        Cursor cursor = MainActivity.db.getAllContacts(date, type);

        String out = "";
        String lateOut = "迟到：\n";
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                if (cursor.getInt(4) == 1) {
                    out += cursor.getString(0) + "\n";
                } else if (cursor.getInt(4) == 2) {
                    lateOut += cursor.getString(0) + "\n";
                }

            } while (cursor.moveToNext());

            out += lateOut;
            mEditText.setText(out);
        }

        return out;
    }
}
