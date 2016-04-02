package krelve.app.Easy.activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import krelve.app.Easy.Config;
import krelve.app.Easy.Kpplication;
import krelve.app.Easy.R;
import krelve.app.Easy.adapter.GridViewAdapter;
import krelve.app.Easy.adapter.SimpleGridAdapter;
import krelve.app.Easy.net.HttpConnectionUtils;
import krelve.app.Easy.net.UploadUtils;

/**
 * Created by Me on 2016/3/2.
 */
public class SendActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private ImageView imageView;
    private EditText title;
    private EditText content;
    private SelectPicPopupWindow menuWindow; // 自定义的头像编辑弹出框
    private LinkedList<String> PhotoPaths = new LinkedList<String>();
    private LinearLayout ll;
    private Uri photoUri;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String username;
    private String userhead;
    private int authority;
    private int id;
    private int blogsId;
    private SimpleGridAdapter adapter;
    public static SendActivity sendActivity;

    private static final int TIME_OUT = 10 * 10000000; // 超时时间
    private static final String CHARSET = "utf-8"; // 设置编码
    public static final String SUCCESS = "1";
    public static final String FAILURE = "0";

    /**
     * 使用照相机拍照获取图片
     */
    public static final int SELECT_PIC_BY_TACK_PHOTO = 1;
    /**
     * 使用相册中的图片
     */
    public static final int SELECT_PIC_BY_PICK_PHOTO = 2;
    /**
     * 获取到的图片路径
     */
    private String picPath = "";
    private static ProgressDialog pd;
    private String resultStr = "";    // 服务端返回结果集
    private String imgUrl = Config.SendBlogs;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            pd.dismiss();
            GridViewAdapter.mSelectedImage.clear();
            switch (msg.what) {
                case 0:
                    onBackPressed();
                    Toast.makeText(SendActivity.this, "发布成功！", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(SendActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        imageView = (ImageView) findViewById(R.id.picImg);
        //ll = (LinearLayout) findViewById(R.id.imagelistll);
        title = (EditText) findViewById(R.id.title);
        content = (EditText) findViewById(R.id.content);
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        userhead = sharedPreferences.getString("userhead", "null");
        username = sharedPreferences.getString("username", "null");
        authority = sharedPreferences.getInt("authority", 0);
        id = sharedPreferences.getInt("id", 0);
        sendActivity = this;
        editor = sharedPreferences.edit();


        if (getIntent().getBooleanExtra("reply", false)) {
            TextView textView = (TextView) findViewById(R.id.addimage);
            //imageView.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            content.setLayoutParams(layoutParams);
            blogsId = getIntent().getIntExtra("blogsId",0);

        }


        toolbar.setTitle("发送");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        adapter = new SimpleGridAdapter(SendActivity.this, R.layout.simple_grid_item, PhotoPaths);
        GridView gridView = (GridView) findViewById(R.id.id_gridView);
        gridView.setAdapter(adapter);


        //设置监听
       // imageView.setOnClickListener(this);

    }

    public void Selected()
    {
        menuWindow = new SelectPicPopupWindow(this, itemsOnClick);
        menuWindow.showAtLocation(findViewById(R.id.uploadLayout),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.picImg: {
                // 从页面底部弹出一个窗体，选择拍照还是从相册选择已有图片
                menuWindow = new SelectPicPopupWindow(this, itemsOnClick);
                menuWindow.showAtLocation(findViewById(R.id.uploadLayout),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            }

        }
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 隐藏弹出窗口
            menuWindow.dismiss();

            switch (v.getId()) {
                case R.id.takePhotoBtn:// 拍照
                    takePhoto();
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


    /**
     * 拍照获取图片
     */
    private void takePhoto() {
        // 执行拍照前，应该先判断SD卡是否存在
        String SDState = Environment.getExternalStorageState();
        if (SDState.equals(Environment.MEDIA_MOUNTED)) {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            /***
             * 需要说明一下，以下操作使用照相机拍照，拍照后的图片会存放在相册中的
             * 这里使用的这种方式有一个好处就是获取的图片是拍照后的原图
             * 如果不使用ContentValues存放照片路径的话，拍照后获取的图片为缩略图不清晰
             */
            ContentValues values = new ContentValues();
            photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
        } else {
            Toast.makeText(this, "内存卡不存在", Toast.LENGTH_LONG).show();
        }
    }


    /***
     * 从相册中取图片
     */
    private void pickPhoto() {
        Intent intent = new Intent(Kpplication.getContext(),GridViewActivity.class);
        intent.putExtra("REQUESTCODE_PICK", 1);
        startActivityForResult(intent, SELECT_PIC_BY_PICK_PHOTO);

//        Intent intent = new Intent();
//        // 如果要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
        //startActivityForResult(intent, SELECT_PIC_BY_PICK_PHOTO);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 点击取消按钮
        if (resultCode == RESULT_CANCELED) {
            return;
        }

        // 可以使用同一个方法，这里分开写为了防止以后扩展不同的需求
        switch (requestCode) {
            case SELECT_PIC_BY_PICK_PHOTO:// 如果是直接从相册获取
                doPhoto(requestCode, data);
                break;
            case SELECT_PIC_BY_TACK_PHOTO:// 如果是调用相机拍照时
                doPhoto(requestCode, data);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 选择图片后，获取图片的路径
     *
     * @param requestCode
     * @param data
     */
    private void doPhoto(int requestCode, Intent data) {

        // 从相册取图片，有些手机有异常情况，请注意
        if (requestCode == SELECT_PIC_BY_PICK_PHOTO) {
            if (data == null) {
                Toast.makeText(this, "选择图片文件出错", Toast.LENGTH_LONG).show();
                return;
            }
//            photoUri = data.getData();
//            if (photoUri == null) {
//                Toast.makeText(this, "选择图片文件出错", Toast.LENGTH_LONG).show();
//                return;
//            }
        }

//        String[] pojo = {MediaStore.MediaColumns.DATA};
//        // The method managedQuery() from the type Activity is deprecated
//        //Cursor cursor = managedQuery(photoUri, pojo, null, null, null);
//        Cursor cursor = this.getContentResolver().query(photoUri, pojo, null, null, null);
//        if (cursor != null) {
//            int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
//            cursor.moveToFirst();
//            picPath = cursor.getString(columnIndex);
//
//            // 4.0以上的版本会自动关闭 (4.0--14;; 4.0.3--15)
//            if (Integer.parseInt(Build.VERSION.SDK) < 14) {
//                cursor.close();
//            }
//        }


        BitmapFactory.Options option = new BitmapFactory.Options();
        // 压缩图片:表示缩略图大小为原始图片大小的几分之一，1为原图
        option.inSampleSize = 1;
        // 根据图片的SDCard路径读出Bitmap
        ArrayList arrayList = data.getCharSequenceArrayListExtra("data_return");
        if(PhotoPaths.get(0).equals("null"))
        {
            PhotoPaths.clear();
            PhotoPaths.addFirst("null");
        }

        for(int i=0;i<arrayList.size();i++)
        {
            PhotoPaths.add((String) arrayList.get(i));

//            Bitmap bm = BitmapFactory.decodeFile((String) arrayList.get(i), option);
//            ImageView imageView = new ImageView(this);
//            imageView.setImageBitmap(bm);
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150, 150);
//            imageView.setLayoutParams(layoutParams);
//            //imageView.setImageBitmap(bm);
//            ll.addView(imageView);
        }
        adapter.notifyDataSetChanged();

        // 如果图片符合要求将其上传到服务器
//        if (picPath != null && (picPath.endsWith(".png") ||
//                picPath.endsWith(".PNG") ||
//                picPath.endsWith(".jpg") ||
//                picPath.endsWith(".JPG"))) {
//
//            PhotoPaths.add(picPath);
//            System.out.println(PhotoPaths.get(0));

            // 显示在图片控件上


            //pd = ProgressDialog.show(this, null, "正在上传图片，请稍候...");
            //new Thread(uploadImageRunnable).start();
        }



    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
//        if((Config.authority&(1<<1)) ==0)
//        {
//            Toast.makeText(this,"请先登录！(或您没有权限)",Toast.LENGTH_SHORT).show();
//            return false;
//        }


        imgUrl = Config.SendBlogs;
        if (item.getItemId() == R.id.reply) {
            final String Tile = title.getText().toString();
            final String Content = content.getText().toString();
            if (title.getVisibility() == View.GONE) {
                imgUrl = Config.SendReply;
                if (Content.equals("")) {
                    Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else if (Tile.equals("") || Content.equals("")) {
                Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
                return false;
            }

            pd = ProgressDialog.show(this, null, "正在上传，请稍候...");
            new Thread(new Runnable() {
                @Override
                public void run() {


                    HttpURLConnection connection = null;
                    HttpConnectionUtils utils = null;
                    HashMap<String, String> textParams = new HashMap<String, String>();
                    HashMap<String, Bitmap> fileparams = new HashMap<String, Bitmap>();

                    try {
                        utils = new HttpConnectionUtils(imgUrl);
                        connection = utils.GetConnection("POST", "", Config.cookie);
                        connection.setReadTimeout(TIME_OUT);
                        connection.setConnectTimeout(TIME_OUT);
                        connection.setDoInput(true);
                        connection.setUseCaches(false); // 不允许使用缓存
                        connection.setRequestProperty("Charset", CHARSET); // 设置编码
                        connection.setRequestProperty("connection", "keep-alive");
                        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + UploadUtils.BOUNDARY);

                        textParams.put("id", String.valueOf(id));
                        if (imgUrl == Config.SendBlogs) {
                            textParams.put("userheader", userhead);
                            textParams.put("title", URLEncoder.encode(URLEncoder.encode(Tile, "UTF-8"), "UTF-8"));
                        }else{
                            textParams.put("imageurl", userhead);
                            textParams.put("blogsId",String.valueOf(blogsId));
                        }
                        textParams.put("content", URLEncoder.encode(URLEncoder.encode(Content, "UTF-8"), "UTF-8"));
                        textParams.put("username", URLEncoder.encode(URLEncoder.encode(username, "UTF-8"), "UTF-8"));



                        // 要上传的图片文件
                        for (int i = 1; i < PhotoPaths.size(); i++) {
                            //System.out.println(PhotoPaths.get(i));
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inJustDecodeBounds = true;
                            BitmapFactory.decodeFile(PhotoPaths.get(i), options);
                            System.out.println("高"+options.outHeight/728+"宽radio"+options.outWidth/1024);

                            int radio;
                            if(options.outHeight/options.outWidth > 5){
                                radio = 0;
                            }else{
                                radio = options.outHeight/728 > options.outWidth/1024?options.outHeight/728:
                                        options.outWidth/1024;
                            }


                            System.out.println("the radio"+radio);
                            options.inJustDecodeBounds = false;
                            options.inSampleSize = radio+1;
                            Bitmap bitmap = BitmapFactory.decodeFile(PhotoPaths.get(i),options);
                            //File file = new File(PhotoPaths.get(i));
                            fileparams.put("image" + i, bitmap);
                        }
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
                        if (code == 200) {// 返回的响应码200,是成功
//                                InputStream is = connection.getInputStream();
//                                BufferedReader br = new BufferedReader(new InputStreamReader(is));
//                                String uuid = br.readLine();
//                                editor = Kpplication.getContext().getSharedPreferences("uuid", Kpplication.getContext().MODE_PRIVATE).edit();
//                                editor.putString("uuid",uuid);
//                                editor.commit();

                        } else {
                            handler.sendEmptyMessage(1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(0);// 执行耗时的方法之后发送消给handler
                }
            }).start();
        }

        return super.onOptionsItemSelected(item);
    }
}
