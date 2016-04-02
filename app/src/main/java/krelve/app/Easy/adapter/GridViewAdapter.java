package krelve.app.Easy.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import krelve.app.Easy.R;
import krelve.app.Easy.activity.GridViewActivity;
import krelve.app.Easy.util.ImageLoader;

/**
 * Created by Me on 2016/3/10 0010.
 */
public class GridViewAdapter extends ArrayAdapter {
    private int Resource;
    private LinkedList arrayList;
    private ImageLoader imageLoader;
    private String mDirPath;
    private Context context;
    public static ArrayList mSelectedImage = new ArrayList<>();

    public GridViewAdapter(Context context, int resource, List objects, String path) {
        super(context,resource,objects);
        //arrayList = (ArrayList) objects;
        this.context = context;
        arrayList= new LinkedList();

        arrayList.addAll(objects);
//        arrayList.addFirst("null");
//        arrayList.addFirst("null");
        Resource = resource;
        imageLoader = ImageLoader.getInstance(8, ImageLoader.Type.LIFO);
        mDirPath = path;
    }






    public View getView(int position, View convertView, ViewGroup parent) {
        final int innerPosition = position;
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(Resource, null);
            holder.imageView = (ImageView) convertView
                    .findViewById(R.id.id_item_image);

            holder.imageButton = (ImageButton) convertView.findViewById(R.id.id_item_select);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ViewHolder InnerHolder = holder;
        holder.imageView.setImageResource(R.drawable.friends_sends_pictures_no);
        holder.imageButton.setImageResource(R.drawable.picture_unselected);
        //使用Imageloader去加载图片
        //System.out.println("PO"+position);
        //System.out.println(arrayList.size());
//        if(position!=0)
//        {
//            imageLoader.loadImage(mDirPath + "/" + arrayList.get(position),
//                    holder.imageView);
//        }else{
//            return convertView;
//        }

        imageLoader.loadImage(mDirPath + "/" + arrayList.get(position),
                holder.imageView);
        //复用的时候如果不重新设置图片的滤镜，会出现bug
        holder.imageView.setColorFilter(null);
        holder.imageView.setOnClickListener(new View.OnClickListener()
        {
            //选择，则将图片变暗，反之则反之
            @Override
            public void onClick(View v)
            {

                // 已经选择过该图片
                if (mSelectedImage.contains(mDirPath + "/" + arrayList.get(innerPosition)))
                {
                    mSelectedImage.remove(mDirPath + "/" + arrayList.get(innerPosition));
                    InnerHolder.imageButton.setImageResource(R.drawable.picture_unselected);

                    InnerHolder.imageView.setColorFilter(null);
                } else
                // 未选择该图片
                {
                    System.out.println(mDirPath + "/" + arrayList.get(innerPosition));
                    mSelectedImage.add(mDirPath + "/" + arrayList.get(innerPosition));
                    InnerHolder.imageButton.setImageResource(R.drawable.pictures_selected);
                    InnerHolder.imageView.setColorFilter(Color.parseColor("#77000000"));
                }
                if(GridViewActivity.tag == 0)
                {
                    GridViewActivity.gridViewActivity.Done();
                }

            }
        });


        if (mSelectedImage.contains(mDirPath + "/" + arrayList.get(innerPosition)))
        {
            System.out.println(mDirPath + "/" + arrayList.get(innerPosition));
            InnerHolder.imageButton.setImageResource(R.drawable.pictures_selected);
            InnerHolder.imageView.setColorFilter(Color.parseColor("#77000000"));
        }


        return convertView;
    }

    private final class ViewHolder {
        ImageView imageView;
        ImageButton imageButton;

    }
}
