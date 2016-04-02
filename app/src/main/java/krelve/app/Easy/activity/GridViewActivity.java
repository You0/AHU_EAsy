package krelve.app.Easy.activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import krelve.app.Easy.R;
import krelve.app.Easy.adapter.GridViewAdapter;
import krelve.app.Easy.bean.ImageFloder;

public class GridViewActivity extends AppCompatActivity implements ListImageDirPopupWindow.OnImageDirSelected {
    private ProgressDialog mProgressDialog;
    private ImageView mImageView;
    private ArrayList<ImageFloder> imageFloders;
    private ListImageDirPopupWindow mListImageDirPopupWindow;
    private int mScreenHeight;
    private RelativeLayout mBottomLy;
    private TextView mChooseDir;
    private TextView mImageCount;
    public static GridViewActivity gridViewActivity;
    public static int tag = 1;
    private TextView ok;

    //储存文件夹中的图片数量
    private int MpicSize;

    //图片数量最多的文件夹
    private File maxSizeDir;

    //所有的图片(图片数量最多的文件夹里的所有图片)
    private List<String> Images;

    private GridView gridView;
    private ListAdapter adapter;

    //临时的辅助类，防止同一文件夹被多次扫描
    private HashSet<String> dirPaths = new HashSet<String>();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mProgressDialog.dismiss();
            // 为View绑定数据
            data2View();
            // 初始化展示文件夹的popupWindw
            initListDirPopupWindw();
        }
    };

    /**
     * 为View绑定数据
     */
    private void data2View() {
        if (maxSizeDir == null) {
            Toast.makeText(getApplicationContext(), "擦，一张图片没扫描到",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Images = Arrays.asList(maxSizeDir.list());
        /**
         * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
         */
        adapter = new GridViewAdapter(getApplicationContext(),
                R.layout.grid_item, Images, maxSizeDir.getAbsolutePath());
        gridView.setAdapter(adapter);
        mImageCount.setText(MpicSize + "张");
    }


    /**
     * 初始化View
     */
    private void initView() {
        gridView = (GridView) findViewById(R.id.id_gridView);
        mChooseDir = (TextView) findViewById(R.id.id_choose_dir);
        mImageCount = (TextView) findViewById(R.id.id_total_count);
        ok = (TextView) findViewById(R.id.ok);
        mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);

    }

    private void initEvent() {
        /**
         * 为底部的布局设置点击事件，弹出popupWindow
         */
        mBottomLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListImageDirPopupWindow
                        .setAnimationStyle(R.style.PopupAnimation);
                mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);

//                mListImageDirPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
//                //mListImageDirPopupWindow.update(0,0,300,200);

                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = .3f;
                getWindow().setAttributes(lp);
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                ArrayList SendBackArray = new ArrayList();
                SendBackArray.addAll(GridViewAdapter.mSelectedImage);
                intent.putCharSequenceArrayListExtra("data_return",SendBackArray );
                //intent.putExtra(, (String) GridViewAdapter.mSelectedImage.get(0));
                //intent.putCharSequenceArrayListExtra("data_return", GridViewAdapter.mSelectedImage);
                setResult(RESULT_OK, intent);
                //GridViewAdapter.mSelectedImage.clear();
                finish();
            }
        });


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gridViewActivity = this;
        setContentView(R.layout.gridview_layout);
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        mScreenHeight = outMetrics.heightPixels;
        imageFloders = new ArrayList<ImageFloder>();
        gridView = (GridView) findViewById(R.id.id_gridView);
        Intent intent = getIntent();
        tag = intent.getIntExtra("REQUESTCODE_PICK", 1);
        initView();
        getImages();
        initEvent();

    }

    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
     */
    private void getImages() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_REMOVED)) {
            Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
            //return;
        }

        // 显示进度条
        mProgressDialog = ProgressDialog.show(this, null, "正在加载...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri ImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver = GridViewActivity.this.getContentResolver();

//                query()方法参数           对应SQL部分                                                   描述
//                uri                   from table_name                      指定查询某个应用程序下的某一张表
//                projection            select column1, column2             指定查询的列名
//                selection             where column = value                指定 where 的约束条件
//                selectionArgs              -                              为 where 中的占位符提供具体的值
//                orderBy               order by column1, column2           指定查询结果的排序方式
                // 只查询jpeg和png的图片
                Cursor cursor = contentResolver.query(ImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED);


                while (cursor.moveToNext()) {
                    // 获取图片的路径
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

                    //获取该图片的父路径名
                    File parentFile = new File(path).getParentFile();
                    String dirPath = parentFile.getAbsolutePath();
                    ImageFloder imageFloder;
                    //利用一个HashSet防止多次扫描同一个文件夹
                    if (dirPaths.contains(dirPath)) {
                        continue;
                    } else {
                        dirPaths.add(dirPath);
                        imageFloder = new ImageFloder();
                        imageFloder.setDir(dirPath);
                        imageFloder.setFirstImagePath(path);
                    }

                    //文件夹下面.jpg文件的数目
                    int picSize = parentFile.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            if (filename.endsWith(".jpg") || filename.endsWith(".png")
                                    || filename.endsWith(".jpeg")|| filename.endsWith(".JPG")
                                    || filename.endsWith(".JPGE")|| filename.endsWith(".PNG")) {
                                return true;
                            }
                            return false;
                        }
                    }).length;
                    imageFloder.setCount(picSize);
                    imageFloders.add(imageFloder);
                    if (picSize > MpicSize) {
                        MpicSize = picSize;
                        maxSizeDir = parentFile;
                    }
                }
                cursor.close();
                //扫描完成，辅助的HashSet也就可以释放内存了
                dirPaths = null;
                // 通知Handler扫描图片完成
                handler.sendEmptyMessage(0x110);
            }
        }).start();
    }

    private void initListDirPopupWindw() {
        //System.out.println(imageFloders.toString());
        mListImageDirPopupWindow = new ListImageDirPopupWindow(
                ViewGroup.LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
                imageFloders, LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.list_dir, null));

        mListImageDirPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        // 设置选择文件夹的回调
        mListImageDirPopupWindow.setOnImageDirSelected(GridViewActivity.this);
    }

    @Override
    public void selected(ImageFloder floder) {

        maxSizeDir = new File(floder.getDir());
        Images = Arrays.asList(maxSizeDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (filename.endsWith(".jpg") || filename.endsWith(".png")
                        || filename.endsWith(".jpeg")|| filename.endsWith(".JPG")
                        || filename.endsWith(".JPGE")|| filename.endsWith(".PNG"))
                    return true;
                return false;
            }
        }));
        /**
         * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
         */
        adapter = new GridViewAdapter(getApplicationContext(),
                R.layout.grid_item, Images, maxSizeDir.getAbsolutePath());
        gridView.setAdapter(adapter);
        // mAdapter.notifyDataSetChanged();
        mImageCount.setText(floder.getCount() + "张");
        mChooseDir.setText(floder.getName());
        mListImageDirPopupWindow.dismiss();

    }





    public void Done() {
        Intent intent = new Intent();
        intent.putExtra("data_return", (String) GridViewAdapter.mSelectedImage.get(0));
        //intent.putCharSequenceArrayListExtra("data_return", GridViewAdapter.mSelectedImage);
        setResult(RESULT_OK, intent);
        GridViewAdapter.mSelectedImage.clear();
        finish();
    }


}
