package krelve.app.Easy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import krelve.app.Easy.R;
import krelve.app.Easy.bean.Blog;
import krelve.app.Easy.fragment.FoundFragment;

/**
 * Created by Me on 2016/2/27.
 */
public class ReadAdapter extends ArrayAdapter {
    private ImageLoader mImageloader;
    private DisplayImageOptions options;
    private int Resource;
    private ViewHolder viewHolder;
    private int tagId;
    private int flag = 0;
    private int TotalImageNum = 0;
    private int nums = 0;
    private int height= 0;


    public ReadAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        Resource = resource;
        mImageloader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Blog blog = (Blog) getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(Resource, null);
            viewHolder = new ViewHolder();
            viewHolder.content = (TextView) convertView.findViewById(R.id.foundcontent);
            viewHolder.date = (TextView) convertView.findViewById(R.id.founddate);
            viewHolder.username = (TextView) convertView.findViewById(R.id.foundusername);
            viewHolder.userhead = (ImageView) convertView.findViewById(R.id.foundheader);
            viewHolder.ll = (LinearLayout) convertView.findViewById(R.id.theLayoutTosetImage);
            viewHolder.imgll = (LinearLayout) convertView.findViewById(R.id.addimage);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.content.setText(blog.getContent());
        viewHolder.date.setText(blog.getTake_date());
        viewHolder.username.setText(blog.getUsername());
        tagId = blog.getBlogsId();
        String head = blog.getUserheader();

//        if (blog.getLimageurls().equals("null")) {
//            for (int i = 0; i < viewHolder.imageViews.size(); i++) {
//                viewHolder.imageViews.get(i).setVisibility(View.GONE);
//            }
//        }
//
        if(blog.getReplyid()==0)
        {
            if (!blog.getLimageurls().equals("null") && flag == 0) {
                flag = 1;

                String[] url = blog.getLimageurls().split(",");
                for (nums = 0; nums < url.length; nums++) {
                    View view = new View(getContext());
                    ImageView imageView = new ImageView(getContext());
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,dip2px(getContext(),500));
                    //imageView.setTop(10);

                    imageView.setLayoutParams(layoutParams);
                    view.setMinimumHeight(10);
                    mImageloader.displayImage(url[nums], imageView, options);
                    viewHolder.imgll.addView(imageView);
                    viewHolder.imgll.addView(view);
                    //height += imageView.getHeight();
                    //System.out.println("这个是height的高度"+height);
                }
            }
        }

        viewHolder.ll.setMinimumHeight(dip2px(getContext(),600*nums));
        mImageloader.displayImage(head, viewHolder.userhead, options);
        return convertView;
    }

    private class ViewHolder {
        TextView content;
        TextView date;
        TextView username;
        ImageView userhead;
        LinearLayout ll;
        LinearLayout imgll;
//        ImageView imageView0;
//        ImageView imageView1;
//        ImageView imageView2;
//        ImageView imageView3;
//        ImageView imageView4;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
