package krelve.app.Easy.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import krelve.app.Easy.Kpplication;
import krelve.app.Easy.jwxt.JwUtils;


/**
 * Created by 11092 on 2016/2/7.
 */


//线程池的知识之前在java编程思想里看过，但是现在忘光了。等看了Java并发编程实战之后再修改此Service吧
public class MyService extends Service {
    private int count=-1;
    private JwUtils jwUtils;
    private String result;
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            result = result + "+已刷"+msg.what+"次";
            Toast.makeText(Kpplication.getContext(), result, Toast.LENGTH_SHORT).show();
        }
    };

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        System.out.println("Service onCreate");
        jwUtils = JwUtils.jwUtils;
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("Service onStartCommand");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                while (true){
                    result=jwUtils.ShuaKe(jwUtils.PostData);
                    jwUtils.OrcImage_Login();
                    count++;
                    if(count%10==0){
                        message = handler.obtainMessage();
                        message.what = count;
                        handler.sendMessage(message);
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                while (true){
                    result=jwUtils.ShuaKe(jwUtils.PostData);
                    jwUtils.OrcImage_Login();
                    count++;
                    if(count%10==0){
                        message = handler.obtainMessage();
                        message.what = count;
                        handler.sendMessage(message);
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                while (true){
                    result=jwUtils.ShuaKe(jwUtils.PostData);
                    jwUtils.OrcImage_Login();
                    count++;
                    if(count%10==0){
                        message = handler.obtainMessage();
                        message.what = count;
                        handler.sendMessage(message);
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                while (true){
                    result=jwUtils.ShuaKe(jwUtils.PostData);
                    jwUtils.OrcImage_Login();
                    count++;
                    if(count%10==0){
                        message = handler.obtainMessage();
                        message.what = count;
                        handler.sendMessage(message);
                    }
                }
            }
        }).start();


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent startIntent = new Intent(MyService.this, MyService.class);
        startService(startIntent);
    }
}
