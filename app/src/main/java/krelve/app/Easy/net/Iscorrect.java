package krelve.app.Easy.net;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import krelve.app.Easy.Ecrypt.Encrypt;
import krelve.app.Easy.Kpplication;

/**
 * Created by 11092 on 2016/2/17.
 */
public class Iscorrect {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;
    private String username;
    private String password;
    public static String msg;
    private HttpConnectionUtils httpConnectionUtils;
    private HttpURLConnection connection;
    private ArrayList<String> arrayList;
    public static boolean success;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(context, "绑定失败，请检查账号密码", Toast.LENGTH_SHORT).show();
            Login();
        }
    };


    public Iscorrect(Context context) {
        this.context = context;
    }


    public boolean Login() {
        sharedPreferences = Kpplication.getContext().getSharedPreferences("School_card", Kpplication.getContext().MODE_PRIVATE);
        editor = sharedPreferences.edit();
        username = sharedPreferences.getString("username", "");
        password = sharedPreferences.getString("password", "");
        msg = sharedPreferences.getString("msg", "");


        if (password.equals("")) {
            inputTitleDialog();
            return false;
        } else {
            try {
                System.out.println(password);
                String Hashpwd = Encrypt.binary2Hex(Encrypt.encrypt(URLEncoder.encode(password, "utf-8").toLowerCase(), "synjones")).toUpperCase();
                arrayList = new ArrayList<String>();

                arrayList.add("phone");
                arrayList.add("#");
                arrayList.add("weixinhao");
                arrayList.add("#");
                arrayList.add("clientMark");
                arrayList.add("#");
                arrayList.add("cardimsi");
                arrayList.add("#");
                arrayList.add("signType");
                arrayList.add("SynSno");
                arrayList.add("account");
                arrayList.add(username);
                arrayList.add("simCode");
                arrayList.add("null");
                arrayList.add("password");
                arrayList.add(Hashpwd);
                arrayList.add("clientType");
                arrayList.add("Android");
                // System.out.println(Hashpwd);
                //System.out.println(arrayList);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            httpConnectionUtils = new HttpConnectionUtils("http://card.ahu.edu.cn:8070/Api/Account/SignInAndGetUserPlus");
                            connection = httpConnectionUtils.GetConnection("POST", arrayList, null);
                            httpConnectionUtils.connect();
                            // System.out.println(httpConnectionUtils.Read(connection));
                            JSONObject jsonObject = new JSONObject(httpConnectionUtils.Read(connection));
                            if (jsonObject.getBoolean("success")) {
                                success = true;
                                editor.putString("msg", jsonObject.getString("msg"));
                                msg = jsonObject.getString("msg");
                                editor.commit();
                            } else {
                                success =false;
                                editor.putString("username", "");
                                editor.putString("password", "");
                                editor.commit();
                                Message message = handler.obtainMessage();
                                handler.sendMessage(message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return true;
    }


    private void inputTitleDialog() {

        final LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);
        final EditText inputUsername = new EditText(Kpplication.getContext());
        inputUsername.setTextColor(0xFF000000);
        inputUsername.setHint("请输入学号");
        inputUsername.setPadding(50, 50, 50, 50);
        final EditText inputPassword = new EditText(Kpplication.getContext());
        inputPassword.setTextColor(0xFF000000);
        inputPassword.setPadding(50, 50, 50, 50);
        inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        inputPassword.setHint("请输入查询密码(身份证后6位)");
        //inputUsername.setFocusable(true);
        ll.addView(inputUsername);
        ll.addView(inputPassword);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("请绑定账号密码:").setView(ll).setNegativeButton(
                "取消", null);

        builder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String name = inputUsername.getText().toString();
                        String pwd = inputPassword.getText().toString();
                        editor.putString("username", name);
                        editor.putString("password", pwd);
                        editor.commit();
                        Login();
                    }
                });
        builder.show();
    }
}
