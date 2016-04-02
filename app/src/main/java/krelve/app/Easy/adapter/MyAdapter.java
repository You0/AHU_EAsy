package krelve.app.Easy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import krelve.app.Easy.R;


/**
 * Created by 11092 on 2016/2/7.
 */
public class MyAdapter extends ArrayAdapter {
    private int resourceId;

    public MyAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        resourceId =resource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String text = (String)getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        TextView textView = (TextView) view.findViewById(R.id.mTextView);
        textView.setText(text);
        return view;
    }
}
