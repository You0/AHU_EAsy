package krelve.app.Easy.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.net.HttpURLConnection;

import krelve.app.Easy.jwxt.JwUtils;
import krelve.app.Easy.net.HttpConnectionUtils;
import krelve.app.Easy.R;
import krelve.app.Easy.activity.ChooseActivity;
import krelve.app.Easy.activity.MainActivity;

/**
 * Created by 11092 on 2016/2/17.
 */
public class RobClassFragment extends BaseFragment implements View.OnClickListener{
    private ProgressDialog dialog;
    private static int port=2;
    private ActionBar actionBar;
    private EditText userNameText;
    private EditText passwdText;
    private Button bnLogin;
    private String Username;
    private String Password;
    private CheckBox checkBox1,checkBox2,checkBox3;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                dialog.dismiss();
                Intent intent = new Intent(mActivity,ChooseActivity.class);
                intent.putExtra("Username",Username);
                intent.putExtra("Password",Password);
                mActivity.startActivity(intent);
            }else{
                dialog.dismiss();
                Toast.makeText(mActivity, "账号密码错误，请重试", Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) mActivity).setToolbarTitle("自动抢课");
        ((MainActivity) mActivity).setSwipeRefreshEnable(false);

        View view = inflater.inflate(R.layout.main_layout,container,false);

        userNameText = (EditText) view.findViewById(R.id.userNameText);
        passwdText = (EditText) view.findViewById(R.id.passwdText);
        bnLogin = (Button) view.findViewById(R.id.bnLogin);
        checkBox1 = (CheckBox) view.findViewById(R.id.checkBox1);
        checkBox2 = (CheckBox) view.findViewById(R.id.checkBox2);
        checkBox3 = (CheckBox) view.findViewById(R.id.checkBox3);

        checkBox1.setOnClickListener(this);
        checkBox2.setOnClickListener(this);
        checkBox3.setOnClickListener(this);
        bnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userNameText.getText().toString().equals("") ||
                        passwdText.getText().toString().equals("")) {
                    Toast.makeText(mActivity, "请输入学号或密码", Toast.LENGTH_SHORT).show();
                } else {
                    dialog = ProgressDialog.show(mActivity, "正在登陆", "请稍等..！");

                    Username = userNameText.getText().toString();
                    Password = passwdText.getText().toString();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            JwUtils jwUtils = JwUtils.jwUtils;
                            jwUtils.Init(String.valueOf(port), Username, Password);
                            boolean istrue = jwUtils.OrcImage_Login();
                            Message message = new Message();
                            if (istrue) {
                                message.what = 1;
                            } else {
                                message.what = 0;
                            }
                            handler.sendMessage(message);
                        }
                    }).start();
                }
            }
        });
        return view;
    }

    @Override
    public void onClick(View view) {
        System.out.println(port);
        switch (view.getId()){
            case R.id.checkBox1:{


                if(checkBox1.isChecked()){
                    checkBox2.setChecked(true);
                    checkBox2.setChecked(false);
                    checkBox3.setChecked(false);
                    checkBox1.setChecked(true);
                    port =1;

                }else{
                    checkBox1.setChecked(false);
                    port = 2;
                    checkBox2.setChecked(true);
                }
                break;
            }
            case R.id.checkBox2:{
                if(checkBox2.isChecked())
                {
                    checkBox1.setChecked(false);
                    checkBox3.setChecked(false);
                    checkBox2.setChecked(true);
                    port = 2;
                }else{
                    checkBox2.setChecked(true);
                    port = 2;
                }
                break;
            }
            case R.id.checkBox3:{
                if(checkBox3.isChecked()){
                    checkBox2.setChecked(false);
                    checkBox1.setChecked(false);
                    checkBox3.setChecked(true);
                    port =3;

                }else{
                    checkBox3.setChecked(false);
                    port = 2;
                    checkBox2.setChecked(true);

                }
                break;
            }

        }
    }
}
