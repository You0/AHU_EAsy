package krelve.app.Easy.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import krelve.app.Easy.Config;
import krelve.app.Easy.Kpplication;
import krelve.app.Easy.jwxt.JwUtils;
import krelve.app.Easy.R;
import krelve.app.Easy.adapter.MyAdapter;
import krelve.app.Easy.net.HttpConnectionUtils;
import krelve.app.Easy.service.MyService;

/**
 * Created by 11092 on 2016/2/6.
 *
 * 这个没什么好注释的，无非就是listview分级显示的问题。
 * 对于选课的逻辑全在Jwutils这个类里面
 */
public class ChooseActivity extends AppCompatActivity {
    private int LEVEL = 1;
    private int Choosepos = 0;
    private ProgressDialog dialog;
    private ListView listView;
    private ArrayAdapter adapter;
    private String Username;
    private String Password;
    private JwUtils jwUtils = JwUtils.jwUtils;
    private TextView result;
    private ArrayList<String> data;
    private ArrayList<String> changeData;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            dialog.dismiss();
            if (msg.what == 1) {
                if (data.size() == 0) {
                    Toast.makeText(ChooseActivity.this, "您已经学过此课，或者时间冲突!", Toast.LENGTH_SHORT).show();
                    data.addAll(changeData);
                } else {
                    adapter.notifyDataSetChanged();
                    listView.setSelection(0);
                }
            } else if (msg.what == 2) {
                inputTitleDialog();
            } else if (msg.what == 3) {
                if (data.size() == 0) {
                    Toast.makeText(ChooseActivity.this, "查无此课!", Toast.LENGTH_SHORT).show();
                    data.addAll(changeData);
                } else {
                    adapter.notifyDataSetChanged();
                    listView.setSelection(0);
                }
            } else if (msg.what == 4) {
                if (Config.size == 0) {
                    Toast.makeText(ChooseActivity.this, "您当前刷课次数不足!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Config.size--;
                SizeDec();
                Intent startIntent = new Intent(ChooseActivity.this, MyService.class);
                startService(startIntent); // 启动服务
                dialog = ProgressDialog.show(ChooseActivity.this, "努力中。。", "每刷10次反馈一次，" +
                        "如果反馈提示成功，请到教务系统确认。" +
                        "只要程序不被杀死，在后台与锁屏无影响其功能。" +
                        "确保有Wifi，否则你流量分分钟消失。" +
                        "如果掉线将自动登录+自动选取上次指定的课目。" +
                        "刷课期间将无视三秒防刷。");

            } else if (msg.what == 5) {
                String receive = (String) msg.obj;
                result.setText(receive);
            }
        }
    };


