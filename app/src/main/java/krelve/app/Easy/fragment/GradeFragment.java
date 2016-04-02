package krelve.app.Easy.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import krelve.app.Easy.Kpplication;
import krelve.app.Easy.R;
import krelve.app.Easy.activity.MainActivity;
import krelve.app.Easy.bean.Course;
import krelve.app.Easy.bean.GradeCourse;
import krelve.app.Easy.db.GradeDB;
import krelve.app.Easy.db.SubjectDB;
import krelve.app.Easy.jwxt.JwUtils;

/**
 * Created by Me on 2016/3/18 0018.
 */
public class GradeFragment extends BaseFragment {
    private ArrayList<ArrayList<GradeCourse> > linkedList;
    private Dialog dialog;
    private Dialog wait;
    private String Content;
    private JwUtils jwUtils = JwUtils.jwUtils;
    private TextView zongChengJi;
    private ViewPager viewPager;
    private ArrayList<String> titleList;
    private String username;
    private String password;
    private ArrayList<View> viewList;
    private View view;
    private GradeDB gradeDB;
    private DecimalFormat format =  new java.text.DecimalFormat("#.00");
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1: {
                    dialog.dismiss();
                    wait.dismiss();
                    System.out.println("case2");
                    //处理成绩数据
                    Document doc = Jsoup.parse(Content);
                    Elements tables = doc.select("table#Datagrid1");//成绩的table
                    Element tbody = tables.get(0).child(0);  //我们需要的是第一个table，他的第一个子元素 tbody

                    //移除不必要数据
                    tbody.child(0).remove();  //剩下35个需要的课程
                    //处理所有的课程的成绩
                    ArrayList<ArrayList<GradeCourse>> termList = new ArrayList<ArrayList<GradeCourse>>();
                    for (int i = 0, j = 0; i < tbody.childNodeSize(); ) {
                        ArrayList<GradeCourse> courseList = new ArrayList<GradeCourse>();
                        if (j == 0) {
                            GradeCourse course = new GradeCourse();
                            Element tr = tbody.child(i);
                            getContent(course, tr);
                            courseList.add(course);

                            j++;
                            i++;
                        }
                        if (j != 0) {
                            Element td1 = tbody.child(i).child(1);
                            Element td2 = tbody.child(i - 1).child(1);
                            while (td1.text().equals(td2.text())) {
                                Element tr = tbody.child(i);
                                GradeCourse course = new GradeCourse();
                                getContent(course, tr);
                                courseList.add(course);
//					course.Printf();
                                i++;
                                if (i == tbody.childNodeSize()) break;
                                td1 = tbody.child(i).child(1);
                                td2 = tbody.child(i - 1).child(1);
                            }
                            j = 0;
                            termList.add(courseList);
                        }
                    }
                    //调用课表界面的Activity,并且传递给ClassTable一个ArrayList<Course>
//                    Intent intent = new Intent();
//                    intent.setClass(Login.this, gradeTable.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("termList", termList);
//                    intent.putExtras(bundle);
//                    startActivity(intent);

                    for(int i=0;i<termList.size();i++)
                    {
                        //System.out.println("iiiiii"+i);
                        for(int j=0;j<termList.get(i).size();j++)
                        {
                            //System.out.println(termList.get(i).get(j).classTerm);
                            gradeDB.saveCourse(termList.get(i).get(j),i);
                        }

                    }


                    Load(termList);
                    break;
                }
                case 2: {
                    wait.dismiss();
                    Toast.makeText(mActivity, "能不能好好输入？", Toast.LENGTH_SHORT).show();
                }
                case 3: {
                    wait.dismiss();
                    Toast.makeText(mActivity, "不知道为什么失败了,或许重启APP试试？", Toast.LENGTH_SHORT).show();

                }
            }

        }
    };

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.gradetable, container, false);
        sharedPreferences = getContext().getSharedPreferences("studentid", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        mActivity.setTitle("学生成绩");
        gradeDB = GradeDB.getInstance(mActivity);
        PagerTabStrip pagerTabStrip = (PagerTabStrip)view.findViewById(R.id.pagerTitle);
        pagerTabStrip.setTextColor(Color.BLACK);
        pagerTabStrip.setDrawFullUnderline(true);
        pagerTabStrip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        linkedList = gradeDB.LoadCourse();
        if (linkedList.size() == 0) {
            final View view2 = View.inflate(mActivity, R.layout.subjectdialog, null);
            final EditText user = (EditText) view2.findViewById(R.id.E);
            final EditText pwd = (EditText) view2.findViewById(R.id.P);
            user.setTextColor(0xFF000000);
            pwd.setTextColor(0xFF000000);
            username = sharedPreferences.getString("username","");
            password = sharedPreferences.getString("password","");
            user.setText(username);
            pwd.setText(password);
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
                    username = user.getText().toString();
                    password = pwd.getText().toString();


                    if (username.equals("") && password.equals("")) {
                        handler.sendEmptyMessage(2);
                        return;
                    }
                    wait = ProgressDialog.show(mActivity, "正在获取成绩", "请稍等片刻");
                    editor.putString("username",username);
                    editor.putString("password",password);
                    editor.commit();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            jwUtils.Init("2", username, password);
                            boolean istrue = jwUtils.OrcImage_Login();
                            System.out.println(istrue);
                            if (istrue == false) {
                                handler.sendEmptyMessage(3);
                                return;
                            }
                            Content = jwUtils.GrageContent();
                            System.out.println(Content);
                            handler.sendEmptyMessage(1);
                        }
                    }).start();
                }
            });
            dialog.show();

        }else{
            Load(linkedList);
        }
        return view;
    }

    private void Load(ArrayList<ArrayList<GradeCourse>> data) {
        double termGPA = 0;


        //PagerTabStrip pagerTabStrip = (PagerTabStrip)findViewById(R.id.pagerTitle);
        viewList = new ArrayList<View>();  //加载pager需要的View的list
        titleList = new ArrayList<String>();//标题的list

        viewPager = (ViewPager) view.findViewById(R.id.viewPager);

        View pager[] = new View[data.size()];
        for (int i = 0; i < data.size(); i++) {
            //获得这个学期的数据
            ArrayList<GradeCourse> courseList = data.get(i);

            //加载页卡
            pager[i] = LayoutInflater.from(mActivity).inflate(R.layout.pager, null);

            //设置页卡中的内容
            TextView xueQiChengJi = (TextView) pager[i].findViewById(R.id.xueQiChengJi);
            final ListView listView = (ListView) pager[i].findViewById(R.id.listView);

            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {

                }

                @Override
                public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                    if (listView != null && listView.getChildCount() > 0) {
                        boolean enable = (i == 0) && (absListView.getChildAt(i).getTop() == 0);
                        ((MainActivity) mActivity).setSwipeRefreshEnable(enable);
                    }
                }
            });


            ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
            double gpa = 0;
            double credit = 0;

            for (int j = 0; j < courseList.size(); j++) {
                GradeCourse course = courseList.get(j);

                gpa += course.classGPA * course.classCredit;
                credit += course.classCredit;

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("course", course.className + ":" + course.classGrade);
                mylist.add(map);
            }

            SimpleAdapter adapter = new SimpleAdapter(mActivity, mylist, R.layout.grade_item, new String[]{"course"}, new int[]{R.id.course});

            listView.setAdapter(adapter);

            double GPA = gpa / credit;
            xueQiChengJi.setText("本学期绩点: " + format.format(GPA));

            termGPA += GPA;

            viewList.add(pager[i]);
            titleList.add("第" + (i + 1) + "学期");
        }


        termGPA = termGPA / data.size();  //所有学期的平均绩点
        zongChengJi = (TextView) view.findViewById(R.id.zongChengJi);
        zongChengJi.setText(format.format(termGPA));


        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return viewList.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                container.removeView(viewList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(viewList.get(position));
                return viewList.get(position);
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

//            @Override
//            public int getItemPosition(Object object) {
//                return super.getItemPosition(object);
//            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titleList.get(position);
            }

        });

        viewPager.setCurrentItem(data.size());

    }


    public static void getContent(GradeCourse course, Element tr) {
        course.classYear = tr.child(0).text();
        course.classTerm = Integer.parseInt(tr.child(1).text());
        course.className = tr.child(3).text();
        course.classProperty = tr.child(4).text();
        course.classCredit = Double.parseDouble(tr.child(6).text());
        course.classGPA = Double.parseDouble(tr.child(7).text());
        course.classGrade = tr.child(8).text();
        course.classDepartment = tr.child(12).text();
    }


}
