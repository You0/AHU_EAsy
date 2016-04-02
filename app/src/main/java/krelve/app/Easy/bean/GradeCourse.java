package krelve.app.Easy.bean;

import java.io.Serializable;

/**
 * Created by Me on 2016/3/18 0018.
 */
public class GradeCourse implements Serializable {
    public String classYear;
    public int classTerm;
    public String className;
    public String classProperty;
    public double classCredit;
    public double classGPA;
    public String classGrade;
    public String classDepartment;



    public GradeCourse() {}


    public String getClassYear() {
        return classYear;
    }

    public int getClassTerm() {
        return classTerm;
    }

    public String getClassName() {
        return className;
    }

    public String getClassProperty() {
        return classProperty;
    }

    public double getClassCredit() {
        return classCredit;
    }

    public double getClassGPA() {
        return classGPA;
    }

    public String getClassGrade() {
        return classGrade;
    }

    public String getClassDepartment() {
        return classDepartment;
    }

    void Printf(){
        System.out.println("学年 :" + classYear);
        System.out.println("学期 :" + classTerm);
        System.out.println("课程名称 :" + className);
        System.out.println("课程性质 :" + classProperty);
        System.out.println("课程学分 :" + classCredit);
        System.out.println("课程绩点 :" + classGPA);
        System.out.println("课程得分 :" + classGrade);
        System.out.println("开课院系 :" +classDepartment + "\n");

    }

}
