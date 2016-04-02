package krelve.app.Easy.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import krelve.app.Easy.Config;
import krelve.app.Easy.Kpplication;
import krelve.app.Easy.R;
import krelve.app.Easy.activity.MainActivity;
import krelve.app.Easy.activity.ReadBlog;
import krelve.app.Easy.adapter.FoundAdapter;
import krelve.app.Easy.bean.Blog;
import krelve.app.Easy.net.HttpConnectionUtils;
import krelve.app.Easy.util.Constant;

/**
 * Created by Me on 2016/2/27.
 */
public class FoundFragment extends BaseFragment {
    static public ListView listView;
    private boolean isLoading = false;
    private int ServerBlogs = 0;
    private int tempCount = 0;
    private Blog blog = null;
    private ArrayList<Blog> arrayList = new ArrayList<Blog>();
    private static int tag = 0;
    ;
    private FoundAdapter adapter;
    private final int NET = 1;
    private final int LOCAL = 0;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    adapter = new FoundAdapter(mActivity, R.layout.found_item, arrayList);
                    listView.setAdapter(adapter);
                }
                case 2: {
                    if (adapter == null) {
                        adapter = new FoundAdapter(mActivity, R.layout.found_item, arrayList);
                        listView.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                        isLoading = false;
                    }
                    break;
                }
            }
        }
    };


    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) mActivity).setToolbarTitle("发现");
        View view = inflater.inflate(R.layout.found_layout, container, false);
        listView = (ListView) view.findViewById(R.id.found_listview);
        sharedPreferences = Kpplication.getContext().getSharedPreferences("Found", Kpplication.getContext().MODE_PRIVATE);
        editor = sharedPreferences.edit();
        parseJson(0, 0, LOCAL, sharedPreferences.getString("Json", ""));


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int[] startingLocation = new int[2];
                view.getLocationOnScreen(startingLocation);
                startingLocation[0] += view.getWidth() / 2;
                Intent intent = new Intent(mActivity, ReadBlog.class);
                intent.putExtra(Constant.START_LOCATION, startingLocation);
                Bundle bundle = new Bundle();
                bundle.putSerializable("Blog", arrayList.get(position));
                intent.putExtras(bundle);
                //operation(Config.Operator, arrayList.get(position).getBlogsId(), 2);
                startActivity(intent);
                mActivity.overridePendingTransition(0, 0);
            }
        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                final int choose = position;
                //builder.setIcon(R.drawable.ic_launcher);
                builder.setTitle("请选择一项操作");
                //    指定下拉列表的显示数据
                final String[] cities = {"顶置", "加亮", "删除"};
                //    设置一个下拉的列表选择项
                builder.setItems(cities, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //choose = which;
                        if(which==2){
                            operation(Config.DeleteBlog,arrayList.get(choose).getBlogsId(),0);
                        }
                        if(which==0){
                            operation(Config.Operator, arrayList.get(choose).getBlogsId(),1);
                        }
                        if(which==1){
                            operation(Config.Operator, arrayList.get(choose).getBlogsId(),0);

                        }
                    }
                });
                builder.show();



                return false;
            }
        });

        getLast();
        //loadmore(0, 100);


        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (listView != null && listView.getChildCount() > 0) {
                    System.out.println("first" + firstVisibleItem + "count" + totalItemCount + "ServerBlogs" + ServerBlogs);
                    boolean enable = (firstVisibleItem == 0) && (view.getChildAt(firstVisibleItem).getTop() == 0);
                    ((MainActivity) mActivity).setSwipeRefreshEnable(enable);
                    if (firstVisibleItem + visibleItemCount == totalItemCount && !isLoading) {
                        if (ServerBlogs - totalItemCount <= 0) {
                            if(tag==0){
                                Toast.makeText(mActivity, "到底啦!", Toast.LENGTH_SHORT).show();
                                tag=1;
                            }

                            return;
                        } else if (0 < ServerBlogs - totalItemCount && ServerBlogs - totalItemCount < 10) {
                            loadmore(tempCount, ServerBlogs - totalItemCount+tempCount-1);
                            tag = 0;
                        } else {
                            loadmore(ServerBlogs - totalItemCount - 10+tempCount, ServerBlogs - totalItemCount+tempCount);
                            tag = 0;
                        }
                    }
                }
            }
        });


        return view;
    }

    public void SaveJson(String json) {
        editor.putString("Json", json);
        editor.commit();
    }

    public void operation(final String url,final int id,final int tag)
    {

        new Thread(new Runnable() {
            @Override
            public void run() {
                //System.out.println("url+"+url+"cookie"+Config.cookie);
               HttpConnectionUtils utils = new HttpConnectionUtils(url);
                ArrayList list = new ArrayList();
                list.add("tag");list.add(String.valueOf(tag));
                list.add("blogsId");list.add(String.valueOf(id));
                HttpURLConnection connection = utils.GetConnection("POST",list,Config.cookie);
                utils.connect();
            }
        }).start();
    }





    public void getLast() {
        new Thread(new Runnable() {
            String value = null;

            @Override
            public void run() {
                try {
                    HttpConnectionUtils utils = new HttpConnectionUtils(Config.GetLast);
                    HttpURLConnection connection = utils.GetConnection("GET", "", null);
                    String result = utils.Read(connection);

                    SaveJson(result);
                    JSONObject jsonObject = new JSONObject(result);
                    arrayList = new ArrayList<Blog>();
                    JSONArray jsonArray = jsonObject.getJSONArray("info");

                    for (int i = jsonArray.length() - 1; i >= 0; i--) {
                        JSONObject jsonObject1 = jsonArray
                                .getJSONObject(i);
                        blog = new Blog();
                        blog.setBlogsId(jsonObject1.getInt("blogsId"));
                        blog.setContent(jsonObject1.getString("content"));
                        blog.setTitle(jsonObject1.getString("title"));
                        blog.setId(jsonObject1.getInt("id"));
                        blog.setIslight(jsonObject1.getInt("islight"));
                        blog.setLimageurls(jsonObject1.getString("limageurls"));
                        blog.setSimageurls(jsonObject1.getString("simageurls"));
                        blog.setReplycount(jsonObject1.getInt("replycount"));
                        blog.setTake_date(jsonObject1.getString("take_date"));
                        blog.setSeecount(jsonObject1.getInt("seecount"));
                        blog.setUserheader(jsonObject1.getString("userheader"));
                        blog.setUsername(jsonObject1.getString("username"));
                        arrayList.add(blog);
                    }

                    utils.setURL(Config.GetCount);
                    connection = utils.GetConnection("GET", "", null);
                    result = utils.Read(connection);
                    String[] nums = result.split(":");
                    ServerBlogs = Integer.valueOf(nums[0]);
                    tempCount = Integer.valueOf(nums[1]);
                    Message message = handler.obtainMessage();
                    message.what = 1;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void loadmore(final int first, final int last) {
        System.out.println("NET LoadMore"+first+":"+last);
        parseJson(first, last, NET, null);

    }

    private void parseJson(final int first, final int last, final int isNet, final String json) {
        new Thread(new Runnable() {
            String value = null;
            String result = json;

            @Override
            public void run() {
                isLoading = true;
                try {
                    if (isNet == NET) {
                        value = "?first=" + first + "&last=" + last;
                        HttpConnectionUtils utils = new HttpConnectionUtils(Config.GetBlogs + value);
                        HttpURLConnection connection = utils.GetConnection("GET", "", null);
                        result = utils.Read(connection);
                        SaveJson(result);
                    }

                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("info");

                    for (int i = jsonArray.length() - 1; i >= 0; i--) {
                        JSONObject jsonObject1 = jsonArray
                                .getJSONObject(i);
                        blog = new Blog();
                        blog.setBlogsId(jsonObject1.getInt("blogsId"));
                        blog.setContent(jsonObject1.getString("content"));
                        blog.setTitle(jsonObject1.getString("title"));
                        blog.setId(jsonObject1.getInt("id"));
                        blog.setIslight(jsonObject1.getInt("islight"));
                        blog.setLimageurls(jsonObject1.getString("limageurls"));
                        blog.setSimageurls(jsonObject1.getString("simageurls"));
                        blog.setReplycount(jsonObject1.getInt("replycount"));
                        blog.setTake_date(jsonObject1.getString("take_date"));
                        blog.setSeecount(jsonObject1.getInt("seecount"));
                        blog.setUserheader(jsonObject1.getString("userheader"));
                        blog.setUsername(jsonObject1.getString("username"));
                        arrayList.add(blog);
                    }
                    Message message = handler.obtainMessage();
                    message.what = 2;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
