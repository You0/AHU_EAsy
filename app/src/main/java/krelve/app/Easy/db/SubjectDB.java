package krelve.app.Easy.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import krelve.app.Easy.bean.Course;

/**
 * Created by Me on 2016/2/28.
 */
public class SubjectDB {
    /**
     * 数据库名
     */
    public static final String DB_NAME = "cool_weather";
    /**
     * 数据库版本
     */
    public static final int VERSION = 1;
    private static SubjectDB subjectDB;
    private SQLiteDatabase db;


    /**
     * 将构造方法私有化
     */
    private SubjectDB(Context context) {
        SubjectDbHelper dbHelper = new SubjectDbHelper(context,
                DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();

    }

    public synchronized static SubjectDB getInstance(Context context) {
        if (subjectDB == null) {
            subjectDB = new SubjectDB(context);
        }
        return subjectDB;
    }

    public void saveCourse(Course course) {
        if (course != null) {
            ContentValues values = new ContentValues();
            values.put("classRoom", course.classRoom);
            values.put("className", course.className);
            values.put("classProperty", course.classProperty);
            values.put("classTeacher", course.classTeacher);
            values.put("days", course.days);
            values.put("start", course.start);
            values.put("step", course.step);
            values.put("classWeek", course.classWeek);
            values.put("startWeek", course.startWeek);
            values.put("endWeek", course.endWeek);
            db.insert("course", null, values);
        }
    }



    public ArrayList<Course> LoadCourse() {
        ArrayList<Course> list = new ArrayList<Course>();
        Cursor cursor = db.query("course", null, null,
                null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Course course = new Course();
                course.classRoom = cursor.getString(cursor
                        .getColumnIndex("classRoom"));
                course.classCredit=cursor.getString(cursor
                        .getColumnIndex("classCredit"));
                course.className=cursor.getString(cursor
                        .getColumnIndex("className"));
                course.classTeacher=cursor.getString(cursor
                        .getColumnIndex("classTeacher"));
                course.classProperty=cursor.getString(cursor
                        .getColumnIndex("classProperty"));
                course.classWeek=cursor.getString(cursor
                        .getColumnIndex("classWeek"));
                course.startWeek=cursor.getInt(cursor
                        .getColumnIndex("startWeek"));
                course.endWeek=cursor.getInt(cursor
                        .getColumnIndex("endWeek"));
                course.days=cursor.getInt(cursor
                        .getColumnIndex("days"));
                course.start=cursor.getInt(cursor
                        .getColumnIndex("start"));
                course.step=cursor.getInt(cursor
                        .getColumnIndex("step"));
                list.add(course);
            } while (cursor.moveToNext());
        }
        return list;
    }
}
