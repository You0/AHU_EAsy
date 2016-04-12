package krelve.app.Easy.activity;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;

import krelve.app.Easy.Kpplication;
import krelve.app.Easy.fragment.ClassTableFragment;
import krelve.app.Easy.fragment.ConsumptionFragment;
import krelve.app.Easy.fragment.FoundFragment;
import krelve.app.Easy.fragment.GradeFragment;
import krelve.app.Easy.fragment.MainFragment;
import krelve.app.Easy.fragment.RobClassFragment;
import krelve.app.Easy.R;
import krelve.app.Easy.db.CacheDbHelper;
import krelve.app.Easy.fragment.MenuFragment;
import krelve.app.Easy.fragment.NewsFragment;
import krelve.app.Easy.fragment.SchoolCardFragment;
import krelve.app.Easy.fragment.SchoolNewsFragment;
import krelve.app.Easy.net.Login;
import krelve.app.Easy.service.MyService;
import krelve.app.Easy.service.PushService;

public class MainActivity extends AppCompatActivity {
    private Menu Mmenu;
    private FrameLayout fl_content;
    private MenuFragment menu_fragment;
    private DrawerLayout mDrawerLayout;
    private SwipeRefreshLayout sr;
    private long firstTime;
    public String curId;
    private Toolbar toolbar;
    private boolean isLight;
    private CacheDbHelper dbHelper;
    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        dbHelper = new CacheDbHelper(this, 1);
        isLight = sp.getBoolean("isLight", true);
        initView();
        loadLatest();
        Login();


        Intent startIntent = new Intent(this, PushService.class);
        startService(startIntent); // 启动服务
    }

    public static void Login()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Login.loginWithToken();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void loadLatest() {
        SharedPreferences sharedPreferences = Kpplication.getContext().getSharedPreferences("last", Kpplication.getContext().MODE_PRIVATE);
        String last = sharedPreferences.getString("last", "found");
        if (last.equals("latest")) {
            getSupportFragmentManager().beginTransaction().
                    setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left).
                    replace(R.id.fl_content, new MainFragment(), "latest").
                    commit();
            curId = "latest";
        } else if (last.equals("subject_content")) {
            getSupportFragmentManager().beginTransaction().
                    setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left).
                    replace(R.id.fl_content, new ClassTableFragment(), "subject_content").
                    commit();
            curId = "subject_content";
        } else if (last.equals("found")) {
            getSupportFragmentManager().beginTransaction().
                    setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left).
                    replace(R.id.fl_content, new FoundFragment(), "found").
                    commit();
            curId = "found";
        }


    }

    public void setCurId(String id) {
        curId = id;
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(isLight ? R.color.light_toolbar : R.color.dark_toolbar));
        setSupportActionBar(toolbar);
        setStatusBarColor(getResources().getColor(isLight ? R.color.light_toolbar : R.color.dark_toolbar));

        sr = (SwipeRefreshLayout) findViewById(R.id.sr);
        sr.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        sr.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (curId.equals("latest")) {
                    replaceFragment();
                    sr.setRefreshing(false);
                } else if (curId.equals("found")) {
                    replaceFragment();
                    sr.setRefreshing(false);
                } else if (curId.equals("consumption")) {
                    replaceFragment();
                    sr.setRefreshing(false);
                }else if(curId.equals("grades")){
                    File file = new File("data/data/krelve.app.kuaihu/databases/term");
                    file.delete();
                    replaceFragment();
                    sr.setRefreshing(false);
                }
            }
        });
        fl_content = (FrameLayout) findViewById(R.id.fl_content);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        final ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

//    getSupportFragmentManager().beginTransaction().
//    setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left).
//    replace(R.id.fl_content, new MainFragment(), "latest").
//    commit();
//    curId = "latest";


    public void replaceFragment() {
        if (curId.equals("latest")) {
            Mmenu.getItem(0).setTitle("发布谣言");
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left)
                    .replace(R.id.fl_content,
                            new MainFragment(), "latest").commit();
        } else if (curId.equals("choose_class")) {
            Mmenu.getItem(0).setTitle("重新绑定");
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left)
                    .replace(R.id.fl_content,
                            new RobClassFragment(), "choose_class").commit();

        } else if (curId.equals("school_card")) {
            Mmenu.getItem(0).setTitle("重新绑定");
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left)
                    .replace(R.id.fl_content,
                            new SchoolCardFragment(), "school_card").commit();

        } else if (curId.equals("consumption")) {
            Mmenu.getItem(0).setTitle("重新绑定");
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left)
                    .replace(R.id.fl_content,
                            new ConsumptionFragment(), "consumption").commit();

        } else if (curId.equals("found")) {
            Mmenu.getItem(0).setTitle("发布谣言");
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left)
                    .replace(R.id.fl_content,
                            new FoundFragment(), "found").commit();

        } else if (curId.equals("subject_content")) {
            Mmenu.getItem(0).setTitle("重新绑定");
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left)
                    .replace(R.id.fl_content,
                            new ClassTableFragment(), "subject_content").commit();

        }else if(curId.equals("info")){
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left)
                    .replace(R.id.fl_content,new SchoolNewsFragment(),"info").commit();

        }else if(curId.equals("grades")){
            Mmenu.getItem(0).setTitle("重新绑定");
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left)
                    .replace(R.id.fl_content,new GradeFragment(),"grades").commit();

        }

    }

    public void closeMenu() {
        mDrawerLayout.closeDrawers();
    }

    public void setSwipeRefreshEnable(boolean enable) {
        sr.setEnabled(enable);
    }

    public void setToolbarTitle(String text) {
        toolbar.setTitle(text);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Mmenu = menu;
        if (curId.equals("found") || curId.equals("latest")) {
            menu.getItem(0).setTitle("发布谣言");
        } else {

        }
        //menu.getItem(0).setTitle(sp.getBoolean("isLight", true) ? "夜间模式" : "日间模式");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.send_new) {
            if (item.getTitle().equals("发布谣言")) {
                Intent intent = new Intent(this, SendActivity.class);
                startActivity(intent);
            } else {
                File file = new File("data/data/krelve.app.kuaihu/databases/");
                String[] filename = file.list();
                for(int i=0;i<filename.length;i++)
                {
                    file = new File("data/data/krelve.app.kuaihu/databases/"+filename[i]);
                    file.delete();
                }
                file = new File("data/data/krelve.app.kuaihu/shared_prefs/School_card.xml");
                file.delete();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isLight() {
        return isLight;
    }

    public CacheDbHelper getCacheDbHelper() {
        return dbHelper;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            closeMenu();
        } else {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Snackbar sb = Snackbar.make(fl_content, "再按一次退出", Snackbar.LENGTH_SHORT);
                sb.getView().setBackgroundColor(getResources().getColor(isLight ? android.R.color.holo_blue_dark : android.R.color.black));
                sb.show();
                firstTime = secondTime;
            } else {
                finish();
            }
        }

    }


    @TargetApi(21)
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
}
