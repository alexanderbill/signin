package com.yunshipei.signin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by ubuntu on 16-8-1.
 */
public class TotalActivity extends AppCompatActivity {
    private final int FILE_SELECT_CODE = 10;
    private EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total);
        et = (EditText)findViewById(R.id.edit_total);
        findViewById(R.id.check).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = et.getText().toString();
                String[] lines = text.split("\n");
                for (int i = 0; i < lines.length; i++) {
                    String line = lines[i];
                    String[] records = line.split("\\s+");
                    if (records.length > 3
                            && PinyinHelper.getInstance().isHanzi(records[0])
                            && (records[1].compareTo("男") == 0 || records[1].compareTo("女") == 0)
                            && records[0].length() <= 4 && records[0].length() > 1
                            && PinyinHelper.getInstance().isPhone(records[2])) {

                    } else {
                        findViewById(R.id.totalin).setEnabled(false);
                        findViewById(R.id.partin).setEnabled(false);
                        Toast.makeText(TotalActivity.this, line + "格式错误", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Toast.makeText(TotalActivity.this, "格式校验正确", Toast.LENGTH_SHORT).show();
                findViewById(R.id.totalin).setEnabled(true);
                findViewById(R.id.partin).setEnabled(true);
            }
        });

        findViewById(R.id.openfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        findViewById(R.id.totalin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = MainActivity.db.getAllContacts("ALL");
                if (cursor.getCount() != 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TotalActivity.this);
                    builder.setMessage("总表已经录入，确定覆盖吗？");
                    builder.setTitle("提示");
                    builder.setPositiveButton("覆盖", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.db.deleteContact("ALL");
                            doInsert();
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                    return;
                }
                doInsert();
            }
        });

        findViewById(R.id.partin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doInsert();
            }
        });

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                findViewById(R.id.totalin).setEnabled(false);
                findViewById(R.id.partin).setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void doInsert() {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        int id = radioGroup.getCheckedRadioButtonId();
        String type = "0";
        if (id != R.id.radioBZ) {
            type = "1";
        }
        String text = et.getText().toString();
        String[] lines = text.split("\n");

        Cursor cursor = MainActivity.db.getAllContacts("ALL");
        ArrayList<String> arrayList = new ArrayList<String>();
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                arrayList.add(cursor.getString(0) + cursor.getString(1) + cursor.getString(2));
            } while (cursor.moveToNext());
        }
        String remains = "";
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String[] records = line.split("\\s+");
            if (!arrayList.contains(records[0] + records[1] + records[2])) {
                MainActivity.db.insertContact(records[0], records[1], records[2], records[3], type, "ALL");
            } else {
                remains += line + " 记录重复\n";
            }
        }
        et.setText(remains);
        Toast.makeText(this, "录入成功, " + (TextUtils.isEmpty(remains) ? "0" : remains.split("\n").length) + "条记录重复" + (TextUtils.isEmpty(remains) ? "" : "，请检查"), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    String path = FileUtils.getPath(this, uri);
                    Log.d("leizhou", path);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.ms-excel");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult( Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.",  Toast.LENGTH_SHORT).show();
        }
    }
}
