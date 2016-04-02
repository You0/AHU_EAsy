package krelve.app.Easy.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import krelve.app.Easy.Config;
import krelve.app.Easy.R;
import krelve.app.Easy.activity.SchoolInfo;
import krelve.app.Easy.adapter.MyItemClickListener;
import krelve.app.Easy.adapter.SchoolInforevAdapter;
import krelve.app.Easy.bean.Blog;
import krelve.app.Easy.bean.SchoolInfoBean;
import krelve.app.Easy.net.HttpConnectionUtils;

/**
 * Created by Me on 2016/3/9 0009.
 */
public class SchoolNewsFragment extends BaseFragment implements MyItemClickListener{
    private RecyclerView recyclerView;
    private ArrayList<SchoolInfoBean> schoolInfoBeans;
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 1:{
                    SchoolInforevAdapter adapter = new SchoolInforevAdapter(schoolInfoBeans);
                    adapter.setOnItemClickListener(SchoolNewsFragment.this);
                    recyclerView.setAdapter(adapter);
                }
            }
        }
    };


    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.school_info_layout, null);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(mActivity);
        recyclerView.setLayoutManager(manager);
        schoolInfoBeans =new ArrayList<SchoolInfoBean>();
        mActivity.setTitle("资讯");
        GetSchoolInfo();

        return view;
    }

    private void GetSchoolInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpConnectionUtils utils = new HttpConnectionUtils(Config.SchoolInfo);
                HttpURLConnection connection = utils.GetConnection("GET", "", null);
                SchoolInfoBean schoolInfoBean;
                String result = utils.Read(connection);

                try{
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("info");

                    for (int i = jsonArray.length() - 1; i >= 0; i--) {
                        JSONObject jsonObject1 = jsonArray
                                .getJSONObject(i);
                        schoolInfoBean = new SchoolInfoBean();
                        schoolInfoBean.setTitle(jsonObject1.getString("title"));
                        schoolInfoBean.setUrl(jsonObject1.getString("url"));
                        schoolInfoBean.setDate(jsonObject1.getString("date"));
                        schoolInfoBeans.add(schoolInfoBean);
                    }

                    handler.sendEmptyMessage(1);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();


    }


    @Override
    public void onItemClick(View view, int postion) {
        String url = schoolInfoBeans.get(postion).getUrl();
        Intent intent = new Intent(mActivity, SchoolInfo.class);
        intent.putExtra("url",url);
        startActivity(intent);

        //Toast.makeText(mActivity,url,Toast.LENGTH_SHORT).show();
    }
}
