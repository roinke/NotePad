package net.roink.notepad;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import net.roink.notepad.db.Note;

import java.util.ArrayList;
import java.util.List;

public class GridAdapter extends BaseAdapter implements Filterable {

    private Context mContext;

    private List<Note> backList;//用来备份原始数据
    private List<Note> noteList;//这个数据是会改变的，所以要有个变量来备份一下原始数据
    private MyFilter mFilter;
    Boolean isShowCheckBox =false;


    void setShowCheckBox(Boolean showCheckBox) {
        isShowCheckBox = showCheckBox;
    }

    GridAdapter(Context mContext, List<Note> noteList) {
        this.mContext = mContext;
        this.noteList = noteList;
        backList = noteList;
    }


    @Override
    public int getCount() {
        return noteList.size();
    }

    @Override
    public Object getItem(int position) {
        return noteList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    static class ViewHolder {
        CheckBox checkBox;
        TextView tv_content,tv_time;
    }
    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        mContext.setTheme(R.style.DayTheme);
        ViewHolder holder;
        if(convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(R.layout.note_layout, null);
            holder=new ViewHolder();
            holder.checkBox= convertView.findViewById(R.id.checkBox);
            holder.tv_content=convertView.findViewById(R.id.tv_content);
            holder.tv_time=convertView.findViewById(R.id.tv_time);
            convertView.setTag(holder);
        }
        else{
            holder= (ViewHolder) convertView.getTag();
        }
        //设置文字
        final Note note = noteList.get(position);
        if(note!=null){

            holder.tv_content.setText(note.getContent());
            holder.tv_time.setText(noteList.get(position).getTime());

            if(isShowCheckBox){
                holder.checkBox.setVisibility(View.VISIBLE);
            }else {
                holder.checkBox.setVisibility(View.GONE);
            }
            holder.checkBox.setChecked(note.getTag() != 0);

            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(note.getTag()==0){
                        note.setTag(1);
                    }else {
                        note.setTag(0);
                    }
                }
            });
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (mFilter ==null){
            mFilter = new MyFilter();
        }
        return mFilter;
    }

    class MyFilter extends Filter {
        //我们在performFiltering(CharSequence charSequence)这个方法中定义过滤规则
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults result = new FilterResults();
            List<Note> list;
            if (TextUtils.isEmpty(charSequence)) {//当过滤的关键字为空的时候，我们则显示所有的数据
                list = backList;
            } else {//否则把符合条件的数据对象添加到集合中
                list = new ArrayList<>();
                for (Note note : backList) {
                    if (note.getContent().contains(charSequence)) {
                        list.add(note);
                    }
                }
            }
            result.values = list; //将得到的集合保存到FilterResults的value变量中
            result.count = list.size();//将集合的大小保存到FilterResults的count变量中
            return result;
        }

        //在publishResults方法中告诉适配器更新界面
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            noteList = (List<Note>)filterResults.values;
            if (filterResults.count>0){
                notifyDataSetChanged();//通知数据发生了改变
            }else {
                notifyDataSetInvalidated();//通知数据失效
            }
        }
    }
}
