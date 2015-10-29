package com.nckuesnclab.daniel.lockapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nckuesnclab.daniel.lockapp.R;

/**
 * Created by Daniel Wang on 2015/10/26.
 */
public class GridItem extends LinearLayout implements Checkable {

    private Context context;
    private boolean mCheck;
    private LinearLayout linearLayout;
    private ImageView imageView;
    private TextView textView;


    public GridItem(Context context) {
        super(context);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.grid_list, this);
        linearLayout = (LinearLayout)this.findViewById(R.id.linearLayout);
        imageView = (ImageView)this.findViewById(R.id.imageView);
        textView = (TextView)this.findViewById(R.id.textView);
    }

    @Override
    public void setChecked(boolean checked) {
        mCheck = checked;
        if(mCheck){
            linearLayout.setBackgroundColor(Color.argb(255,255,244,140));
        }else{
            linearLayout.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public boolean isChecked() {
        return mCheck;
    }

    @Override
    public void toggle() {
        this.setChecked(!mCheck);
    }

    public void setTextView(String s){
        textView.setText(s);
        textView.setTextColor(Color.BLACK);
    }

    public void setImageView(Drawable drawable){
        imageView.setImageDrawable(drawable);
    }
}
