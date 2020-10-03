package net.roink.notepad;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectChangeListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;

import net.roink.notepad.calendar.CalendarReminderUtils;
import net.roink.notepad.utils.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class EventActivity extends BaseActivity implements View.OnClickListener {

    private TimePickerView pvStartTime;
    private TimePickerView pvEndTime;

    private EditText et_event_Title,et_event_Desc,et_previous;
    private TextView tv_startTime,tv_endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        //初始化时间选择器
        initStartPicker();
        initEndPicker();

        Toolbar myToolbar = findViewById(R.id.my_Toolbar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        //设置不显示标题
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //设置toolbar取代actionbar

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.in_lefttoright,R.anim.out_lefttoright);
            }
        });

        findViewById(R.id.LL_1).setOnClickListener(this);
        findViewById(R.id.LL_2).setOnClickListener(this);
        tv_startTime = findViewById(R.id.tv_startTime);
        tv_startTime.setText(getTime(new Date()));
        tv_endTime = findViewById(R.id.tv_endTime);
        Date now = new Date();
        tv_endTime.setText(getTime(new Date(now.getTime() + 600000)));
        et_event_Title=findViewById(R.id.et_event_Title);
        et_event_Desc=findViewById(R.id.et_event_Desc);
        et_previous=findViewById(R.id.et_previous);

    }

    //toolBar的布局设置
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.bingo:
                if(!TextUtils.isEmpty(et_event_Title.getText())&&!TextUtils.isEmpty(et_event_Desc.getText())&&!TextUtils.isEmpty(et_previous.getText())&&!TextUtils.isEmpty(tv_startTime.getText())&&!TextUtils.isEmpty(tv_endTime.getText())){
                    int previous = Integer.parseInt(et_previous.getText().toString());
                    String title = et_event_Title.getText().toString();
                    String desc = et_event_Desc.getText().toString();
                    long startTime = CalendarReminderUtils.date2TimeStamp(tv_startTime.getText().toString(), "yyyy-MM-dd HH:mm");
                    long endTime = CalendarReminderUtils.date2TimeStamp(tv_endTime.getText().toString(), "yyyy-MM-dd HH:mm");

                    int result = CalendarReminderUtils.addCalendarEvent(EventActivity.this, title, desc, startTime, endTime, previous);
                    if(result==1){
                        ToastUtil.showShortToast(EventActivity.this,"添加事件成功");
                        finish();
                    }else {
                        ToastUtil.showShortToast(EventActivity.this,"添加事件失败，请稍后再试");
                        finish();
                    }

                }else {
                    ToastUtil.showShortToast(EventActivity.this,"请完整输入信息");
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void initStartPicker() {//Dialog 模式下，在底部弹出
        pvStartTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                tv_startTime.setText(getTime(date));
            }
        })
                .setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                    @Override
                    public void onTimeSelectChanged(Date date) {
                    }
                })
                .setCancelText("取消")
                .setSubmitText("确定")
                .setType(new boolean[]{true, true, true, true, true, false})
                .isDialog(true) //默认设置false ，内部实现将DecorView 作为它的父控件。
                .addOnCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                })
                .setItemVisibleCount(10) //若设置偶数，实际值会加1（比如设置6，则最大可见条目为7）
                .setLineSpacingMultiplier(2.0f)
                .isAlphaGradient(true)
                .build();

        Dialog mDialog = pvStartTime.getDialog();
        if (mDialog != null) {

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM);

            params.leftMargin = 0;
            params.rightMargin = 0;
            pvStartTime.getDialogContainerLayout().setLayoutParams(params);

            Window dialogWindow = mDialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim);//修改动画样式
                dialogWindow.setGravity(Gravity.BOTTOM);//改成Bottom,底部显示
                dialogWindow.setDimAmount(0.3f);
            }
        }
    }


    private void initEndPicker() {//Dialog 模式下，在底部弹出
        pvEndTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                tv_endTime.setText(getTime(date));
            }
        })
                .setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                    @Override
                    public void onTimeSelectChanged(Date date) {
                    }
                })
                .setType(new boolean[]{true, true, true, true, true, false})
                .isDialog(true) //默认设置false ，内部实现将DecorView 作为它的父控件。
                .addOnCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                })
                .setCancelText("取消")
                .setSubmitText("确定")
                .setItemVisibleCount(10) //若设置偶数，实际值会加1（比如设置6，则最大可见条目为7）
                .setLineSpacingMultiplier(2.0f)
                .isAlphaGradient(true)
                .build();

        Dialog mDialog = pvEndTime.getDialog();
        if (mDialog != null) {

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM);

            params.leftMargin = 0;
            params.rightMargin = 0;
            pvEndTime.getDialogContainerLayout().setLayoutParams(params);

            Window dialogWindow = mDialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim);//修改动画样式
                dialogWindow.setGravity(Gravity.BOTTOM);//改成Bottom,底部显示
                dialogWindow.setDimAmount(0.3f);
            }
        }
    }


    private String getTime(Date date) {//可根据需要自行截取数据显示
        //Log.d("getTime()", "choice date millis: " + date.getTime());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.LL_1 && pvStartTime != null) {
            pvStartTime.show(v);//弹出时间选择器，传递参数过去，回调的时候则可以绑定此view
        }else if(v.getId() == R.id.LL_2 && pvEndTime != null){
            pvEndTime.show(v);
        }
    }
}
