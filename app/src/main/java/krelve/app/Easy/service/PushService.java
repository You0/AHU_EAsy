package krelve.app.Easy.service;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;


import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import krelve.app.Easy.Config;
import krelve.app.Easy.Kpplication;
import krelve.app.Easy.R;
import krelve.app.Easy.activity.ReadBlog;
import krelve.app.Easy.bean.Blog;
import krelve.app.Easy.net.HttpConnectionUtils;

/**
 * Created by Me on 2016/3/9 0009.
 */
public class PushService extends Service {
    HttpConnectionUtils utils;
    HttpURLConnection connection;
    SharedPreferences sharedPreferences;
    ArrayList<String> arrayList;
    Blog blog;
    static int NOTIFICATION = 1200;

    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        System.out.println("Create Service");
        utils = new HttpConnectionUtils(Config.Push);
        sharedPreferences = Kpplication.getContext().getSharedPreferences("user", Kpplication.getContext().MODE_PRIVATE);
        arrayList = new ArrayList<String>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                arrayList.clear();
                arrayList.add("userId");
                arrayList.add(String.valueOf(sharedPreferences.getInt("id", 0)));

                while (true) {
                    System.out.println("正在轮询服务器");
                    utils.setURL(Config.Push);
                    connection = utils.GetConnection("POST", arrayList, Config.cookie);
                    utils.connect();
                    String result = utils.Read(connection);
                    System.out.println("轮询所得结果" + result);
                    if (!result.equals("0")&&!result.equals("")) {
                        System.out.println("查询到了结果!");
                        utils.setURL(Config.Notification + "?blogsId=" + result);
                        connection = utils.GetConnection("GET", "", Config.cookie);
                        result = utils.Read(connection);
                        try {
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
                            }

                            Intent intent = new Intent(Kpplication.getContext(), ReadBlog.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("Blog", blog);
                            intent.putExtras(bundle);
                            intent.putExtra("NOTIFICATION", 1);
                            PendingIntent pi = PendingIntent.getActivity(Kpplication.getContext(), 0, intent,
                                    PendingIntent.FLAG_CANCEL_CURRENT);

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(Kpplication.getContext());
                            builder.setSmallIcon(R.mipmap.retina);
                            builder.setContentTitle("有人回复您了！");
                            builder.setContentText("点我查看！");
                            builder.setContentIntent(pi);
                            Notification notification = builder.build();
                            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            manager.notify(NOTIFICATION, notification);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        Thread.sleep(100000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
