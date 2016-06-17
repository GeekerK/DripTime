package com.geekerk.driptime;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.geekerk.driptime.db.DataBaseHelper;
import com.geekerk.driptime.db.EventDao;
import com.geekerk.driptime.utils.LayoutUtil;
import com.geekerk.driptime.vo.EventBean;
import com.geekerk.driptime.vo.ListBean;
import com.geekerk.driptime.vo.UserBean;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 新建事件，修改事件 的页面
 * Created by Administrator on 2016/6/16.
 */
public class AddItemActivity extends AppCompatActivity {
    private static final String TAG = "AddItemActivity";
    private ImageView priorityCommon_iv, prioritySecond_iv, priorityFirst_iv, lastPriority_iv;
    private TextView startTime_tv, dealLine_tv;
    private EditText title_ed;
    private EventBean eventBean;
    private DataBaseHelper mDataBaseHelper;
    private boolean isUpdate = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additem);
        final TextInputLayout textInputLayout = (TextInputLayout) findViewById(R.id.content_inputlayout);
        title_ed = textInputLayout.getEditText();
        title_ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    textInputLayout.setError("Content can not be empty");
                    textInputLayout.setErrorEnabled(true);
                } else {
                    textInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        priorityCommon_iv = (ImageView) findViewById(R.id.priority_level_0);
        priorityFirst_iv = (ImageView) findViewById(R.id.priority_level_1);
        prioritySecond_iv = (ImageView) findViewById(R.id.priority_level_2);
        startTime_tv = (TextView) findViewById(R.id.startTime);
        dealLine_tv = (TextView) findViewById(R.id.Deadline);

        lastPriority_iv = priorityCommon_iv;

        eventBean = (EventBean) getIntent().getSerializableExtra("modifyEvent");
        if (eventBean == null) {            //新建事件
            eventBean = new EventBean();
            //设置事件的当前用户
            eventBean.setUser(new UserBean(getSharedPreferences("user", MODE_PRIVATE).getInt("currentUserID", -1)));
            //如果有清单，设置清单
            int listId = getIntent().getIntExtra("ListId", -1);
            if(listId != -1)
                eventBean.setList(new ListBean(listId));
            //设置默认的优先级
            eventBean.setPriority(EventBean.Priority.NORMAL_LEVEL);
            //默认设置
            eventBean.setFinished(false);
        } else {    //修改事件
            isUpdate = true;
            title_ed.setText(eventBean.getTitle());
            switch (eventBean.getPriority()) {
                case FIRST_LEVEL:
                    updatePriorityView(priorityFirst_iv);
                    break;
                case SECOND_LEVEL:
                    updatePriorityView(prioritySecond_iv);
                    break;
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            startTime_tv.setText(dateFormat.format(eventBean.getReleaseTime()));
            Date deadline = eventBean.getDeadline();
            if (deadline != null)
                dealLine_tv.setText(dateFormat.format(deadline));
        }

        mDataBaseHelper = OpenHelperManager.getHelper(this, DataBaseHelper.class);
    }


    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.cancel_action :   //取消
                setResult(Activity.RESULT_CANCELED);
                finish();
                break;
            case R.id.done_action :     //完成
                //检查title,起始时间是否为空
                String title = title_ed.getText().toString();
                if (TextUtils.isEmpty(title)) {
                    Toast.makeText(this, "Content "+getResources().getString(R.string.inputIsEmpty), Toast.LENGTH_SHORT).show();
                    return;
                } else if (eventBean.getReleaseTime() == null){
                    Toast.makeText(this, "StartTime "+getResources().getString(R.string.inputIsEmpty), Toast.LENGTH_SHORT).show();
                    return;
                }
                //更新数据
                eventBean.setTitle(title);
                //写入数据库
                try {
                    EventDao eventdao = new EventDao(mDataBaseHelper.getEventDao());
                    if (!isUpdate) {
                        eventdao.create(eventBean);
                    } else {
                        eventdao.update(eventBean);
                    }
                    setResult(Activity.RESULT_OK);
                    finish();
                } catch (SQLException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "添加数据库失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.priority_level_0 :    //设置普通优先级
                //设置优先级
                eventBean.setPriority(EventBean.Priority.NORMAL_LEVEL);
                //更新页面
                updatePriorityView(priorityCommon_iv);
                break;
            case R.id.priority_level_2 :    //设置2级优先级
                eventBean.setPriority(EventBean.Priority.SECOND_LEVEL);
                updatePriorityView(prioritySecond_iv);
                break;
            case R.id.priority_level_1 :    //设置1级优先级
                eventBean.setPriority(EventBean.Priority.FIRST_LEVEL);
                updatePriorityView(priorityFirst_iv);
                break;
            case R.id.startTime_bt :    //设置开始时间
                showCalendarDialog(true);
                break;
            case R.id.deadline_bt :     //设置截止时间
                showCalendarDialog(false);
                break;
        }
    }

    private void showCalendarDialog(boolean isSetStartTime) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_setdate, null);
        final CalendarView calendarView = (CalendarView) view.findViewById(R.id.calendar);
        final Calendar calendar = Calendar.getInstance();
        if (isSetStartTime) {
            Date currentReleaseTime = eventBean.getReleaseTime();
            if (currentReleaseTime != null) {
                calendar.setTime(currentReleaseTime);
                calendarView.setDate(currentReleaseTime.getTime());
            }
        } else {
            Date currentDeadline = eventBean.getDeadline();
            if (currentDeadline != null) {
                calendar.setTime(currentDeadline);
                calendarView.setDate(currentDeadline.getTime());
            }
        }
        final int[] time = {calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)};
        TextView setTime = (TextView) view.findViewById(R.id.setTime);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                time[2] = year;
                time[3] = month;
                time[4] = dayOfMonth;
            }
        });
        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(AddItemActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        time[0] = hourOfDay;
                        time[1] = minute;
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
            }
        });
        if (isSetStartTime) {
            builder.setTitle(getResources().getString(R.string.setStartTime)).setView(view).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    calendar.set(time[2], time[3], time[4], time[0], time[1]);
                    eventBean.setReleaseTime(calendar.getTime());
                    startTime_tv.setText(dateFormat.format(calendar.getTime()));
                    dialog.dismiss();
                }
            }).show();
        }else {
            builder.setTitle(getResources().getString(R.string.setDeadline)).setView(view).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(time[2], time[3], time[4], time[0], time[1]);
                    eventBean.setDeadline(calendar.getTime());
                    dealLine_tv.setText(dateFormat.format(calendar.getTime()));
                    dialog.dismiss();
                }
            }).show();
        }
    }

    private void updatePriorityView(ImageView currentPriority) {
        if (lastPriority_iv != currentPriority) {
            lastPriority_iv.setImageResource(android.R.color.transparent);
            lastPriority_iv.setElevation(LayoutUtil.getPixelByDIP(this, 4));
            currentPriority.setImageResource(R.mipmap.priority_check);
            currentPriority.setElevation(LayoutUtil.getPixelByDIP(this, 1));
            lastPriority_iv = currentPriority;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OpenHelperManager.releaseHelper();
        mDataBaseHelper = null;
    }
}
