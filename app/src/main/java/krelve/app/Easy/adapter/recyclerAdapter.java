package krelve.app.Easy.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.LinkedList;

import krelve.app.Easy.R;
import krelve.app.Easy.activity.CircleImg;
import krelve.app.Easy.activity.ImageDisplay;
import krelve.app.Easy.bean.Blog;

/**
 * Created by Me on 2016/3/4.
 */
public class recyclerAdapter extends RecyclerView.Adapter{
    private ArrayList<Blog> arrayList;
    int flag = 0;
    private Context content;
    private ImageLoader mImageloader;
    private DisplayImageOptions options;
    private ArrayList list_save_url = new ArrayList();
    private LinkedList l = new LinkedList();
    private  int tempImage;
    public LinearLayout ll;



    public recyclerAdapter(ArrayList<Blog> arrayList)
    {
        this.arrayList = arrayList;
        mImageloader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
    }
    class ViewHolder extends RecyclerView.ViewHolder
    {
        public View view;
        public LinearLayout theLayoutTosetImage;
        public CircleImg foundheader;
        public TextView foundusername;
        public TextView founddate;
        public LinearLayout addimage;
        public TextView foundcontent;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            addimage = (LinearLayout) view.findViewById(R.id.addimage);
            ll = (LinearLayout) view.findViewById(R.id.addimage);
            theLayoutTosetImage = (LinearLayout) view.findViewById(R.id.theLayoutTosetImage);
            founddate = (TextView) view.findViewById(R.id.founddate);
            foundheader = (CircleImg) view.findViewById(R.id.foundheader);
            foundusername = (TextView) view.findViewById(R.id.foundusername);
            foundcontent = (TextView) view.findViewById(R.id.foundcontent);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        content = parent.getContext();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.read_item,null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        Blog blog = arrayList.get(position);
        viewHolder.foundcontent.setText(blog.getContent());
        viewHolder.founddate.setText(blog.getTake_date());
        viewHolder.foundusername.setText(blog.getUsername());
        viewHolder.addimage.removeAllViews();
        viewHolder.addimage.addView(viewHolder.foundcontent);
        //viewHolder.addimage.setVisibility(View.GONE);
        //viewHolder.addimage = (LinearLayout)content.findViewById(R.id.addimage);
        int tagId = blog.getBlogsId();
        String head = blog.getUserheader();
        int nums = 0;
        if(blog.getReplyid()==0)
        {
            if (!blog.getLimageurls().equals("null") && flag == 0) {
                flag = 1;

                String[] url = blog.getLimageurls().split(",");
                list_save_url = new ArrayList();
                for(int i=0;i<url.length;i++)
                {
                    list_save_url.add(url[i]);
                }

                for (nums = 0; nums < url.length; nums++) {
                    tempImage = nums;
                    final ImageView imageView = new ImageView(content);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    //imageView.setTop(10);
                    layoutParams.setMargins(0, 10,0, 0);
                    imageView.setImageResource(R.drawable.default1);
                    //imageView.setLayoutParams(layoutParams);
                    //view1.setMinimumHeight(10);
                    //View view1 = new View(content);
                    mImageloader.displayImage(url[nums], imageView, options);
                    imageView.setLayoutParams(layoutParams);
                    viewHolder.addimage.addView(imageView);
                   //viewHolder.addimage.setVisibility(View.VISIBLE);
                    imageView.setTag(nums);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(content, ImageDisplay.class);
                            intent.putExtra("temp", (Integer)imageView.getTag());
                            intent.putStringArrayListExtra("imageUrl", list_save_url);

                            content.startActivity(intent);
                        }
                    });

                    //viewHolder.addimage.addView(view1);
                    System.out.println(url[nums]);

                    //height += imageView.getHeight();
                    //System.out.println("这个是height的高度"+height);
                }
            }
        }

        //viewHolder.theLayoutTosetImage.setMinimumHeight(dip2px(content,600*nums));
        mImageloader.displayImage(head, viewHolder.foundheader, options);


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }



}
