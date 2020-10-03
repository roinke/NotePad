package net.roink.notepad.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.roink.notepad.utils.Constants;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context){
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ Constants.TABLE_NAME
                + "("
                + Constants.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Constants.CONTENT + " TEXT NOT NULL,"
                + Constants.TIME + " TEXT NOT NULL)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //数据库更新操作
    }
}
