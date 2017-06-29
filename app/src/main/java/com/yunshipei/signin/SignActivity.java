package com.yunshipei.signin;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ubuntu on 16-8-1.
 */
public class SignActivity extends Activity {
    private String date = "";
    private EditText et;
    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        et = (EditText)findViewById(R.id.edit_total);
        findViewById(R.id.datepicker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mycalendar = Calendar.getInstance(Locale.CHINA);
                Date mydate = new Date(); //获取当前日期Date对象
                mycalendar.setTime(mydate);////为Calendar对象设置时间为当前日期

                int year = mycalendar.get(Calendar.YEAR); //获取Calendar对象中的年
                int month = mycalendar.get(Calendar.MONTH);//获取Calendar对象中的月
                final int day = mycalendar.get(Calendar.DAY_OF_MONTH);//获取Calendar对象中的月
                DatePickerDialog dialog = new DatePickerDialog(SignActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        date = String.format("%s-%s-%s", year, monthOfYear, dayOfMonth);
                        RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
                        int id = radioGroup.getCheckedRadioButtonId();
                        Cursor cursor = MainActivity.db.getAllContacts(date, "1");

                        ListView listView = (ListView) SignActivity.this.findViewById(R.id.list);
                        findViewById(R.id.datepicker).setVisibility(View.GONE);
                        radioGroup.setVisibility(View.GONE);
                        adapter = new UserAdapter(SignActivity.this, R.layout.list_item);
                        int signed = 0;
                        int man = 0;
                        if (cursor.getCount() != 0) {
                            cursor.moveToFirst();
                            do {
                                if (cursor.getInt(4) == 1) {
                                    signed++;
                                    if (cursor.getString(1).equals("男")) {
                                        man++;
                                    }
                                }
                                adapter.add(new User(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getString(5), cursor.getInt(6)));
                            } while (cursor.moveToNext());
                            adapter.sort();
                        }
                        Toast.makeText(SignActivity.this, "本次报名表有" + String.valueOf(cursor.getCount()) + "条记录," + signed + "已签到" + man + "男", Toast.LENGTH_SHORT).show();
                        listView.setAdapter(adapter);
                    }
                }, year, month, day);
                dialog.show();
            }
        });



        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(date)) {
                    adapter.search(s.toString().toLowerCase());
                } else {
                    Toast.makeText(SignActivity.this, "请先选择签到日期", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    class User {
        public final String mPhone;
        public final String mName;
        public final String mSex;
        public final String mDepartment;
        public int mChecked;
        public final String mNick;
        public final int mRowId;

        public User(String name, String sex, String phone, String department, int checked, String nick, int rowId) {
            this.mPhone = phone;
            this.mName = name;
            this.mSex = sex;
            this.mDepartment = department;
            mChecked = checked;
            mNick = nick;
            mRowId = rowId;
        }

        public String getName() {
            return this.mName;
        }

        public String getPhone() {
            return this.mPhone;
        }

        public String getSex() {
            return this.mSex;
        }

        public String getDepartment() {
            return this.mDepartment;
        }
    }

    class SortByAge implements Comparator {
        public int compare(Object o1, Object o2) {
            User s1 = (User) o1;
            User s2 = (User) o2;
            return s1.mNick.compareTo(s2.mNick);
        }
    }

    class UserAdapter extends ArrayAdapter<User> {
        private int mResourceId;

        private ArrayList<User> sorted;

        private ArrayList<User> searched;

        public UserAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
            this.mResourceId = textViewResourceId;
            sorted = new ArrayList<User>();
            searched = new ArrayList<User>();
        }

        @Override
        public void add(User user) {
            sorted.add(user);
        }

        public void sort() {
            Collections.sort(sorted, new SortByAge());
            searched.addAll(sorted);
        }

        public void search(String key) {
            if (TextUtils.isEmpty(key)) {
                searched.clear();
                searched.addAll(sorted);
            } else {
                searched.clear();
                for (int i = 0; i < sorted.size(); i++) {
                    if (sorted.get(i).mNick.startsWith(key)) {
                        searched.add(sorted.get(i));
                    }
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public User getItem(int position) {
            return searched.get(position);
        }

        @Override
        public int getCount() {
            return searched.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final User user = getItem(position);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(mResourceId, null);
            TextView nameText = (TextView) view.findViewById(R.id.name);
            TextView phoneText = (TextView) view.findViewById(R.id.phone);
            TextView sexText = (TextView) view.findViewById(R.id.sex);
            TextView depText = (TextView) view.findViewById(R.id.department);
            final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbok);
            final CheckBox lateCheckBox = (CheckBox) view.findViewById(R.id.late_checkbok);

            lateCheckBox.setEnabled(false);
            checkBox.setEnabled(false);
            nameText.setText(user.getName());
            phoneText.setText(user.getPhone());
            sexText.setText(user.getSex());
            depText.setText(user.getDepartment());
            if (user.mChecked == 1) {
                checkBox.setChecked(true);
            } else if (user.mChecked == 2) {
                lateCheckBox.setChecked(true);
            } else {
                lateCheckBox.setEnabled(true);
                checkBox.setEnabled(true);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        MainActivity.db.updateContact(user.mRowId, 1);
                        user.mChecked = 1;
                        lateCheckBox.setEnabled(false);
                        checkBox.setEnabled(false);
                    }
                });
                lateCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        MainActivity.db.updateContact(user.mRowId, 2);
                        user.mChecked = 2;
                        lateCheckBox.setEnabled(false);
                        checkBox.setEnabled(false);
                    }
                });
            }
            return view;
        }
    }
}
