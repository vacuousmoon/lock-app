package com.nckuesnclab.daniel.lockapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nckuesnclab.daniel.lockapp.R;
import com.nckuesnclab.daniel.lockapp.model.App;

import java.util.List;

/**
 * Created by Daniel Wang on 2015/10/26.
 */
public class AppAdapter extends ArrayAdapter{

    private Context context;
    private List<App> appList;

    public AppAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        this.context = context;
        this.appList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listView = View.inflate(context, R.layout.grid_list, null);
        if(position < appList.size()){
            App app = appList.get(position);
            ImageView imageView = (ImageView)listView.findViewById(R.id.imageView);
            TextView textView = (TextView)listView.findViewById(R.id.textView);
            textView.setText(app.getName());
            imageView.setImageDrawable(app.getIcon());
        }
        return listView;
    }
}
