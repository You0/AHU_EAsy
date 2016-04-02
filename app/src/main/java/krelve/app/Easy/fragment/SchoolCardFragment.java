package krelve.app.Easy.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import krelve.app.Easy.Config;
import krelve.app.Easy.R;
import krelve.app.Easy.activity.MainActivity;
import krelve.app.Easy.net.HttpConnectionUtils;
import krelve.app.Easy.net.Iscorrect;


/**
 * Created by 11092 on 2016/2/17.
 */
public class SchoolCardFragment extends BaseFragment {
    private Iscorrect ic ;
    private EditText count;
    private EditText pwd;
    private Button button;
    private HttpConnectionUtils utils;
    private HttpURLConnection connection;
    private ProgressDialog dialog;




    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            dialog.dismiss();
            if(msg.what==1){
                AlDialog();
            }
            if(msg.what==2){
                Toast.makeText(mActivity, "充值失败，请检查！", Toast.LENGTH_SHORT).show();
            }

        }
    };


    public void AlDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("充值成功！");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                pwd.setText("");
                count.setText("");
            }
        });
        builder.show();
    }

    @Override
    protected View initView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) mActivity).setToolbarTitle("校园卡充值");
        ((MainActivity) mActivity).setSwipeRefreshEnable(false);

        ic = new Iscorrect(this.getActivity());
        View view = inflater.inflate(R.layout.school_card_layout,container,false);
        count = (EditText) view.findViewById(R.id.count);
        pwd = (EditText) view.findViewById(R.id.query_pwd);
        button = (Button) view.findViewById(R.id.button);
        ic.Login();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if((Config.authority&(1<<4)) ==0)
                {
                    Toast.makeText(mActivity,"请先登录！(或您没有权限)",Toast.LENGTH_SHORT).show();
                    return;

                }



                final int Count;
                final String password = pwd.getText().toString();

                try {
                     Count = Integer.parseInt(count.getText().toString());
                }catch (Exception e){
                    Toast.makeText(mActivity, "金额错误", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Count > 300 || Count <= 0) {
                    Toast.makeText(mActivity, "金额错误", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.equals("")) {
                    Toast.makeText(mActivity, "请输入查询密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                dialog = ProgressDialog.show(mActivity, "充值中", "请稍等..！");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<String> arrayList = new ArrayList<String>();
                        arrayList.add("amount");
                        arrayList.add(String.valueOf(Count));
                        arrayList.add("bankpass");
                        arrayList.add("#");
                        arrayList.add("smsCode");
                        arrayList.add("#");
                        arrayList.add("transferType");
                        arrayList.add("null");
                        arrayList.add("toaccount");
                        arrayList.add("card");
                        arrayList.add("iPlanetDirectoryPro");
                        arrayList.add(ic.msg);
                        arrayList.add("fromaccount");
                        arrayList.add("null");
                        arrayList.add("password");
                        arrayList.add(password);
                        arrayList.add("clientType");
                        arrayList.add("Android");

                        try {
                            utils = new HttpConnectionUtils("http://card.ahu.edu.cn:8070/Api/Card/BankTransfer");
                            connection = utils.GetConnection("POST", arrayList, null);
                            utils.connect();
                            JSONObject jsonObject = new JSONObject(utils.Read(connection));
                            if (jsonObject.getBoolean("success")) {
                                Message message = handler.obtainMessage();
                                message.what = 1;
                                handler.sendMessage(message);

                            } else {
                                Message message = handler.obtainMessage();
                                message.what = 2;
                                handler.sendMessage(message);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        //System.out.println(utils.Read(connection));
                    }
                }).start();
            }
        });


        return view;
    }




}