    private void SizeDec() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpConnectionUtils utils = new HttpConnectionUtils(Config.Dec + "?id=" + Config.id);
                utils.Read(utils.GetConnection("GET", "", Config.cookie));
            }
        }).start();
    }


    private void inputTitleDialog() {

        final EditText inputServer = new EditText(this);
        inputServer.setTextColor(0xFF000000);
        inputServer.setFocusable(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入课程名:").setView(inputServer).setNegativeButton(
                "取消", null);
        builder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        final ProgressDialog dialog1;
                        dialog1 = ProgressDialog.show(ChooseActivity.this, "正在查询", "亲稍等~~");
                        final String SubjectName = inputServer.getText().toString();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                jwUtils.CrossSubjectContent.clear();
                                jwUtils.CrossSubject(SubjectName);
                                changeData.clear();
                                changeData.addAll(data);
                                data.clear();
                                for (int i = 0; i < jwUtils.CrossSubjectContent.size() / 3; i++) {
                                    data.add(jwUtils.CrossSubjectContent.get(i * 3));
                                }
                                dialog1.dismiss();
                                Message message = new Message();
                                message.what = 3;
                                handler.sendMessage(message);
                            }
                        }).start();

                    }
                });
        builder.show();
    }


    public void querySubject(final int pos) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                if (pos == 0) {
                    jwUtils.SubjectContent.clear();
                    jwUtils.PublicSubject();
                    changeData.clear();
                    changeData.addAll(data);
                    data.clear();
                    for (int i = 0; i < jwUtils.SubjectContent.size() / 2; i++) {
                        data.add(jwUtils.SubjectContent.get(i * 2 + 1));
                    }
                    LEVEL = 2;
                    Choosepos = 0;
                    message.what = 1;
                } else if (pos == 1) {
                    //jwUtils.SportSubjectContent.clear();
                    jwUtils.SportSubject();
                    changeData.clear();
                    changeData.addAll(data);
                    data.clear();
                    for (int i = 3; i <= 27; i++) {
                        data.add(jwUtils.SportSubjectContent.get(i));
                    }
                    LEVEL = 2;
                    Choosepos = 1;
                    message.what = 1;
                } else if (pos == 2) {
                    LEVEL = 2;
                    message.what = 2;
                    Choosepos = 2;
                } else if (pos == 3) {
                    jwUtils.SubjectContent.clear();
                    jwUtils.SpecialSubject();
                    changeData.clear();
                    changeData.addAll(data);
                    data.clear();
                    for (int i = 0; i < jwUtils.SubjectContent.size() / 2; i++) {
                        data.add(jwUtils.SubjectContent.get(i * 2 + 1));
                    }
                    LEVEL = 2;
                    Choosepos = 3;
                    message.what = 1;

                }


                handler.sendMessage(message);
            }
        }).start();
    }


    public void queryTeacher(final int pos) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Choosepos == 0) {
                    jwUtils.TeacherInfoContent.clear();
                    jwUtils.GetTeacherInfo(jwUtils.SubjectContent.get(pos * 2));
                    //GetTeacherInfo(SubjectContent.get(choose*2));
                    changeData.clear();
                    changeData.addAll(data);
                    data.clear();
                    for (int i = 0; i < jwUtils.TeacherInfoContent.size() / 2; i++) {
                        data.add(jwUtils.TeacherInfoContent.get(i * 2));
                        //System.out.println(i+":"+arrayList.get(i*2));
                    }
                    LEVEL = 3;
                } else if (Choosepos == 1) {
                    //jwUtils.SportSubjectContent.clear();
                    jwUtils.querySportsTeacher(pos + 3);
                    changeData.clear();
                    changeData.addAll(data);
                    data.clear();
                    for (int i = 56; i < jwUtils.SportSubjectContent.size() - jwUtils.flag; i = i + 2) {
                        data.add(jwUtils.SportSubjectContent.get(i));
                    }
                    LEVEL = 3;
                } else if (Choosepos == 2) {
                    jwUtils.TeacherInfoContent.clear();
                    jwUtils.queryCrossTeacher(pos);
                    changeData.clear();
                    changeData.addAll(data);
                    data.clear();
                    for (int i = 0; i < jwUtils.TeacherInfoContent.size() / 2; i++) {
                        data.add(jwUtils.TeacherInfoContent.get(i * 2));
                        //System.out.println(i+":"+arrayList.get(i*2));
                    }
                    LEVEL = 3;
                } else {
                    jwUtils.TeacherInfoContent.clear();
                    jwUtils.GetTeacherInfo(jwUtils.SubjectContent.get(pos * 2));
                    changeData.clear();
                    changeData.addAll(data);
                    data.clear();
                    for (int i = 0; i < jwUtils.TeacherInfoContent.size() / 2; i++) {
                        data.add(jwUtils.TeacherInfoContent.get(i * 2));
                    }
                    LEVEL = 3;
                }
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        }).start();
    }

    public void getFinallyPostData(final int pos) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Choosepos == 0) {
                    jwUtils.ChooseTeacher(pos);
                    //System.out.println(jwUtils.PostData);
                    LEVEL = 4;
                } else if (Choosepos == 1) {
                    jwUtils.GetSportsTeacherPostData(pos);
                    LEVEL = 4;
                } else if (Choosepos == 2) {
                    jwUtils.ChooseTeacher(pos);
                    LEVEL = 4;
                } else {
                    jwUtils.ChooseTeacher(pos);
                    LEVEL = 4;
                }
                Message message = new Message();
                message.what = 4;
                handler.sendMessage(message);

            }
        }).start();

    }

    public void onBackPressed() {


        if (LEVEL == 1) {
            finish();
        } else if (LEVEL == 2) {
            data.clear();
            data.add("特殊课程");
            data.add("选体育课");
            data.add("选跨专业");
            data.add("选修课程");
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            --LEVEL;
        } else {
            data.clear();
            data.addAll(changeData);
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            --LEVEL;
        }
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this, "教务处接口关闭期间，程序将无法正常响应。", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.choose);
        result = (TextView) findViewById(R.id.Callback);
        data = new ArrayList<String>();
        changeData = new ArrayList<String>();
        data.add("特殊课程");
        data.add("选体育课");
        data.add("选跨专业");
        data.add("选修课程");
        listView = (ListView) findViewById(R.id.chooselv);
        adapter = new MyAdapter(Kpplication.getContext(), R.layout.choose_item, data);
        listView.setAdapter(adapter);
        //System.out.println(JwUtils.Cookies);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog = ProgressDialog.show(ChooseActivity.this, "正在查询", "请稍等..！");
                if (LEVEL == 1) {
                    querySubject(position);
                } else if (LEVEL == 2) {
                    queryTeacher(position);
                } else if (LEVEL == 3) {
                    if ((Config.authority & (1)) == 0) {
                        Toast.makeText(ChooseActivity.this, "请先登录！(或您没有权限)", Toast.LENGTH_SHORT).show();
                    } else {
                        getFinallyPostData(position);
                        changeData.clear();
                        changeData.addAll(data);
                        data.clear();
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }


}








