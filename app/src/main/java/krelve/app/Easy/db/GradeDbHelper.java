package krelve.app.Easy.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Me on 2016/3/18 0018.
 */
public class GradeDbHelper extends SQLiteOpenHelper{

    public static final String CREATE_TABLE = "create table grade("
            + "id integer primary key autoincrement, "
            + "TAG int, "
            + "classRoom text,"
            + "classTerm int, "
            + "className text, "
            +"classYear text,"
            + "classProperty text, "
            + "classCredit double, "
            + "classGPA double, "
            + "classGrade text, "
            + "classDepartment text"
            +")";





    public GradeDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE); // 创建表
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
