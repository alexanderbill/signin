package com.yunshipei.signin;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ubuntu on 16-8-1.
 */
public class InActivity extends Activity {
    private final int FILE_SELECT_CODE = 10;
    private String date = "";
    private EditText et;
    private String mBanzhang;
    private String mYigong;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //persmission method.
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in);

        verifyStoragePermissions(this);

        findViewById(R.id.datepicker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mycalendar = Calendar.getInstance(Locale.CHINA);
                Date mydate = new Date(); //获取当前日期Date对象
                mycalendar.setTime(mydate);////为Calendar对象设置时间为当前日期

                int year = mycalendar.get(Calendar.YEAR); //获取Calendar对象中的年
                int month = mycalendar.get(Calendar.MONTH);//获取Calendar对象中的月
                final int day = mycalendar.get(Calendar.DAY_OF_MONTH);//获取Calendar对象中的月
                DatePickerDialog dialog = new DatePickerDialog(InActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        date = String.format("%s-%s-%s", year, monthOfYear, dayOfMonth);
                        findViewById(R.id.check).setEnabled(true);
                        findViewById(R.id.openfile).setEnabled(true);
                    }
                }, year, month, day);
                dialog.show();
            }
        });

        findViewById(R.id.openfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // showFileChooser();
                doRead(Environment.getExternalStorageDirectory() + "/bz.xls");
            }
        });

        et = (EditText)findViewById(R.id.edit_total);
        findViewById(R.id.check).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mBanzhang) && TextUtils.isEmpty(mYigong)) {
                    doCheck(et.getText().toString());
                } else {
                    doCheck(et.getText().toString());
                    //doCheck(mYigong);
                }
            }
        });

        findViewById(R.id.totalin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = MainActivity.db.getAllContacts(date);
                if (cursor.getCount() != 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(InActivity.this);
                    builder.setMessage("本次报名信息已经录入，确定覆盖吗？");
                    builder.setTitle("提示");
                    builder.setPositiveButton("覆盖", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.db.deleteContact(date);
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
                if (!TextUtils.isEmpty(et.getText().toString())) {
                    findViewById(R.id.check).setEnabled(true);
                } else {
                    findViewById(R.id.check).setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void doCheck(String text) {
        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (TextUtils.isEmpty(line)) {
                continue;
            }
            String[] records = line.split("\\s+");
            if (records.length > 0
                    && PinyinHelper.getInstance().isHanzi(records[0])
                    //&& (records[1].compareTo("男") == 0 || records[1].compareTo("女") == 0)
                    && records[0].length() <= 4 && records[0].length() > 1) {
                //&& PinyinHelper.getInstance().isPhone(records[2])) {

            } else {
                findViewById(R.id.totalin).setEnabled(false);
                findViewById(R.id.partin).setEnabled(false);
                Toast.makeText(InActivity.this, line + "格式错误", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Toast.makeText(InActivity.this, "格式校验正确", Toast.LENGTH_SHORT).show();
        findViewById(R.id.totalin).setEnabled(true);
        findViewById(R.id.partin).setEnabled(true);
    }

    private void doInsert() {
        // mBanzhang = et.getText().toString();
              if (TextUtils.isEmpty(mBanzhang) && TextUtils.isEmpty(mYigong)) {
            doInsert(et.getText().toString(), "1");
        } else {
            // doInsert(mBanzhang, "0");
            doInsert(mYigong, "1");
        }
        Toast.makeText(InActivity.this, "记录插入成功", Toast.LENGTH_SHORT).show();
    }
    private void doInsert(String text, String type) {
        String[] lines = text.split("\n");

        Cursor cursor = MainActivity.db.getAllContacts(date);
        ArrayList<String> arrayList = new ArrayList<String>();
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                arrayList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        Cursor cursor1 = MainActivity.db.getAllContacts("ALL");
        ArrayList<String> arrayList1 = new ArrayList<String>();
        if (cursor1.getCount() != 0) {
            cursor1.moveToFirst();
            do {
                arrayList1.add(cursor1.getString(0).trim());
            } while (cursor1.moveToNext());
        }

        String remains = "";
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String[] records = line.split(" ");
            String name;
            String sex = "";
            String phone = "";
            String depart = "";
            if (records.length > 3) {
                depart = records[3];
            }
            if (records.length > 2) {
                phone = records[2];
            }
            if (records.length > 1) {
                sex = records[1];
            }
            name = records[0];
            if (arrayList.contains(records[0] + sex + phone)) {
                remains += line + "\t已经报名\n";
            //} else if (!arrayList1.contains(records[0])) {
            //    remains += line + "\t没有上课资格\n";
            } else {
                MainActivity.db.insertContact(records[0], sex, phone, depart, type, date);
            }
        }
        et.setText(remains);
        Toast.makeText(this, "录入成功, " + (TextUtils.isEmpty(remains) ? "0" : remains.split("\n").length) + "条记录有问题" + (TextUtils.isEmpty(remains) ? "" : "，请检查"), Toast.LENGTH_SHORT).show();
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
                    doRead(path);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void doRead(String path) {
        // mBanzhang = doRead(path, 1);
        mYigong = doRead(path, 0);
        et.setText(mYigong);
        Toast.makeText(InActivity.this, "文件读取成功", Toast.LENGTH_SHORT).show();
    }

    private String doRead(String path, int type) {

        try {
            String result = "";
            HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(path));
            org.apache.poi.ss.usermodel.Sheet sheet1 = wb.getSheetAt(type);
            for (Row row : sheet1) {
                int i = 0;
                String r1 = "";
                for (Cell cell : row) {
                    if (i > 3) {
                        break;
                    }

                    CellReference cellRef = new CellReference(row.getRowNum(), cell.getColumnIndex());

                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            r1 += " " + cell.getRichStringCellValue().getString();
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell)) {
                                r1 += " " + String.valueOf(cell.getDateCellValue());
                            } else {
                                double phone = cell.getNumericCellValue();
                                r1 += " " + new DecimalFormat("###").format(phone);
                            }
                            break;
                        default:
                            System.out.println();
                    }
                    i++;
                }
                if (!TextUtils.isEmpty(r1)) {
                    result += r1.substring(1) + "\n";
                }
            }
            return result;
        } catch (Exception e) {
            Log.d("err", e.toString());
        }
        return "";
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult( Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.",  Toast.LENGTH_SHORT).show();
        }
    }
}
