package krelve.app.Easy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import krelve.app.Easy.R;
import krelve.app.Easy.bean.ConsumptionBean;

/**
 * Created by 11092 on 2016/2/18.
 */
public class CardAdapter extends ArrayAdapter {
    private String JnDateTime;//交易时间
    private String TranName;//交易名称
    private String TranAmt;//交易金额
    private String AccAmt;//交易后余额
    private String MercName;//交易场所
    private int Resource;
    public CardAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        Resource = resource;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ConsumptionBean consumptionBean = (ConsumptionBean)getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(Resource, null);

        JnDateTime = consumptionBean.getJnDateTime();
        TranName = consumptionBean.getTranName();
        TranAmt = consumptionBean.getTranAmt();
        AccAmt = consumptionBean.getAccAmt();
        MercName = consumptionBean.getMercName();

        ((TextView)view.findViewById(R.id.JnDateTime)).setText(JnDateTime);
        ((TextView)view.findViewById(R.id.TranName)).setText(TranName);
        ((TextView)view.findViewById(R.id.TranAmt)).setText(TranAmt);
        ((TextView)view.findViewById(R.id.AccAmt)).setText(AccAmt);
        ((TextView)view.findViewById(R.id.MercName)).setText(MercName);
        return view;
    }
}
