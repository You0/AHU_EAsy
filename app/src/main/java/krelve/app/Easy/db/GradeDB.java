package krelve.app.Easy.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import krelve.app.Easy.bean.Course;
import krelve.app.Easy.bean.GradeCourse;

/**
 * Created by Me on 2016/2/28.
 */
public class GradeDB {
    /**
     * 数据库名
     */
    public static final String DB_NAME = "term";
    /**
     * 数据库版本
     */
    public static final int VERSION = 1;
    private static GradeDB gradeDB;
    private SQLiteDatabase db;


    /**
     * 将构造方法私有化
     */
    private GradeDB(Context context) {
        GradeDbHelper dbHelper = new GradeDbHelper(context,
                DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();

    }

    public  static GradeDB getInstance(Context context) {
//        if (gradeDB == null) {
//            gradeDB = new GradeDB(context);
//        }
        gradeDB = new GradeDB(context);
        return gradeDB;
    }

    public void saveCourse(GradeCourse course,int tag) {
        if (course != null) {
            ContentValues values = new ContentValues();
            System.out.println("tag"+tag);
            values.put("TAG",tag);
            values.put("classYear", course.classYear);
            values.put("classTerm", course.classTerm);
            values.put("className", course.className);
            values.put("classProperty", course.classProperty);
            values.put("classCredit", course.classCredit);
            values.put("classGPA", course.classGPA);
            values.put("classGrade", course.classGrade);
            values.put("classDepartment", course.classDepartment);
            db.insert("grade", null, values);
        }
    }



    public ArrayList<ArrayList<GradeCourse> > LoadCourse() {
        ArrayList<ArrayList<GradeCourse> >list = new ArrayList<ArrayList<GradeCourse> >();
        ArrayList<GradeCourse> inner = new ArrayList<>();
        Cursor cursor = db.query("grade", null, null,
                null, null, null, null);
        int pretag;
        int tag=0;
        if (cursor.moveToFirst()) {
            do {
                GradeCourse course = new GradeCourse();
                pretag = tag;
                tag = cursor.getInt(cursor
                        .getColumnIndex("TAG"));
                course.classYear = cursor.getString(cursor
                        .getColumnIndex("classRoom"));
                course.classTerm=cursor.getInt(cursor
                        .getColumnIndex("classTerm"));
                course.className=cursor.getString(cursor
                        .getColumnIndex("className"));
                course.classProperty=cursor.getString(cursor
                        .getColumnIndex("classProperty"));
                course.classCredit=cursor.getDouble(cursor
                        .getColumnIndex("classCredit"));
                course.classGPA=cursor.getDouble(cursor
                        .getColumnIndex("classGPA"));
                course.classGrade=cursor.getString(cursor
                        .getColumnIndex("classGrade"));
                course.classDepartment=cursor.getString(cursor
                        .getColumnIndex("classDepartment"));

                if(pretag == tag)
                {
                    inner.add(course);
                }else{
                    list.add(inner);
                    inner = new ArrayList<>();
                    inner.add(course);
                }

            } while (cursor.moveToNext());
            list.add(inner);
        }
        return list;
    }
}
