package net.roink.notepad.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import net.roink.notepad.utils.Constants;

import java.util.ArrayList;
import java.util.List;


public class HandleDataBase {

    SQLiteOpenHelper dbHelper;

    private static final String[] columns = {
            Constants.ID,
            Constants.CONTENT,
            Constants.TIME
    };

    public HandleDataBase(Context context){
        dbHelper = new DBHelper(context);
    }

    public void close(){
        dbHelper.close();
    }

    //把note 加入到database里面
    public Note addNote(Note note){
        //add a note object to database
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.CONTENT, note.getContent());
        contentValues.put(Constants.TIME, note.getTime());
        long insertId = writableDatabase.insert(Constants.TABLE_NAME, null, contentValues);
        note.setId(insertId);
        writableDatabase.close();
        return note;
    }

    public Note getNote(long id){
        //get a note from database using cursor index
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        Cursor cursor = writableDatabase.query(Constants.TABLE_NAME,columns, Constants.ID + "=?",
                new String[]{String.valueOf(id)},null,null, null, null);
        if (cursor != null) cursor.moveToFirst();
        Note e = new Note(cursor.getString(1),cursor.getString(2),0);
        cursor.close();
        writableDatabase.close();
        return e;
    }

    public List<Note> getAllNotes(){

        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        Cursor cursor = writableDatabase.query(Constants.TABLE_NAME,columns,null,null,null, null, null);

        List<Note> notes = new ArrayList<>();
        if(cursor.getCount() > 0){
            while(cursor.moveToNext()){
                Note note = new Note();
                note.setId(cursor.getLong(cursor.getColumnIndex(Constants.ID)));
                note.setContent(cursor.getString(cursor.getColumnIndex(Constants.CONTENT)));
                note.setTime(cursor.getString(cursor.getColumnIndex(Constants.TIME)));
                note.setTag(0);
                notes.add(note);
            }
        }
        cursor.close();
        writableDatabase.close();
        return notes;
    }

    public int updateNote(Note note) {
        //更新数据库的信系
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.CONTENT, note.getContent());
        values.put(Constants.TIME, note.getTime());

        int update = writableDatabase.update(Constants.TABLE_NAME, values,
                Constants.ID + "=?", new String[]{String.valueOf(note.getId())});
        writableDatabase.close();
        return update;
    }


    public void removeNote(Note note) {
        //remove a note according to ID value
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        writableDatabase.delete(Constants.TABLE_NAME, Constants.ID+ "=" + note.getId(), null);
        writableDatabase.close();
    }


}
