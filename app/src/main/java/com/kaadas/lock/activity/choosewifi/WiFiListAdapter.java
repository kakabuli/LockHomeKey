package com.kaadas.lock.activity.choosewifi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.choosecountry.CountrySortModel;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by hushucong
 * on 2020/7/3
 */
public class WiFiListAdapter  extends BaseAdapter {

    private List<WifiBean> mList;
    private LayoutInflater mInflater;

    private Context mContext;

    //通过构造方法将数据源和数据适配器关联起来
    public WiFiListAdapter(Context context, List<WifiBean> list){
        mList = list;
        mInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){  //没有缓存的
            convertView = mInflater.inflate(R.layout.activity_wifi_list_item_content,null);
        }
        ImageView imageView = (ImageView)convertView.findViewById(R.id.item_wifi_image);
        TextView wifiName = (TextView) convertView.findViewById(R.id.tv_wifi_name);
        WifiBean wifiBean = mList.get(position);
        imageView.setImageResource(wifiBean.ImageId);
        wifiName.setText(wifiBean.name);

        return convertView;
    }
}

