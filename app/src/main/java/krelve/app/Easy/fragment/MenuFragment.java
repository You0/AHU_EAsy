package krelve.app.Easy.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import krelve.app.Easy.Config;
import krelve.app.Easy.Kpplication;
import krelve.app.Easy.activity.CircleImg;
import krelve.app.Easy.activity.CropActivity;
import krelve.app.Easy.activity.GridViewActivity;
import krelve.app.Easy.activity.MainActivity;
import krelve.app.Easy.activity.Register;
import krelve.app.Easy.activity.SelectPicPopupWindow;
import krelve.app.Easy.activity.SendActivity;
import krelve.app.Easy.model.NewsListItem;
import krelve.app.Easy.net.HttpConnectionUtils;
import krelve.app.Easy.net.Login;
import krelve.app.Easy.net.UploadUtils;
import krelve.app.Easy.util.Constant;
import krelve.app.Easy.util.FileUtil;
import krelve.app.Easy.util.HttpUtils;
import krelve.app.Easy.R;
import krelve.app.Easy.util.PreUtils;

public class MenuFragment extends BaseFragment implements OnClickListener {
    private ListView lv_item;
    private static TextView tv_download, tv_main, tv_backup, tv_login;
    private LinearLayout ll_menu;
    private static CircleImg circleImg;
    private TextView tv_sentence;
    private SelectPicPopupWindow menuWindow; // 自定义的头像编辑弹出框
    String urlpath;
    private SharedPreferences sharedPreferences;
    /**
     * 使用相册中的图片
     */
    public static final int SELECT_PIC_BY_PICK_PHOTO = 2;
    private static final String IMAGE_FILE_NAME = "avatarImage.jpg";// 头像文件名称
    private static final int REQUESTCODE_PICK = 0;      // 相册选图标记
    private static final int REQUESTCODE_TAKE = 1;      // 相机拍照标记
    private static final int REQUESTCODE_CUTTING = 2;   // 图片裁切标记


    //     private static String[] ITEMS = { "自动抢课", "课程表", "查成绩", "微博趣图",
//     "校园卡消费", "校园卡充值", "学校消息聚合", "最新VIP分享"};
    private List<NewsListItem> items;
    //private Handler handler = new Handler();
    private boolean isLight;
    //private NewsTypeAdapter mAdapter;

    public static void loadInfo() {
        File file = new File("data/data/krelve.app.kuaihu/JiaXT/temphead.jpg");
        SharedPreferences sharedPreferences = Kpplication.getContext().getSharedPreferences("user", Kpplication.getContext().MODE_PRIVATE);
        if (file.exists()) {
            sharedPreferences = Kpplication.getContext().getSharedPreferences("user", Kpplication.getContext().MODE_PRIVATE);
            tv_login.setText(sharedPreferences.getString("username", "用户名读取出错"));
            Bitmap bitmap = BitmapFactory.decodeFile("data/data/krelve.app.kuaihu/JiaXT/temphead.jpg");
            circleImg.setImageBitmap(bitmap);
        } else {
            if (Config.authority == 204) {
            } else {
                tv_login.setText(sharedPreferences.getString("username", "用户名读取出错"));
                Kpplication.mImageloader.displayImage(sharedPreferences.getString("userhead", ""), circleImg, Kpplication.options);
//                String path = Kpplication.mImageloader.getDiscCache().get(sharedPreferences.getString("userhead","")).getPath();
//                System.out.println(path);
//                Bitmap bitmap = BitmapFactory.decodeFile(path);
//                FileUtil.saveFile(Kpplication.getContext(),"data/data/krelve.app.kuaihu/JiaXT/temphead.jpg" , bitmap);
            }
        }
    }


    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu, container, false);
        ll_menu = (LinearLayout) view.findViewById(R.id.ll_menu);
        circleImg = (CircleImg) view.findViewById(R.id.userheader);
        tv_login = (TextView) view.findViewById(R.id.tv_login);
        //tv_backup = (TextView) view.findViewById(R.id.tv_backup);
        //tv_download = (TextView) view.findViewById(R.id.tv_download);
        File file = new File("data/data/krelve.app.kuaihu/JiaXT/temphead.jpg");
        sharedPreferences = Kpplication.getContext().getSharedPreferences("user", Kpplication.getContext().MODE_PRIVATE);
        Config.authority = sharedPreferences.getInt("authority", 204);
        if (file.exists()) {
            //sharedPreferences = Kpplication.getContext().getSharedPreferences("user", Kpplication.getContext().MODE_PRIVATE);
            tv_login.setText(sharedPreferences.getString("username", "用户名读取出错"));
            Bitmap bitmap = BitmapFactory.decodeFile("data/data/krelve.app.kuaihu/JiaXT/temphead.jpg");
            circleImg.setImageBitmap(bitmap);
        } else {
            if (Config.authority == 204) {
            } else {
                tv_login.setText(sharedPreferences.getString("username", "用户名读取出错"));
                Kpplication.mImageloader.displayImage(sharedPreferences.getString("userhead", ""), circleImg, Kpplication.options);
            }
        }


