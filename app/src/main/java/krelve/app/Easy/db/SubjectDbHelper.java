package krelve.app.Easy.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Me on 2016/2/28.
 */


public class SubjectDbHelper extends SQLiteOpenHelper {
    public static final String CREATE_TABLE = "create table course ("
            + "id integer primary key autoincrement, "
            + "classRoom text, "
            + "className text,"
            + "classProperty text, "
            + "classTeacher text, "
            + "classCredit text, "
            + "days int, "
            + "start int, "
            + "step int, "
            + "classWeek text, "
            + "startWeek int, "
            + "endWeek int)";





    public SubjectDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE); // 创建表
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
