package krelve.app.Easy.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.Inflater;

import krelve.app.Easy.R;
import krelve.app.Easy.activity.SendActivity;

/**
 * Created by Me on 2016/3/13 0013.
 */
public class SimpleGridAdapter extends ArrayAdapter{
    ImageView imageView;
    private int Resource;
    private LinkedList arrayList;
    public static LinkedList<ByteArrayInputStream> Upload;
    public SimpleGridAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        Resource = resource;
        arrayList = (LinkedList) objects;
        arrayList.addFirst("null");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(getContext()).inflate(Resource,null);
        imageView = (ImageView) convertView.findViewById(R.id.imageview);
        if(position==0){
            imageView.setBackgroundResource(R.drawable.ic_add_pic);
            convertView.setLayoutParams(new GridView.LayoutParams(ReadAdapter.dip2px(getContext(), 50), ReadAdapter.dip2px(getContext(), 50)));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SendActivity.sendActivity.Selected();
                }
            });

        }else{
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile((String) arrayList.get(position),options);
            options.inSampleSize = options.outHeight/200>options.outWidth/200?options.outHeight/200:
                    options.outWidth/200;
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile((String) arrayList.get(position), options);
            //Upload.add(bitmap.byte)
            imageView.setImageBitmap(bitmap);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            convertView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ReadAdapter.dip2px(getContext(),100)));
        }

        return convertView;
    }
}
