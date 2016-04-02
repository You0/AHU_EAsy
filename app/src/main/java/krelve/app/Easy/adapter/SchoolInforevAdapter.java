package krelve.app.Easy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import krelve.app.Easy.adapter.MyItemClickListener;
import java.util.ArrayList;

import krelve.app.Easy.R;
import krelve.app.Easy.bean.SchoolInfoBean;

/**
 * Created by Me on 2016/3/9 0009.
 */
public class SchoolInforevAdapter extends RecyclerView.Adapter {
    private Context context;
    private ArrayList<SchoolInfoBean> arrayList;
    public TextView title;
    public TextView date;
    public MyItemClickListener mItemClickListener;




    public SchoolInforevAdapter(ArrayList<SchoolInfoBean> arrayList)
    {
        this.arrayList = arrayList;

    }

    public void setOnItemClickListener(MyItemClickListener listener){
        this.mItemClickListener = listener;
    }



    class ViewHolder extends RecyclerView.ViewHolder{
        public View view;
        public TextView title;
        public TextView date;
        public String url;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            title = (TextView) itemView.findViewById(R.id.title);
            date = (TextView) itemView.findViewById(R.id.date);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.school_info_item,null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        title = viewHolder.title;
        date = viewHolder.date;
        final int temppostion = position;


        title.setText(arrayList.get(position).getTitle());
        date.setText(arrayList.get(position).getDate());


        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mItemClickListener!=null)
                {
                    mItemClickListener.onItemClick(viewHolder.view,temppostion);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
       return arrayList.size();
    }
}
