package krelve.app.Easy.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import krelve.app.Easy.R;
import krelve.app.Easy.bean.Blog;
import krelve.app.Easy.bean.ConsumptionBean;
import krelve.app.Easy.fragment.FoundFragment;
import krelve.app.Easy.net.HttpConnectionUtils;

/**
 * Created by Me on 2016/2/27.
 */
public class FoundAdapter extends ArrayAdapter {
    private ArrayList arrayList;
    private int Resource;
    private Bitmap b1;
    private Bitmap b2;
    private ViewHolder viewHolder;
    private ListView listView;
    private int tagId;
    private ImageLoader mImageloader;
    private DisplayImageOptions options;

//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            arrayList = (ArrayList) msg.obj;
//            switch (msg.what) {
//
//                case 1: {
//                    ImageView imageView = (ImageView) listView.findViewWithTag((String) arrayList.get(0) + (String) arrayList.get(2));
//                    if (b1 != null&&imageView!=null) {
//                        imageView.setImageBitmap(b1);
//                    }
//                    break;
//                }
//                case 2: {
//                    ImageView imageView = (ImageView) listView.findViewWithTag((String) arrayList.get(0) + (String) arrayList.get(2));
//                    ImageView imageView1 = (ImageView) listView.findViewWithTag((String) arrayList.get(1));
//                    if (b1 != null && b2 != null&&imageView!=null&&imageView1!=null) {
//                        imageView.setImageBitmap(b1);
//                        imageView1.setImageBitmap(b2);
//                    }
//                    break;
//                }
//            }
//        }
//    };


    public FoundAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        Resource = resource;
        mImageloader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        listView = FoundFragment.listView;


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Blog blog = (Blog) getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(Resource, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.foundtile);
            viewHolder.content = (TextView) convertView.findViewById(R.id.foundcontent);
            viewHolder.date = (TextView) convertView.findViewById(R.id.founddate);
            viewHolder.username = (TextView) convertView.findViewById(R.id.foundusername);
            viewHolder.seecount = (TextView) convertView.findViewById(R.id.foundseecount);
            viewHolder.userhead = (ImageView) convertView.findViewById(R.id.foundheader);
            viewHolder.imgeurl = (ImageView) convertView.findViewById(R.id.foundimage);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.seecount.setText("浏览" + String.valueOf(blog.getSeecount()) + "次");
        viewHolder.title.setTextColor(Color.BLACK);
        if(blog.getIslight()==1)
        {
            viewHolder.title.setTextColor(android.graphics.Color.RED);
        }

        if(blog.getTitle().length()>10){
            viewHolder.title.setText(blog.getTitle().substring(0,10)+"...");
        }else{
            viewHolder.title.setText(blog.getTitle());
        }
        if(blog.getContent().length()>30){
            viewHolder.content.setText(blog.getContent().substring(0,30)+"...");
        }else{
            viewHolder.content.setText(blog.getContent());
        }
        //viewHolder.title.setText(blog.getTitle().substring(1,10));
        //viewHolder.content.setText(blog.getContent().substring(1,10));
        viewHolder.date.setText(blog.getTake_date());
        viewHolder.username.setText(blog.getUsername());
        tagId = blog.getBlogsId();
        String head = blog.getUserheader();
        String image = null;
      //  viewHolder.userhead.setTag(head + tagId);
//        System.out.println(head + tagId);
        viewHolder.imgeurl.setVisibility(View.GONE);
        if (!blog.getLimageurls().equals("null")) {
            System.out.println("有BUG的地方" + blog.getLimageurls());
            viewHolder.imgeurl.setVisibility(View.VISIBLE);
            String[] url = blog.getLimageurls().split(",");
            image = url[0];
            System.out.println(image);
            mImageloader.displayImage(image, viewHolder.imgeurl, options);
//            viewHolder.imgeurl.setTag(image);
        }
        if(!head.equals("null"))
        {
            mImageloader.displayImage(head, viewHolder.userhead, options);
        }
        //mImageloader.displayImage(head, viewHolder.userhead, options);
//        arrayList = new ArrayList();
//        arrayList.add(head);
//        arrayList.add(image);
//        arrayList.add(String.valueOf(tagId));
//        //String[] urls = {head,image,};
//
//        SetImage(arrayList);

        return convertView;
    }

    private class ViewHolder {
        TextView title;
        TextView content;
        TextView date;
        TextView username;
        ImageView userhead;
        ImageView imgeurl;
        TextView seecount;

    }


//    synchronized private void SetImage(final ArrayList url) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Message message = handler.obtainMessage();
//                    message.obj = url;
//                    HttpConnectionUtils utils = new HttpConnectionUtils((String) url.get(0));
//                    HttpURLConnection connection = utils.GetConnection("GET", "", null);
//                    InputStream is = connection.getInputStream();
//                    b1 = BitmapFactory.decodeStream(is);
//                    //System.out.println(url.get(1));
//                    if (url.get(1) != null) {
//                        utils.setURL((String) url.get(1));
//                        connection = utils.GetConnection("GET", "", null);
//                        is = connection.getInputStream();
//                        b2 = BitmapFactory.decodeStream(is);
//                        message.what = 2;
//                        handler.sendMessage(message);
//                    } else {
//                        message.what = 1;
//                        handler.sendMessage(message);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        ).start();
//
//
//    }

}
