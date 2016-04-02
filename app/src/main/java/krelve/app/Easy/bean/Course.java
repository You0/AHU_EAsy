package krelve.app.Easy.bean;

import java.io.Serializable;

/**
 * Created by Admin on 2016/2/26.
 */
public class Course implements Serializable {
        public String classRoom;
        public String className;
        public String classProperty;
        public String classTeacher;
        public String classCredit;
        public  int days; //星期几上课
        public  int start, step;  //开始于哪节课，要上几节课
        public String classWeek;
        public  int startWeek, endWeek; //哪周开始哪周结束


        public Course() {}

        public void printf(){
            System.out.println("课程名称 :" + className);
            System.out.println("课程性质 :" + classProperty);
            System.out.println("课程学分 :" + classCredit);
            System.out.println("教学老师 :" +classTeacher);
            System.out.println("上课教室 :" + classRoom);
            System.out.println("星期 :" + days);
            System.out.println("开始于第" + start +"节课");
            System.out.println("要上" + step + "节课");
            System.out.println("上课周期 :" + classWeek);
            System.out.println("开始于第" + startWeek+"星期");
            System.out.println("结束于第" + endWeek + "星期" + "\n");
        }
}