//        tv_download.setOnClickListener(this);
        tv_main = (TextView) view.findViewById(R.id.tv_main);
        tv_sentence = (TextView) view.findViewById(R.id.tv_sentence);
        tv_sentence.setText(Kpplication.sharedPreferences.getString("sentence", "月照千峰为一人。"));
        tv_main.setOnClickListener(this);
        circleImg.setOnClickListener(this);
        //设置监听
        view.findViewById(R.id.choose_class).setOnClickListener(this);
        view.findViewById(R.id.consumption).setOnClickListener(this);
        view.findViewById(R.id.school_card).setOnClickListener(this);
        view.findViewById(R.id.tv_login).setOnClickListener(this);
        view.findViewById(R.id.found).setOnClickListener(this);
        view.findViewById(R.id.subject_content).setOnClickListener(this);
        view.findViewById(R.id.info).setOnClickListener(this);
        view.findViewById(R.id.grades).setOnClickListener(this);
        view.findViewById(R.id.tv_sentence).setOnClickListener(this);
        return view;
    }



    private void replace() {
        ((MainActivity) mActivity).replaceFragment();
        ((MainActivity) mActivity).closeMenu();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_login: {
                if (tv_login.getText().equals("请登录")) {
                    Intent intent = new Intent(mActivity, Register.class);
                    startActivity(intent);
                } else {

                }

                break;
            }

            case R.id.found: {
                ((MainActivity) mActivity).curId = "found";
                replace();
                break;
            }
            case R.id.subject_content: {
                ((MainActivity) mActivity).curId = "subject_content";
                replace();
                break;

            }


            case R.id.tv_main: {
                ((MainActivity) mActivity).curId = "latest";
                replace();
                break;
            }

            case R.id.choose_class: {
                ((MainActivity) mActivity).curId = "choose_class";
                replace();

                break;
            }

            case R.id.consumption: {
                ((MainActivity) mActivity).curId = "consumption";
                replace();
                break;
            }

            case R.id.school_card: {
                ((MainActivity) mActivity).curId = "school_card";
                replace();
                break;
            }

            case R.id.info: {
                ((MainActivity) mActivity).curId = "info";
                replace();
                break;

            }
            case R.id.userheader: {
                if (tv_login.getText().equals("请登录")) {
                    Intent intent = new Intent(mActivity, Register.class);
                    startActivity(intent);
                } else {
                    menuWindow = new SelectPicPopupWindow(mActivity, itemsOnClick);
                    menuWindow.showAtLocation(mActivity.findViewById(R.id.drawerlayout),
                            Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                }
                break;

            }

            case R.id.grades: {
                ((MainActivity) mActivity).curId = "grades";
                replace();
                break;

            }
            case R.id.tv_sentence: {
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                final LinearLayout ll = new LinearLayout(mActivity);
                ll.setHorizontalGravity(LinearLayout.VERTICAL);
                final EditText input = new EditText(mActivity);
                input.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                input.setHint("请输入");
                input.setTextColor(0xff000000);
                ll.addView(input);
                builder.setView(ll).setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String inputStr = input.getText().toString();
                                tv_sentence.setText(inputStr);
                                Kpplication.editor.putString("sentence", inputStr).commit();
                            }
                        });

                builder.show();

            }


        }
    }

    public void updateTheme() {
        isLight = ((MainActivity) mActivity).isLight();
        ll_menu.setBackgroundColor(getResources().getColor(isLight ? R.color.light_menu_header : R.color.dark_menu_header));
        tv_login.setTextColor(getResources().getColor(isLight ? R.color.light_menu_header_tv : R.color.dark_menu_header_tv));
        tv_backup.setTextColor(getResources().getColor(isLight ? R.color.light_menu_header_tv : R.color.dark_menu_header_tv));
        tv_download.setTextColor(getResources().getColor(isLight ? R.color.light_menu_header_tv : R.color.dark_menu_header_tv));
        tv_main.setBackgroundColor(getResources().getColor(isLight ? R.color.light_menu_index_background : R.color.dark_menu_index_background));
        //lv_item.setBackgroundColor(getResources().getColor(isLight ? R.color.light_menu_listview_background : R.color.dark_menu_listview_background));
        //mAdapter.notifyDataSetChanged();
    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 隐藏弹出窗口
            menuWindow.dismiss();

            switch (v.getId()) {
                case R.id.takePhotoBtn:// 拍照
                    Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //下面这句指定调用相机拍照后的照片存储的路径
                    takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
                    startActivityForResult(takeIntent, REQUESTCODE_TAKE);
                    break;
                case R.id.pickPhotoBtn:// 相册选择图片
                    pickPhoto();
                    break;
                case R.id.cancelBtn:// 取消
                    break;
                default:
                    break;
            }
        }
    };

    /***
     * 从相册中取图片
     */
    private void pickPhoto() {
        Intent intent = new Intent(Kpplication.getContext(), GridViewActivity.class);
        intent.putExtra("REQUESTCODE_PICK", REQUESTCODE_PICK);
        startActivityForResult(intent, REQUESTCODE_PICK);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUESTCODE_PICK:// 直接从相册获取
                try {
                    // System.out.println("我的天"+String.valueOf(data.getCharSequenceArrayListExtra("data_return").get(0)));
                    //startPhotoZoom(Uri.fromFile(new File(data.getStringExtra("data_return"))));
                    Intent intent = new Intent(mActivity, CropActivity.class);
                    intent.putExtra("url", data.getStringExtra("data_return"));
                    startActivityForResult(intent, REQUESTCODE_CUTTING);
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
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        String path = picdata.getStringExtra("path");
        urlpath = path;
        if (path != null) {
            // 取得SDCard图片路径做显示
            Bitmap photo = BitmapFactory.decodeFile(path);
            Drawable drawable = new BitmapDrawable(null, photo);
            circleImg.setImageDrawable(drawable);
            UploadHeader();
        }
    }


    private void UploadHeader() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                HttpConnectionUtils utils = null;
                HashMap<String, String> textParams = new HashMap<String, String>();
                HashMap<String, Bitmap> fileparams = new HashMap<String, Bitmap>();

                try {
                    utils = new HttpConnectionUtils(Config.UploadHeader);
                    connection = utils.GetConnection("POST", "", Config.cookie);
                    connection.setDoInput(true);
                    connection.setUseCaches(false); // 不允许使用缓存
                    connection.setRequestProperty("Charset", "utf-8"); // 设置编码
                    connection.setRequestProperty("connection", "keep-alive");
                    connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + UploadUtils.BOUNDARY);

                    //要上传的文字数据
                    textParams.put("id", String.valueOf(Config.id));

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
                        ImageLoader imageLoader = ImageLoader.getInstance();
                        //imageLoader.clearDiskCache();
                        //imageLoader.clearMemoryCache();
                        Login.loginWithToken();
                    } else {
                        //handler.sendEmptyMessage(1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //handler.sendEmptyMessage(0);// 执行耗时的方法之后发送消给handler
            }
        }).start();


    }


}
