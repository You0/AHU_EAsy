package krelve.app.Easy.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jsoup.helper.HttpConnection;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import krelve.app.Easy.Config;
import krelve.app.Easy.Kpplication;
import krelve.app.Easy.R;
import krelve.app.Easy.fragment.MenuFragment;
import krelve.app.Easy.jwxt.JwUtils;
import krelve.app.Easy.net.HttpConnectionUtils;
import krelve.app.Easy.net.Login;

/**
 * Created by Me on 2016/3/18 0018.
 */
public class LoginActivity extends AppCompatActivity {
    private ProgressDialog dialog;
    private EditText userNameText;
    private EditText passwdText;
    private Button bnLogin;
    private String Username;
    private String Password;
    private  MenuFragment menu_fragment;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                dialog.dismiss();
                menu_fragment = new MenuFragment();
                menu_fragment.loadInfo();
                finish();



            }else{
                dialog.dismiss();
                Toast.makeText(LoginActivity.this, "出现错误", Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        userNameText = (EditText)findViewById(R.id.userNameText);
        passwdText = (EditText)findViewById(R.id.passwdText);
        bnLogin = (Button) findViewById(R.id.bnLogin);

        bnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userNameText.getText().toString().equals("") ||
                        passwdText.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, "请输入账号或密码", Toast.LENGTH_SHORT).show();
                } else {
                    dialog = ProgressDialog.show(LoginActivity.this, "正在登陆", "请稍等..！");

                    Username = userNameText.getText().toString();
                    Password = passwdText.getText().toString();
                    Calendar calendar = Calendar.getInstance();
                    //int hour = calendar.get(Calendar.HOUR_OF_DAY); // 获取小时;

                    int minute = calendar.get(Calendar.MINUTE); // 获取分钟;
                    final String result = Integer.toString(minute ^ 12345678);
                    //System.out.println(minute);



                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //username，password和date。
                            System.out.println(Config.Login
                                    + "?username=" + Username + "&password=" + Password +
                                    "&date=" + result);
                            String uuid  = null;

                            try {
                                HttpURLConnection connection;
                                connection = (HttpURLConnection) (new URL(Config.Login
                                        +"?username="+Username+"&password="+Password+
                                        "&date="+result)).openConnection();
                                connection.connect();
                                uuid = HttpConnectionUtils.Read(connection);

                                //System.out.println(uuid);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

//                            HttpConnectionUtils utils = new HttpConnectionUtils(Config.Login
//                            +"?username="+Username+"&password="+Password+
//                            "&date="+result);
//
//                            HttpURLConnection connection = utils.GetConnection("GET", "", null);
//                            try {
//                                connection.connect();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
                            //String uuid = utils.Read(connection);
                            System.out.println("LoginAcy"+uuid);
                            SharedPreferences.Editor editor = Kpplication.getContext().getSharedPreferences("user", Kpplication.getContext().MODE_PRIVATE).edit();
                            editor.putString("uuid",uuid);
                            editor.commit();
                            Login.loginWithToken();
                            handler.sendEmptyMessage(1);
                        }
                    }).start();
                }
            }
        });
    }




    }
