package krelve.app.Easy.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import krelve.app.Easy.Kpplication;
import krelve.app.Easy.R;
import krelve.app.Easy.activity.MainActivity;
import krelve.app.Easy.bean.Course;
import krelve.app.Easy.db.SubjectDB;
import krelve.app.Easy.jwxt.JwUtils;
import krelve.app.Easy.mycustom.PickerView;

/**
 * Created by Me on 2016/2/28.
 */
public class ClassTableFragment extends BaseFragment {
    private PickerView pk;
    private int choose;
    private JwUtils jwUtils;
    private RelativeLayout layout;
    private TextView className, classTeacher, classTime, classRoom;
    private TextView text1, days1;
    private Dialog dialog;
    private Dialog wait;
    ArrayList<Course> arrayList;
    SubjectDB subjectDB = null;
    private static String Content;
    private static String NAME;
    private ArrayList<Course> courseList = new ArrayList<>();
    private static int heightPx;
    private static int widthPx;
    private SharedPreferences sharedPreferences;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1: {
                    wait.dismiss();
                    dialog.dismiss();
                    {
                        Document doc = Jsoup.parse(Content);

                        //提取学年和学期
                        Elements semesters = doc.select("option[selected=selected]");
                        String[] years = semesters.get(0).text().split("-");
                        int startYear = Integer.parseInt(years[0]);
                        int endYear = Integer.parseInt(years[1]);
                        int semester = Integer.parseInt(semesters.get(1).text());

                        System.out.println(startYear + "-" + endYear + "学年，第" + semester + "学期");

                        //提取课表,去除无关信息
                        Elements tables = doc.select("table#DBGrid");//table是tables的第一个数据get(0)
                        Element tbody = tables.get(0).child(0);            //tbody是table的第一个子元素child(0)

                        tbody.child(0).remove();

                        //保存课程数据
                        ArrayList<Map<String, String>> classList = new ArrayList<Map<String, String>>();
                        System.out.println("已选课程的数目为 " + tbody.childNodeSize());

                        //分别对每一门课进行处理
                        for (int i = 0; i < tbody.childNodeSize(); i++) {
                            Map<String, String> classMap = new HashMap<String, String>();
                            for (int j = 0; j < 10; j++) {
                                Element classElement = tbody.child(i);  //classElement就是每一门课
                                //课程名称
                                if (j == 2) {
                                    String className = "课程名称";
                                    String classNameVlaueString = classElement.child(j).child(0).text();
                                    classMap.put(className, classNameVlaueString);
                                }

                                //课程性质
                                else if (j == 3) {
                                    String classProperty = "课程性质";
                                    String classPropertyValue = classElement.child(j).text();
                                    if (classPropertyValue.length() < 2) {
                                        classPropertyValue = "无";
                                    }
                                    classMap.put(classProperty, classPropertyValue);
                                }

                                //教学老师
                                else if (j == 5) {
                                    String classTeacher = "教学老师";
                                    String classTeacherValue = classElement.child(j).child(0).text();
                                    classMap.put(classTeacher, classTeacherValue);
                                }

                                //课程学分
                                else if (j == 6) {
                                    String classCredit = "课程学分";
                                    String classCreditValue = classElement.child(j).text();
                                    classMap.put(classCredit, classCreditValue);
                                }

                                //上课时间
                                else if (j == 8) {
                                    String classTime = "上课时间";
                                    String classTimeValueString = classElement.child(j).child(0).text();
                                    classMap.put(classTime, classTimeValueString);
                                }

                                //上课教室
                                else if (j == 9) {
                                    String classPlace = "上课教室";
                                    String[] classPlaceVlaueArray = classElement.child(j).text().split(";");
                                    String classPlaceVlaue = classPlaceVlaueArray[0];
                                    classMap.put(classPlace, classPlaceVlaue);
                                }
                            }
                            classList.add(classMap);
                        }


                        //初始化Course的信息，记录每一次课的信息
                        courseList = new ArrayList<Course>();
                        for (int i = 0; i < classList.size(); i++) {
                            Map<String, String> map = classList.get(i);
                            String[] ss = map.get("上课时间").split(";");
                            //每一次课都是一个类
                            for (int j = 0; j < ss.length; j++) {
                                Course course = new Course();
                                //先初始化时间以外的信息
                                course.className = map.get("课程名称");
                                course.classRoom = map.get("上课教室");
                                course.classCredit = map.get("课程学分");
                                course.classProperty = map.get("课程性质");
                                course.classTeacher = map.get("教学老师");
                                //处理时间信息
                                course.days = StringToInt(ss[j].substring(0, 2)); //储存周几 从0开始
//    		System.out.println(ss[j].substring(0,2));
//    		System.out.println(course.days);
                                //储存 start和step
                                Pattern pattern = Pattern.compile("第.*?节");
                                Matcher matcher = pattern.matcher(ss[j]);
                                String tt = null;
                                while (matcher.find()) {
                                    tt = matcher.group(0);
                                    System.out.println(tt);
                                }
                                int[] startAndStep = new int[2];
                                TimeToInt(tt, course);
//            System.out.println(course.start +" " + course.step);

                                //存储上课周期
                                Pattern pattern1 = Pattern.compile("\\{第.*?周");
                                Matcher matcher1 = pattern1.matcher(ss[j]);
                                String uu = null;
                                while (matcher1.find()) {
                                    uu = matcher1.group(0);
                                }
                                course.classWeek = uu.substring(2);
//            System.out.println(course.classWeek);

                                String[] ii = course.classWeek.split("周");
                                String[] oo = ii[0].split("-");
                                course.startWeek = Integer.parseInt(oo[0]);
                                course.endWeek = Integer.parseInt(oo[1]);
//            System.out.println(course.startWeek + " " + course.endWeek);

                                courseList.add(course);
                            }
                        }
                        //处理特殊课程
                        for (int i = 1; i < courseList.size(); i++) {
                            Course current = courseList.get(i);

                            Course pro = courseList.get(i - 1);
                            if (current.className.equals(pro.className) && (current.days == pro.days)) {
                                pro.step += current.step;
                                courseList.remove(i);
                                i--;
                            }

                        }

                        for(int i=0;i<courseList.size();i++)
                        {
                            subjectDB.saveCourse(courseList.get(i));
                        }

                        heightPx = text1.getHeight();
                        widthPx = days1.getWidth();
                        SharedPreferences.Editor editor = Kpplication.getContext().getSharedPreferences("HW", Kpplication.getContext().MODE_PRIVATE).edit();
                        editor.putInt("heightpx", heightPx);
                        editor.putInt("widthpx", widthPx);
                        editor.commit();

                        LoadButton();

                       // SharedPreferences sharedPreferences = Kpplication.getContext().getSharedPreferences("last", Kpplication.getContext().MODE_PRIVATE);
                       // sharedPreferences.edit().putString("last","subject_content").commit();
                    }
                    break;
                }
                case 2: {
                    Toast.makeText(mActivity, "能不能好好输入？", Toast.LENGTH_SHORT).show();
                }
                case 3: {
                    wait.dismiss();
                    Toast.makeText(mActivity, "不知道为什么失败了,或许重启APP试试？", Toast.LENGTH_SHORT).show();

                }
            }

        }
    };


    public void LoadButton() {
        sharedPreferences.edit().putString("last","subject_content").commit();

        System.out.println("LOADBUTTON");

        layout.removeAllViews();
        layout.addView(pk);

        final View view1 = View.inflate(mActivity, R.layout.diaglog, null);
        className = (TextView) view1.findViewById(R.id.className1);
        classTeacher = (TextView) view1.findViewById(R.id.classTeacher1);
        classTime = (TextView) view1.findViewById(R.id.classTime1);
        classRoom = (TextView) view1.findViewById(R.id.classRoom1);

        final Dialog dialog1 = new Dialog(mActivity);
        //LinearLayout pa = new LinearLayout.

        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        dialog1.addContentView(view1, p);
        dialog1.setTitle("课程详细信息 :");


//                        int heightPx = dip2px(mActivity, 53);
//                        int widthPx = dip2px(mActivity, 47);


        for (int i = 0; i < courseList.size(); i++) {
            final Course course = courseList.get(i);


            //Button button = (Button) customlayout.findViewById(R.id.custombutton);
            Button button = new Button(mActivity);
            button.setText(course.className + "&" + course.classRoom);
            button.setId(i);
            button.setPadding(0, 0, 0, 0);
            button.setTextSize(13);
            button.setTextColor(Color.WHITE);
            int height = course.step * heightPx;
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(widthPx - 3, height - 3); //给子控件设置参数
            lp.leftMargin = course.days * widthPx;
            lp.topMargin = (course.start - 1) * heightPx;
            button.setLayoutParams(lp);
            if(course.endWeek<choose){
                //layout.removeView(button);
                System.out.println("Set color");
                button.setBackgroundColor(0x00000000);
            }else if(course.startWeek>choose) {
                button.setBackgroundColor(0x00000000);
            }
            else{
                if (i % 5 == 0) {
                    button.setBackground(getResources().getDrawable(R.drawable.shapered));
                } else if (i % 5 == 1) {
                    button.setBackground(getResources().getDrawable(R.drawable.shapeblue));
                } else if (i % 5 == 2) {
                    button.setBackground(getResources().getDrawable(R.drawable.shapegreen));
                } else if (i % 5 == 3) {
                    button.setBackground(getResources().getDrawable(R.drawable.shapesky));
                } else {
                    button.setBackground(getResources().getDrawable(R.drawable.shapeyellow));
                }
            }

            //button.setBackground(getResources().getDrawable(R.drawable.shape));


            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    className.setText("课程名称 :" + course.className);
                    classTeacher.setText("课程教师: " + course.classTeacher);
                    classTime.setText("上课时间 :" + course.classWeek + " " + course.start + "-" + (course.start + course.step - 1) + "节");
                    classRoom.setText("上课地点 :" + course.classRoom);
                    dialog1.show();
                }
            });

            layout.addView(button);
        }


    }


    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calsstable, container, false);
        layout = (RelativeLayout) view.findViewById(R.id.relativeLayout);
        ((MainActivity) mActivity).setSwipeRefreshEnable(false);
        sharedPreferences = Kpplication.getContext().getSharedPreferences("last", Kpplication.getContext().MODE_PRIVATE);
        choose = sharedPreferences.getInt("week",1);

        pk = (PickerView) view.findViewById(R.id.selector);

        List<String> data = new ArrayList<String>();
        for (int i = 1; i < 10; i++)
        {
            data.add("0" + i);
        }
        for (int i = 10; i < 20; i++)
        {
            data.add(i+"");
        }
        pk.setData(data);
        pk.setSelected(choose-1);

        pk.setOnSelectListener(new PickerView.onSelectListener() {

            @Override
            public void onSelect(String text) {
                choose = Integer.valueOf(text);
                System.out.println(choose);
                sharedPreferences.edit().putInt("week",choose).commit();
                LoadButton();
            }
        });



        mActivity.setTitle("课程表");
        subjectDB = SubjectDB.getInstance(mActivity);
        arrayList = subjectDB.LoadCourse();
        courseList = arrayList;
        System.out.println(arrayList.size());
        if (arrayList.size() == 0) {
            final View view2 = View.inflate(mActivity, R.layout.subjectdialog, null);
            final EditText user = (EditText) view2.findViewById(R.id.E);
            final EditText pwd = (EditText) view2.findViewById(R.id.P);
            user.setTextColor(0xFF000000);
            pwd.setTextColor(0xFF000000);
            Button button = (Button) view2.findViewById(R.id.yes);
            dialog = new Dialog(mActivity);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            dialog.addContentView(view2, p);
            dialog.setTitle("绑定学号:");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String username = user.getText().toString();
                    final String password = pwd.getText().toString();

                    if (username.equals("") && password.equals("")) {
                        handler.sendEmptyMessage(2);
                        return;
                    }
                    wait = ProgressDialog.show(mActivity, "正在获取课表", "请稍等片刻");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            jwUtils = JwUtils.jwUtils;
                            jwUtils.Init("2", username, password);
                            boolean istrue = jwUtils.OrcImage_Login();
                            System.out.println(istrue);
                            if (istrue == false) {
                                handler.sendEmptyMessage(3);
                                return;
                            }
                            Content = jwUtils.ClassContent();
                            handler.sendEmptyMessage(1);
                        }
                    }).start();
                }
            });


            dialog.show();
        } else {
            heightPx = Kpplication.getContext().getSharedPreferences("HW", Context.MODE_PRIVATE).getInt("heightpx", 0);
            widthPx = Kpplication.getContext().getSharedPreferences("HW", Context.MODE_PRIVATE).getInt("widthpx", 0);
            LoadButton();
        }

        //获取控件的大小
        text1 = (TextView) view.findViewById(R.id.text1);
        days1 = (TextView) view.findViewById(R.id.days1);

        //System.out.println("高度和宽度"+heightPx+widthPx);

        return view;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    private static int StringToInt(String day) {
        int dayInt = 0;
        if (day.equals("周一")) {
            dayInt = 0;
        } else if (day.equals("周二")) {
            dayInt = 1;
        } else if (day.equals("周三")) {
            dayInt = 2;
        } else if (day.equals("周四")) {
            dayInt = 3;
        } else if (day.equals("周五")) {
            dayInt = 4;
        } else if (day.equals("周六")) {
            dayInt = 5;
        } else if (day.equals("周日")) {
            dayInt = 6;
        }

        return dayInt;
    }


    private static void TimeToInt(String time, Course course) {
//		int [] startAndStep = new int [2];
        int start = 0, step = 0;
        //对字符串的处理，完全匹配、、、、
        if (time.equals("第1,2节")) {
            start = 1;
            step = 2;
        } else if (time.equals("第3,4节")) {
            start = 3;
            step = 2;
        } else if (time.equals("第5,6节")) {
            start = 5;
            step = 2;
        } else if (time.equals("第7,8节")) {
            start = 7;
            step = 2;
        } else if (time.equals("第9,10节")) {
            start = 9;
            step = 2;
        } else if (time.equals("第1,2,3节")) {
            start = 1;
            step = 3;
        } else if (time.equals("第5,6,7节")) {
            start = 5;
            step = 3;
        } else if (time.equals("第9,10,11节")) {
            start = 9;
            step = 3;
        } else if (time.equals("第3节")) {
            start = 3;
            step = 1;
        } else if (time.equals("第7节")) {
            start = 7;
            step = 1;
        } else if (time.equals("第11节")) {
            start = 11;
            step = 1;
        } else if (time.equals("第1,2,3,4节")) {
            start = 1;
            step = 4;
        } else if (time.equals("第5,6,7,8节")) {
            start = 5;
            step = 4;
        }
        course.start = start;
        course.step = step;
    }

}
