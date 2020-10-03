package net.roink.notepad;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupWindow;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.roink.notepad.db.DBHelper;
import net.roink.notepad.db.HandleDataBase;
import net.roink.notepad.db.Note;
import net.roink.notepad.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener {

    private DBHelper dbHelper=null;

    private Context context = this;
    //final String TAG = "tag";

    FloatingActionButton floatingActionButton;

    private GridView gridView;
    private GridAdapter adapter;
    private List<Note> noteList = new ArrayList<>();
    private Toolbar myToolbar;
    private PopupWindow mPop;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        adapter=new GridAdapter(context,noteList);

        refreshListView();
        gridView.setAdapter(adapter);

        setSupportActionBar(myToolbar);//设置toolbar取代actionbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        gridView.setOnItemClickListener(this);
        gridView.setOnItemLongClickListener(this);

        //悬浮按钮设置事件
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("mode", 4);
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.in_righttoleft,R.anim.out_righttoleft);
            }
        });

    }

    //初始化视图
    public void initView(){
        floatingActionButton = findViewById(R.id.fab);
        gridView = findViewById(R.id.lv);
        myToolbar = findViewById(R.id.myToolbar);
    }


    //显示PopupWindows
    public void showPopUpView(){

        @SuppressLint("InflateParams") View view=getLayoutInflater().inflate(R.layout.layout_pop,null,false);
        final PopupWindow mPop =new PopupWindow(view,ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);

        //设置PopupWindow之外可以点击
        mPop.setOutsideTouchable(true);
        mPop.setFocusable(true);
        //设置出位置
        mPop.showAsDropDown(myToolbar,700,30);

        View view1 = view.findViewById(R.id.del_All);
        View view2 = view.findViewById(R.id.bulk_Del);

        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除全部实现
                mPop.dismiss();//让下拉栏消失
                DelAllNotes();
            }
        });

        view2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPop.dismiss();//让下拉栏消失
                startActivity(new Intent(MainActivity.this,EventActivity.class));
            }
        });
    }

    //删除全部项
    private void DelAllNotes(){
        new AlertDialog.Builder(MainActivity.this)
                .setMessage("确定删除全部吗？")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper = new DBHelper(MainActivity.this);
                        SQLiteDatabase db = MainActivity.this.dbHelper.getWritableDatabase();
                        db.delete(Constants.TABLE_NAME, null, null);
                        db.execSQL("update sqlite_sequence set seq=0 where name='Notes'");
                        db.close();
                        dbHelper.close();
                        refreshListView();
                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }


    // 接受startActivityForResult的结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        int returnMode;
        long note_Id;
        returnMode = Objects.requireNonNull(data.getExtras()).getInt("mode", -1);
        note_Id = data.getExtras().getLong("id", 0);

        //如果返回的mode是1，需要更新视图
        if (returnMode == 1) {

            String content = data.getExtras().getString("content");
            String time = data.getExtras().getString("time");
            int tag = data.getExtras().getInt("tag", 0);

            Note newNote = new Note(content, time, tag);
            newNote.setId(note_Id);
            HandleDataBase op = new HandleDataBase(context);
            op.updateNote(newNote);
            op.close();

        } else if (returnMode == 0) {  // 如果返回mode是0，新建一条记录并更新视图
            String content = data.getExtras().getString("content");
            String time = data.getExtras().getString("time");
            int tag = data.getExtras().getInt("tag", 0);

            Note newNote = new Note(content, time, tag);
            HandleDataBase op = new HandleDataBase(context);
            op.addNote(newNote);
            op.close();
        } else if (returnMode == 2) { // 如果返回mode是2，删除指定记录并更新视图
            Note curNote = new Note();
            curNote.setId(note_Id);
            HandleDataBase op = new HandleDataBase(context);
            op.removeNote(curNote);
            op.close();
        }
        //更新显示视图
        refreshListView();
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        //搜索设置
        MenuItem mSearch = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) mSearch.getActionView();

        //设置整个搜索框界面都是可点击的
        mSearchView.setIconifiedByDefault(false);
        //设置占位内容
        mSearchView.setQueryHint("请输入搜索内容");

        //设置输入内容监听
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }

    //当ToolBar中的Item被点击时，执行相关操作
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.menu_clear) {
            showPopUpView();
        }
        return super.onOptionsItemSelected(item);
    }

    //刷新主页面视图
    public void refreshListView(){

        HandleDataBase handleDataBase = new HandleDataBase(context);
        if (noteList.size() > 0) {
            noteList.clear();
        }
        noteList.addAll(handleDataBase.getAllNotes());
        handleDataBase.close();
        adapter.notifyDataSetChanged();
    }

    //当GridView中的Item被点击是执行操作
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.lv) {
            Note curNote = (Note) parent.getItemAtPosition(position);
            Intent intent = new Intent(MainActivity.this, EditActivity.class);
            intent.putExtra("content", curNote.getContent());
            intent.putExtra("id", curNote.getId());
            intent.putExtra("time", curNote.getTime());
            intent.putExtra("mode", 3);     // 设置mode为3，说明要编辑记录
            intent.putExtra("tag", curNote.getTag());
            startActivityForResult(intent, 1);
            overridePendingTransition(R.anim.in_righttoleft, R.anim.out_righttoleft);
            //Log.d(TAG, "onItemClick: " + position);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

        adapter.setShowCheckBox(true);
        adapter.notifyDataSetChanged();
        floatingActionButton.setVisibility(View.GONE);

        @SuppressLint("InflateParams") View view1=getLayoutInflater().inflate(R.layout.select_layout,null,false);
        View cancel_opt = view1.findViewById(R.id.cancel_opt);
        cancel_opt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOption();
            }
        });

        final View sel_all_opt = view1.findViewById(R.id.sel_all_opt);
        sel_all_opt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectAllList();
            }
        });

        View unSel_all_opt = view1.findViewById(R.id.unsel_all_opt);
        unSel_all_opt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unSelectAllList();
            }
        });

        View del_sel_opt = view1.findViewById(R.id.del_sel_opt);
        del_sel_opt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DelSelect();
            }
        });

        mPop = new PopupWindow(view1,ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);

        //设置PopupWindow之外可以点击
        mPop.setOutsideTouchable(false);
        mPop.setFocusable(false);
        //设置出位置
        // 在底部显示
        mPop.showAtLocation(parent,Gravity.BOTTOM, 0, 0);

        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        mPop.setBackgroundDrawable(dw);

        // 设置popWindow的显示和消失动画
        mPop.setAnimationStyle(R.style.my_popWindow_anim_style);
        return true;
    }

    /**
     * 全选
     */
    public void SelectAllList() {
        if (adapter.isShowCheckBox) {
            for (Note note : noteList) {
                note.setTag(1);
                //ids.add(note.getId());
            }
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 全不选
     */
    public void unSelectAllList() {
        if (adapter.isShowCheckBox) {
            for (Note note : noteList) {
                note.setTag(0);
                //ids.clear();
            }
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 取消操作
     */
    public void cancelOption() {
        mPop.dismiss();
        floatingActionButton.setVisibility(View.VISIBLE);
        adapter.setShowCheckBox(false);
        adapter.notifyDataSetChanged();
    }

    /**
     * 删除选中
     */
    public void DelSelect() {

        dbHelper=new DBHelper(context);
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        for (Note note : noteList) {
            if(note.getTag()==1){
                writableDatabase.delete(Constants.TABLE_NAME, Constants.ID + "=" + note.getId() , null);
            }
        }
        /*for (Long id : ids) {
            writableDatabase.delete(Constants.TABLE_NAME, Constants.ID + "=" + id , null);
        }*/
        writableDatabase.close();
        dbHelper.close();
        //ids.clear();

        refreshListView();
    }
}
