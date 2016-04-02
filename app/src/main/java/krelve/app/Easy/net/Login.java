package krelve.app.Easy.net;

import android.content.Intent;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import krelve.app.Easy.Config;
import krelve.app.Easy.Kpplication;
import krelve.app.Easy.fragment.MenuFragment;
import krelve.app.Easy.service.MyService;

/**
 * Created by Me on 2016/3/2.
 */
public class Login {

    static private MenuFragment menuFragment;
    public static void loginWithToken(){
        SharedPreferences sharedPreferences = Kpplication.getContext().getSharedPreferences("user", Kpplication.getContext().MODE_PRIVATE);
        // editor.putString("uuid",uuid);
        String uuid = sharedPreferences.getString("uuid", "");
        HttpURLConnection connection = null;
        uuid = "?Token=" + uuid;
        System.out.println(uuid);

        try {
            connection = (HttpURLConnection) (new URL(Config.Login + uuid)).openConnection();
            connection.connect();


            //System.out.println(uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }


       // HttpConnectionUtils utils = new HttpConnectionUtils(Config.Login + uuid);
       // connection = utils.GetConnection("GET", "", null);
//        ArrayList<String> arrayList = new ArrayList<String>();
//        arrayList.add("Token");arrayList.add(uuid);
//        connection = utils.GetConnection("POST", arrayList, null);
//        utils.connect();
        try{
            JSONObject jsonObject = new JSONObject(HttpConnectionUtils.Read(connection));

            System.out.println(jsonObject.toString());
            Config.cookie = connection.getHeaderField("Set-Cookie");
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", jsonObject.getString("username"));
            editor.putInt("id", jsonObject.getInt("id"));
            Config.id = jsonObject.getInt("id");
            Config.size = jsonObject.getInt("size");
            editor.putString("userhead", jsonObject.getString("userhead"));
            editor.putInt("authority", jsonObject.getInt("authority"));

            System.out.println(jsonObject.getString("username"));
            System.out.println(jsonObject.getInt("id"));
            System.out.println(jsonObject.getString("userhead"));
            System.out.println(jsonObject.getInt("authority"));
            Config.authority = jsonObject.getInt("authority");
            Config.size = jsonObject.getInt("size");
            editor.commit();
//            menuFragment = new MenuFragment();
//            menuFragment.loadInfo();

        }catch (Exception e){
            e.printStackTrace();
        }


    }


}
