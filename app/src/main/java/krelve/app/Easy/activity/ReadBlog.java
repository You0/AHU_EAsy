package krelve.app.Easy.activity;

import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import krelve.app.Easy.Config;
import krelve.app.Easy.R;
import krelve.app.Easy.adapter.ReadAdapter;
import krelve.app.Easy.adapter.recyclerAdapter;
import krelve.app.Easy.bean.Blog;
import krelve.app.Easy.net.HttpConnectionUtils;
import krelve.app.Easy.util.Constant;
import krelve.app.Easy.util.HttpUtils;
import krelve.app.Easy.view.RevealBackgroundView;

/**
 * Created by Me on 2016/3/1.
 */
public class ReadBlog extends AppCompatActivity implements RevealBackgroundView.OnStateChangeListener {
    private ImageView iv;
    private RevealBackgroundView vRevealBackground;
    private AppBarLayout mAppBarLayout;
    private ListView lv;
    private Blog blog;
    private ReadAdapter adapter;
    private RecyclerView recyclerView;
    ArrayList<Blog> arrayList = new ArrayList<Blog>();
    private Blog first;
    // private WebCacheDbHelper dbHelper;
    // private boolean isLight;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    recyclerAdapter  adapter =new recyclerAdapter(arrayList);
                    recyclerView.setAdapter(adapter);
                    break;
                }
            }
        }
    };


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.readblog);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        mAppBarLayout.setVisibility(View.INVISIBLE);
        vRevealBackground = (RevealBackgroundView) findViewById(R.id.revealBackgroundView);
        iv = (ImageView) findViewById(R.id.iv);
//        lv = (ListView) findViewById(R.id.readblog);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        blog = (Blog) bundle.getSerializable("Blog");
        int NOTIFICATION = intent.getIntExtra("NOTIFICATION",0);
        first = blog;
        CollapsingToolbarLayout mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        //设置标题
        mCollapsingToolbarLayout.setTitle(blog.getTitle());
        mCollapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.light_toolbar));
        mCollapsingToolbarLayout.setStatusBarScrimColor(getResources().getColor(R.color.light_toolbar));



        //下面的oncreate处理recycle的内容
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);





        SetTitlebar(blog);
        recyclerView.setHasFixedSize(true);
        if(NOTIFICATION==0){
            setupRevealBackground(savedInstanceState);

        }else{
            mAppBarLayout.setVisibility(View.VISIBLE);
            //setStatusBarColor(Color.TRANSPARENT);
            NotificationManager manager = (NotificationManager)
                    getSystemService(NOTIFICATION_SERVICE);
            manager.cancel(1200);
        }
        setStatusBarColor(getResources().getColor(R.color.light_toolbar));

    }


    //下面与reclyview无关


    private void loadFirst(final Blog blog) {

        if (HttpUtils.isNetworkConnected(this)) {

            new Thread(new Runnable() {
                String line = null;
                String result;
                HttpURLConnection connection;
                HttpConnectionUtils utils ;
                @Override

                //这里面用自己封装的类库就是败了。不知道为什么
                public void run() {
                    // {"replyinfo":[{"replyid":1,"content":"1","iamgeurl":"1","take_date":"1","username":"1","blogsid":1}]}
                    try {
                        String url = Config.GetReply + "?blogsId=" + blog.getBlogsId();
                        connection = (HttpURLConnection) (new URL(url)).openConnection();
                        connection.connect();
                        //connection = utils.GetConnection("GET","",null);
                        BufferedReader is = null;
                        StringBuffer result = new StringBuffer();

                        try {
                            is = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                            line = is.readLine();

                            while (line != null) {
                                result.append(line);
                                line = is.readLine();

                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (is != null) {
                                try {
                                    is.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        JSONObject jsonObject = new JSONObject(result.toString());
                        JSONArray jsonArray = jsonObject.getJSONArray("replyinfo");
                        JSONObject sonObject = null;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            sonObject = jsonArray.getJSONObject(i);
                            Blog blog = new Blog();
                            blog.setBlogsId(sonObject.getInt("blogsid"));
                            blog.setUserheader(sonObject.getString("iamgeurl"));
                            blog.setUsername(sonObject.getString("username"));
                            blog.setContent(sonObject.getString("content"));
                            blog.setTake_date(sonObject.getString("take_date"));
                            blog.setReplyid(sonObject.getInt("replyid"));
                            arrayList.add(blog);
                        }
                        handler.sendEmptyMessage(1);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            //没有网络时
        } else {

            Toast.makeText(this, "无网络连接", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        arrayList.clear();
        arrayList.add(first);
        loadFirst(blog);
    }

    private void SetTitlebar(Blog responseString) {

        ImageLoader imageloader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        if(!(blog.getLimageurls().split(","))[0].equals("null")){
            //System.out.println("url" + (blog.getLimageurls().split(","))[0]);
            imageloader.displayImage((blog.getLimageurls().split(","))[0], iv, options);
        }else {
            System.out.println("excume me?");
            int i = GetRandomNum();
            if (i < 5) {
                iv.setImageResource(R.drawable.radom1);
                //imageloader.displayImage(R.drawable.radom1, iv, options);
                return;
            }
            if (i < 10) {
                iv.setImageResource(R.drawable.radom2);
                return;
            }
            if (i < 15) {
                iv.setImageResource(R.drawable.radom3);
                return;
            }
            if (i < 25) {
                iv.setImageResource(R.drawable.radom4);
                return;
            }
        }
    }

    public static int GetRandomNum()
    {
        return new Random().nextInt(25);
    }


    private void setupRevealBackground(Bundle savedInstanceState) {
        vRevealBackground.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            final int[] startingLocation = getIntent().getIntArrayExtra(Constant.START_LOCATION);
            vRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    vRevealBackground.startFromLocation(startingLocation);
                    return true;
                }
            });
        } else {
            vRevealBackground.setToFinishedFrame();
        }
    }

    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
            mAppBarLayout.setVisibility(View.VISIBLE);
            setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, R.anim.slide_out_to_left_from_right);
    }

    private void setStatusBarColor(int statusBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // If both system bars are black, we can remove these from our layout,
            // removing or shrinking the SurfaceFlinger overlay required for our views.
            Window window = this.getWindow();
            if (statusBarColor == Color.BLACK && window.getNavigationBarColor() == Color.BLACK) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
            window.setStatusBarColor(statusBarColor);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.reply) {
            Intent intent = new Intent(this, SendActivity.class);
            intent.putExtra("reply", true);
            intent.putExtra("blogsId",blog.getBlogsId());
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


}
