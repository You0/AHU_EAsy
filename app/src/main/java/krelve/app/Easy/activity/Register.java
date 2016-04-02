package krelve.app.Easy.activity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import krelve.app.Easy.Config;
import krelve.app.Easy.Kpplication;
import krelve.app.Easy.R;
import krelve.app.Easy.fragment.MenuFragment;
import krelve.app.Easy.net.HttpConnectionUtils;
import krelve.app.Easy.net.UploadUtils;
import krelve.app.Easy.util.FileUtil;


public class Register extends Activity implements OnClickListener {
    private Context mContext;
    private CircleImg avatarImg;// 头像图片
    private Button registBtn;// 页面的登录按钮
    private EditText username;//用户名
    private EditText password;//密码
    private EditText email; //邮箱
    private EditText comfirpassword;
    private TextView alreadyAccout;
    private EditText nickname;
    private ImageView imageView;
    private Bitmap photo;
    private String strUsername;
    private MenuFragment menu_fragment;

    private static final int TIME_OUT = 10 * 10000000; // 超时时间
    private static final String CHARSET = "utf-8"; // 设置编码
    public static final String SUCCESS = "1";
    public static final String FAILURE = "0";


    private SelectPicPopupWindow menuWindow; // 自定义的头像编辑弹出框
    // 上传服务器的路径【一般不硬编码到程序中】
    //http://10.0.2.2:8080/EAsy/appregist
    private String imgUrl = Config.RegistUrl;
    private static final String IMAGE_FILE_NAME = "avatarImage.jpg";// 头像文件名称
    private String urlpath = "";         // 图片本地路径
    private String resultStr = "";  // 服务端返回结果集
    private static ProgressDialog pd;// 等待进度圈
    private static final int REQUESTCODE_PICK = 0;      // 相册选图标记
    private static final int REQUESTCODE_TAKE = 1;      // 相机拍照标记
    private static final int REQUESTCODE_CUTTING = 2;   // 图片裁切标记
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    ProgressDialog dialog;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dialog.dismiss();
            switch (msg.what){
                case 0:
                    Toast.makeText(mContext,"注册成功！",Toast.LENGTH_SHORT).show();
                    SharedPreferences sharedPreferences =Kpplication.getContext().getSharedPreferences("user", Kpplication.getContext().MODE_PRIVATE);
                    // editor.putString("uuid",uuid);
                   // String uuid = sharedPreferences.getString("uuid","");

                    editor.putString("username", strUsername);
                    editor.commit();
                    menu_fragment = new MenuFragment();
                    menu_fragment.loadInfo();
                    MainActivity.Login();
                    onBackPressed();
//                    imageView = (ImageView) findViewById(R.id.userheader);
//                    TextView textView = (TextView) findViewById(R.id.tv_login);
//                    textView.setText(strUsername.toString());
//                    imageView.setImageBitmap(photo);
                    break;
                case 1:
                    Toast.makeText(mContext,"网络错误",Toast.LENGTH_SHORT).show();
                    break;

                case 2:
                    Toast.makeText(mContext,"您的账号已被注册！",Toast.LENGTH_SHORT).show();
                    break;

            }


        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist_layout);


        mContext = Register.this;
        dialog = new ProgressDialog(mContext);
        dialog.setTitle("正在注册");
        dialog.setMessage("请稍后。。");
        initViews();
    }

    /**
     * 初始化页面控件
     */
    private void initViews() {
        avatarImg = (CircleImg) findViewById(R.id.avatarImg);
        registBtn = (Button) findViewById(R.id.registbtn);
        nickname = (EditText) findViewById(R.id.nickname);
        password = (EditText) findViewById(R.id.firstpassword);
        comfirpassword = (EditText) findViewById(R.id.comfirmPassword);
        username = (EditText) findViewById(R.id.user_name);
        alreadyAccout = (TextView) findViewById(R.id.alreadyAccout);

        email = (EditText) findViewById(R.id.email);

        avatarImg.setOnClickListener(this);
        registBtn.setOnClickListener(this);
        alreadyAccout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.avatarImg:// 更换头像点击事件
                menuWindow = new SelectPicPopupWindow(mContext, itemsOnClick);
                menuWindow.showAtLocation(findViewById(R.id.mainLayout),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;


            case R.id.alreadyAccout:
                Intent intent = new Intent(Register.this,LoginActivity.class);
                startActivity(intent);
                finish();
                break;


            case R.id.registbtn://注册按钮跳转事件

                String temp = null;
                try{
                    //System.out.println(nickname.getText().toString());
                    temp =  URLEncoder.encode(URLEncoder.encode(nickname.getText().toString(), "UTF-8"), "UTF-8");
                    //System.out.println(temp);
                    strUsername = nickname.getText().toString();
                }catch (Exception e){
                    e.printStackTrace();
                }
                final String strpassword = password.getText().toString();
                String strconfirmpassword = comfirpassword.getText().toString();
                final String nick_name = temp;

                final String em = email.getText().toString();
                final String user_name = username.getText().toString();

                if (user_name.equals("") && strpassword.equals("") &&
                        strconfirmpassword.equals("") && nick_name.equals("")
                        && em.equals("")) {
                    Toast.makeText(mContext, "请填写完整", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!strpassword.equals(strconfirmpassword)) {
                    Toast.makeText(mContext, "二次输入的密码不相等", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (urlpath.equals("")) {
                    Toast.makeText(mContext, "请选择头像", Toast.LENGTH_SHORT).show();

                    return;
                }
                dialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {


                        HttpURLConnection connection = null;
                        HttpConnectionUtils utils = null;
                        HashMap<String, String> textParams = new HashMap<String, String>();
                        HashMap<String, Bitmap> fileparams = new HashMap<String, Bitmap>();

                        try {
                            utils = new HttpConnectionUtils(imgUrl);
                            connection = utils.GetConnection("POST", "", null);
                            connection.setReadTimeout(TIME_OUT);
                            connection.setConnectTimeout(TIME_OUT);
                            connection.setDoInput(true);
                            connection.setUseCaches(false); // 不允许使用缓存
                            connection.setRequestProperty("Charset", CHARSET); // 设置编码
                            connection.setRequestProperty("connection", "keep-alive");
                            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + UploadUtils.BOUNDARY);

//                            connection.setRequestProperty("Content-Type", UploadUtils.CONTENT_TYPE + ";boundary="
//                                    + UploadUtils.BOUNDARY);

                            //要上传的文字数据
                            textParams.put("username",user_name);
                            textParams.put("password",strpassword);
                            textParams.put("nickname",nick_name);
                            textParams.put("e-mail",em);

                            // 要上传的图片文件
                            //File file = new File(urlpath);
                            Bitmap bitmap = BitmapFactory.decodeFile(urlpath);
                            fileparams.put("image", bitmap);
                            OutputStream os = connection.getOutputStream();
                            DataOutputStream ds = new DataOutputStream(os);
                            UploadUtils.writeStringParams(textParams, ds);
                            UploadUtils.writeFileParams(fileparams, ds);
                            UploadUtils.paramsEnd(ds);
                            // 对文件流操作完,要记得及时关闭
                            os.close();
                            // 服务器返回的响应吗
                            int code = connection.getResponseCode(); // 从Internet获取网页,发送请求,将网页以流的形式读回来
                            // 对响应码进行判断
                            Config.cookie = connection.getHeaderField("Set-Cookie");

                            if (code == 200) {// 返回的响应码200,是成功
                                InputStream is = connection.getInputStream();
                                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                                String uuid = br.readLine();
                                if(uuid.length()<5)
                                {
                                    handler.sendEmptyMessage(2);
                                    return;
                                }
                                //System.out.println("难道没有UUID嘛"+uuid);
                                editor = Kpplication.getContext().getSharedPreferences("user", Kpplication.getContext().MODE_PRIVATE).edit();
                                editor.putString("uuid",uuid);
                                editor.commit();

                            } else {
                                handler.sendEmptyMessage(1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        handler.sendEmptyMessage(0);// 执行耗时的方法之后发送消给handler
                    }
                }).start();
                //startActivity(new Intent(mContext, UploadActivity.class));
                break;

            default:
                break;
        }
    }

    //为弹出窗口实现监听类  
    private OnClickListener itemsOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                // 拍照
                case R.id.takePhotoBtn:
                    Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //下面这句指定调用相机拍照后的照片存储的路径
                    takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
                    startActivityForResult(takeIntent, REQUESTCODE_TAKE);
                    break;
                // 相册选择图片
                case R.id.pickPhotoBtn:
                    Intent intent = new Intent(Kpplication.getContext(),GridViewActivity.class);
                    intent.putExtra("REQUESTCODE_PICK",REQUESTCODE_PICK);
                    startActivityForResult(intent, REQUESTCODE_PICK);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUESTCODE_PICK:// 直接从相册获取
                try {
                   // System.out.println("我的天"+String.valueOf(data.getCharSequenceArrayListExtra("data_return").get(0)));
                    //startPhotoZoom(Uri.fromFile(new File(data.getStringExtra("data_return"))));
                    Intent intent = new Intent(this,CropActivity.class);
                    intent.putExtra("url",data.getStringExtra("data_return"));
                    startActivityForResult(intent,REQUESTCODE_CUTTING);

                } catch (NullPointerException e) {
                    e.printStackTrace();// 用户点击取消操作
                }
                break;
            case REQUESTCODE_TAKE:// 调用相机拍照
                File temp = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
                startPhotoZoom(Uri.fromFile(temp));
                break;
            case REQUESTCODE_CUTTING:// 取得裁剪后的图片
                if (data != null) {
                    setPicToView(data);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 500);
        intent.putExtra("outputY", 500);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        String path = picdata.getStringExtra("path");
        urlpath= path;
        if (path != null) {
            // 取得SDCard图片路径做显示
            photo = BitmapFactory.decodeFile(path);
            //System.out.println("photo"+photo);
           // photo = Bitmap.createScaledBitmap(photo,10000,10000,true);
//            photo.setWidth(500);
//            photo.setHeight(500);

            Drawable drawable = new BitmapDrawable(null, photo);
            //urlpath = FileUtil.saveFile(mContext, "temphead.jpg", photo);

            //System.out.println("url+"+urlpath);
            avatarImg.setImageDrawable(drawable);

        }
    }


}
