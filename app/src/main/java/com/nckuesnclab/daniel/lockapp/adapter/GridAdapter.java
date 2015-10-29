package com.nckuesnclab.daniel.lockapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.nckuesnclab.daniel.lockapp.model.App;

import java.util.List;

/**
 * Created by Daniel Wang on 2015/10/26.
 */
public class GridAdapter extends BaseAdapter {

    private Context context;
    private List<App> appList;

    public GridAdapter(Context context, List list){
        this.context = context;
        appList = list;
    }

    @Override
    public int getCount() {
        return appList.size();
    }

    @Override
    public Object getItem(int position) {
        return appList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GridItem gridItem;
        if(convertView == null){
            gridItem  = new GridItem(context);
        }else{
            gridItem = (GridItem)convertView;
        }
        gridItem.setTextView(appList.get(position).getName());
        gridItem.setImageView(appList.get(position).getIcon());
        return gridItem;
    }
}
