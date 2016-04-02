package krelve.app.Easy.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.LinkedList;

import krelve.app.Easy.R;
import krelve.app.Easy.image.MyScaleView;
import krelve.app.Easy.util.FileUtil;

/**
 * Created by Me on 2016/3/17 0017.
 */
public class ImageDisplay extends Activity {
    private TextView now;
    private TextView download;
    private ViewPager vp;
    private ArrayList arrayList;
    private LinkedList linkedList;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private int globalLocal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imagedisplay_layout);
        now = (TextView) findViewById(R.id.now);
        download = (TextView) findViewById(R.id.ok);
        vp = (ViewPager) findViewById(R.id.vp);
        Intent intent = getIntent();
        arrayList = intent.getStringArrayListExtra("imageUrl");

        linkedList = new LinkedList();
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();


        vp.setAdapter(new PagerAdapter() {

            @Override
            public Object instantiateItem(ViewGroup container, final int position) {
                //globalLocal = position;
                MyScaleView myScaleView = new MyScaleView(getApplicationContext());
                imageLoader.displayImage((String) arrayList.get(position), myScaleView, options);
                //myScaleView.setImageBitmap((Bitmap) arrayList.get(position));
                container.addView(myScaleView);
                linkedList.add(myScaleView);


                return myScaleView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                //设置destroy会导致闪退。还不清楚为什么
                //container.removeView((View) linkedList.get(position));
            }

            @Override
            public int getCount() {
                return arrayList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });

        vp.setCurrentItem(intent.getIntExtra("temp",1));

        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                now.setText(vp.getCurrentItem()+1 + "/" + (arrayList.size()));
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = imageLoader.getDiscCache().get((String) arrayList.get(vp.getCurrentItem())).getPath();
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                FileUtil.saveFile(getApplicationContext(), Environment.getExternalStorageDirectory().toString(), SubString(path, -10, -1) + ".jpg", bitmap);
                // System.out.println(path + path.substring(54, 64));
                Toast.makeText(getApplicationContext(), "图片已保存在" + Environment.getExternalStorageDirectory().toString(), Toast.LENGTH_SHORT).show();

            }
        });






    }

    public String SubString(String string,int star,int end)
    {
        int length = string.length();

        if(end<0){
            end = length+end;
        }
        if(star<0){
            star = length+star;
        }


        return string.substring(star,end);

    }

}
