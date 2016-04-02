package krelve.app.Easy.activity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;



import java.util.List;

import krelve.app.Easy.R;
import krelve.app.Easy.adapter.PopListViewAdapter;
import krelve.app.Easy.bean.ImageFloder;

/**
 * Created by Me on 2016/3/12 0012.
 */
public class ListImageDirPopupWindow extends BasePopupWindowForListView<ImageFloder>{
    private ListView mListDir;

    public ListImageDirPopupWindow(int width, int height, List<ImageFloder> datas,
                                   View convertView)
    {
        super(convertView, width, height, true, datas);
    }


    @Override
    protected void beforeInitWeNeedSomeParams(Object... params) {

    }

    @Override
    public void initViews() {
        mListDir = (ListView) findViewById(R.id.id_list_dir);
        mListDir.setAdapter(new PopListViewAdapter(context,R.layout.list_dir_item,mDatas));
    }

    public interface OnImageDirSelected
    {
        void selected(ImageFloder floder);
    }

    private OnImageDirSelected imageDirSelected;

    public void setOnImageDirSelected(OnImageDirSelected mImageDirSelected)
    {
        imageDirSelected = mImageDirSelected;
    }

    @Override
    public void initEvents() {
        mListDir.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id)
            {

                if (imageDirSelected != null)
                {
                    imageDirSelected.selected(mDatas.get(position));
                }
            }
        });

    }

    @Override
    public void init() {

    }
}
