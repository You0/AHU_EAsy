package krelve.app.Easy.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;

import krelve.app.Easy.Config;
import krelve.app.Easy.R;
import krelve.app.Easy.activity.MainActivity;
import krelve.app.Easy.adapter.CardAdapter;
import krelve.app.Easy.bean.ConsumptionBean;
import krelve.app.Easy.net.HttpConnectionUtils;
import krelve.app.Easy.net.Iscorrect;

/**
 * Created by 11092 on 2016/2/18.
 */
public class ConsumptionFragment extends BaseFragment {
    private Iscorrect ic;
    private HttpConnectionUtils utils;
    private HttpURLConnection connection;
    private ProgressDialog dialog;
    private ArrayList<ConsumptionBean> MsgArray;
    private ConsumptionBean bean;
    private ListView listView;
    private CardAdapter cardAdapter;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dialog.dismiss();
            if (msg.what == 1) {
                cardAdapter = new CardAdapter(mActivity, R.layout.acc_item, (ArrayList<ConsumptionBean>) msg.obj);
                listView.setAdapter(cardAdapter);
            }
            if (msg.what == 2) {
                Toast.makeText(mActivity, "查询失败！", Toast.LENGTH_SHORT).show();
            }

        }
    };


    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) mActivity).setToolbarTitle("今日消费记录");
        ic = new Iscorrect(mActivity);
        System.out.println(1<<3);

        if ((Config.authority & (1 << 5)) == 0) {
            Toast.makeText(mActivity, "请先登录！(或您没有权限)", Toast.LENGTH_SHORT).show();
        } else if (ic.Login()) {
            if (ic.success) {
                dialog = ProgressDialog.show(mActivity, "正在查询", "请稍等..！");
                query();
            }

        }

        View view = inflater.inflate(R.layout.acc_history, container, false);
        listView = (ListView) view.findViewById(R.id.Acclist);
        ((MainActivity) mActivity).setSwipeRefreshEnable(true);
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

        return view;
    }


    private void query() {
        MsgArray = new ArrayList<ConsumptionBean>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> arrayList = new ArrayList<String>();


                try {
                    arrayList.add("iPlanetDirectoryPro");
                    arrayList.add(ic.msg);
                    arrayList.add("type");
                    arrayList.add("1");
                    arrayList.add("trancode");
                    arrayList.add("#");
                    arrayList.add("enddate");
                    arrayList.add("#");
                    arrayList.add("begindate");
                    arrayList.add("#");
                    utils = new HttpConnectionUtils("http://card.ahu.edu.cn:8070/Api/Card/GetTrjnCount");
                    connection = utils.GetConnection("POST", arrayList, null);
                    utils.connect();
                    JSONObject jsonObject = new JSONObject(utils.Read(connection));
                    utils.release();
                    System.out.println(jsonObject);
                    if (jsonObject.getBoolean("success")) {
                        int sum = 0;
                        int page = 1;
                        int result = jsonObject.getInt("obj");
                        while (result > sum) {
                            arrayList.clear();
                            arrayList.add("pageSize");
                            arrayList.add("10");
                            arrayList.add("trancode");
                            arrayList.add("#");
                            arrayList.add("iPlanetDirectoryPro");
                            arrayList.add(ic.msg);
                            arrayList.add("pageIndex");
                            arrayList.add(String.valueOf(page));
                            page++;

                            utils.setURL("http://card.ahu.edu.cn:8070/Api/Card/GetCurrentTrjn");
                            connection = utils.GetConnection("POST", arrayList, null);
                            utils.connect();
                            jsonObject = new JSONObject(utils.Read(connection));
                            if (jsonObject.getBoolean("success")) {
                                Message message = handler.obtainMessage();
                                System.out.println(jsonObject);
                                JSONArray jsonArray = jsonObject.getJSONArray("obj");
                                sum = sum + jsonArray.length();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject localJSONObject2 = jsonArray.getJSONObject(i);
                                    bean = new ConsumptionBean();
                                    bean.setJnDateTime(localJSONObject2.getString("JnDateTime"));
                                    bean.setAccAmt(localJSONObject2.getString("CardBalance"));
                                    bean.setTranAmt(localJSONObject2.getString("TranAmt"));
                                    bean.setMercName(localJSONObject2.getString("MercName"));
                                    bean.setTranName(localJSONObject2.getString("TranName"));
                                    MsgArray.add(bean);
                                }
                            }
                        }

                        //System.out.println(MsgArray.toString());
                        Message message = handler.obtainMessage();
                        message.what = 1;
                        message.obj = MsgArray;
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


}
