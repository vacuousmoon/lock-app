package com.nckuesnclab.daniel.lockapp.model;

import android.graphics.drawable.Drawable;

/**
 * Created by Daniel Wang on 2015/10/26.
 */
public class App {

    private String name;
    private String packageName;
    private String processName;
    private Drawable icon;

    public App() {

    }

    public App(String name, String packageName, String processName, Drawable icon){
        this.name = name;
        this.packageName = packageName;
        this.processName = processName;
        this.icon = icon;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setPackageName(String packageName){
        this.packageName = packageName;
    }

    public void setProcessName(String processName){
        this.processName = processName;
    }

    public void setIcon(Drawable icon){
        this.icon = icon;
    }

    public String getName(){
        return  name;
    }

    public String getPackageName(){
        return  packageName;
    }

    public String getProcessName(){
        return processName;
    }

    public Drawable getIcon(){
        return icon;
    }
}
