package net.roink.notepad;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import net.roink.notepad.utils.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class EditActivity extends BaseActivity {

    private TextView mTvSelectedTime;
    private static final int PERMISSION_RESULT_CODE = 1;
    private EditText editText;
    private String old_content = "";

    private long id = 0;
    private int openMode = 0;
    public Intent intent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_layout);

        Toolbar myToolbar = findViewById(R.id.my_Toolbar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        //设置不显示标题
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //设置toolbar取代actionbar

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoSetMessage();
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.in_lefttoright,R.anim.out_lefttoright);
            }
        });

        editText = findViewById(R.id.et);
        Intent getIntent = getIntent();

        openMode = getIntent.getIntExtra("mode", 0);

        //如果openMode是3 表示要编辑记录，将记录查询展示到页面
        if (openMode == 3) {
            id = getIntent.getLongExtra("id", 0);
            old_content = getIntent.getStringExtra("content");
            String old_time = getIntent.getStringExtra("time");
            int old_Tag = getIntent.getIntExtra("tag", 0);
            editText.setText(old_content);
            editText.setSelection(old_content.length());
        }
    }

    //toolBar的布局设置
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.delete:
                new AlertDialog.Builder(EditActivity.this)
                        .setMessage("确定删除吗？")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (openMode == 4){
                                    intent.putExtra("mode", -1);
                                    setResult(RESULT_OK, intent);
                                }
                                else {
                                    intent.putExtra("mode", 2);
                                    intent.putExtra("id", id);
                                    setResult(RESULT_OK, intent);
                                }
                                finish();
                                overridePendingTransition(R.anim.in_lefttoright,R.anim.out_lefttoright);
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                    }
                }).create().show();
                break;

            case R.id.event:
                //ToastUtil.showShortToast(EditActivity.this,"被点击了");
                checkCalendarPermission();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkCalendarPermission() {
        int writeResult = checkSelfPermission(Manifest.permission.WRITE_CALENDAR);
        int readResult = checkSelfPermission(Manifest.permission.READ_CALENDAR);
        if(writeResult != PackageManager.PERMISSION_GRANTED || readResult != PackageManager.PERMISSION_GRANTED) {
            //没有权限,需要申请权限
            //一般先提示用户，如果用户点击了确定，才去请求权限
            //TODO:同学们就展示一个新的界面，然后如果用户点击不再显示，则不要再显示请求权限了，也不要给用户使用程序，因为没意义呀。
            //TODO:这里我就直接上检查权限的代码，在视频里我们会按实际项目流程走，把提示也做出来。
            //请求权限
            requestPermissions(new String[]{Manifest.permission.WRITE_CALENDAR,Manifest.permission.READ_CALENDAR},PERMISSION_RESULT_CODE);
        } else {
            //有权限
            startActivity(new Intent(EditActivity.this,EventActivity.class));
            //showAlertDialog();
        }
    }

   /* private void initTimerPicker() {
        String beginTime = "2020-06-228 00:00";
        String endTime = DateFormatUtils.long2Str(System.currentTimeMillis(), true);

        mTvSelectedTime.setText(endTime);

        // 通过日期字符串初始化日期，格式请用：yyyy-MM-dd HH:mm
        mTimerPicker = new CustomDatePicker(this, new CustomDatePicker.Callback() {
            @Override
            public void onTimeSelected(long timestamp) {
                mTvSelectedTime.setText(DateFormatUtils.long2Str(timestamp, true));
            }
        }, beginTime, endTime);
        // 允许点击屏幕或物理返回键关闭
        mTimerPicker.setCancelable(true);
        // 显示时和分
        mTimerPicker.setCanShowPreciseTime(true);
        // 允许循环滚动
        mTimerPicker.setScrollLoop(true);
        // 允许滚动动画
        mTimerPicker.setCanShowAnim(true);
    }
    private void showAlertDialog() {
        View loginForm = LayoutInflater.from(EditActivity.this)
                .inflate(R.layout.alert_event_layout, null);
        EditText et_eventTitle=loginForm.findViewById(R.id.et_event_Title);
        EditText et_event_Desc=loginForm.findViewById(R.id.et_event_Desc);
        View LL_1 = loginForm.findViewById(R.id.LL_1);

        LL_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initTimerPicker();
            }
        });
        View LL_2 = loginForm.findViewById(R.id.LL_2);
        LL_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initTimerPicker();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
        builder.setTitle("插入事件")
                .setView(loginForm)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", null)
                .show();
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //权限请求结果
        if(requestCode == PERMISSION_RESULT_CODE) {
            // permissions，这个就是前面我们传的数组，请求权限的数组
            // grantResults 结果
            if(grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //有权限，不用做操作
                //showAlertDialog();
                startActivity(new Intent(EditActivity.this,EventActivity.class));
            } else {
                // 无权限，结束程序
                ToastUtil.showShortToast(EditActivity.this,"此功能需要访问日历");
            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event){
        //当按下Home键，随系统
        if (keyCode == KeyEvent.KEYCODE_HOME){
            return true;
        }
        //按下返回键
        else if (keyCode == KeyEvent.KEYCODE_BACK){
            autoSetMessage();
            setResult(RESULT_OK, intent);
            finish();
            overridePendingTransition(R.anim.in_lefttoright,R.anim.out_lefttoright);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //保存操作
    public void autoSetMessage(){
        //openMode是4 表示新建
        if(openMode == 4){
            if(editText.getText().toString().length() == 0){
                //返回-1表示什么都没干，不用保存
                intent.putExtra("mode", -1);
            }
            else{
                //返回0表示新建了一条记录，将相关数据保存到intent中
                intent.putExtra("mode", 0);
                intent.putExtra("content", editText.getText().toString());
                intent.putExtra("time", dateToStr());
                intent.putExtra("tag", 0);
            }
        }
        //openMode!=4,表示打开留的记录
        else {
            //保存时判断内容是否更改
            //如果内容未更改，返回mode=-1，表示什么都没做
            if (editText.getText().toString().equals(old_content))
                intent.putExtra("mode", -1);
            else {
                //如果内容已经更改，返回mode=1，表示内容已经已经更改，需要更新视图
                intent.putExtra("mode", 1); //edit the content
                intent.putExtra("content", editText.getText().toString());
                intent.putExtra("time", dateToStr());
                intent.putExtra("id", id);
                intent.putExtra("tag", 0);
            }
        }
    }

    //以指定格式获得当前时间
    public String dateToStr(){
        Date date = new Date();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }


}
