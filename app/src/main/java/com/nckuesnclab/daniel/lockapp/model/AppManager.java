package com.nckuesnclab.daniel.lockapp.model;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel Wang on 2015/10/26.
 */
public class AppManager {

    private static final String LOCK_APP = "lock_app";
    private static final String PASSWORD = "password";
    private static final String MY_PACKAGE_NAME = "com.nckuesnclab.daniel.lockapp";

    private Context context;
    private PackageManager pm;
    private ActivityManager am;
    private SharedPreferences sp;
    private SharedPreferences.Editor edit;
    private AppDB appDB;
    private List<App> appAllList;
    private List<App> appAllPermissionList;
    private String myPackageName;

    public AppManager(Context context){
        this.context = context;
        pm = context.getPackageManager();
        am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        sp = context.getSharedPreferences(LOCK_APP, Context.MODE_PRIVATE);
        edit = sp.edit();
        appDB = new AppDB(context);
        appAllPermissionList = this.getAllPermissionApp();
        appAllList = this.getAllApp();
        myPackageName = context.getPackageName();
        this.isAppInDBStillInstalled();
    }

    public void launchApp(App app){
        Intent intent = pm.getLaunchIntentForPackage(app.getPackageName());
        context.startActivity(intent);
    }

    public List<App> getAllAppNew(){
        return appAllList;
    }

    //get all installed applications in device
    private List<App> getAllApp(){
        List<PackageInfo> packageInfoList = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES);
        List<App> appList = new ArrayList<App>();
        for(int i = 0; i<packageInfoList.size(); i++){
            PackageInfo info = packageInfoList.get(i);
            Intent intent = pm.getLaunchIntentForPackage(info.packageName);
            if(intent != null){
                String name = info.applicationInfo.loadLabel(pm).toString();
                String packageName = info.packageName;
                String processName = info.applicationInfo.processName;
                try {
                    Drawable icon = pm.getApplicationIcon(info.packageName);
                    App app = new App(name, packageName, processName, icon);
                    appList.add(app);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        if(appList.size()==0){
            return null;
        }else{
            return appList;
        }
    }

    public List<App> getALLAppWithoutItself(){
        List<App> appList = appAllList;
        for(int i=0; i<appAllList.size(); i++){
            if(appList.get(i).getPackageName().equals(myPackageName)){
                appList.remove(i);
                break;
            }
        }
        return appList;
    }

    //include it self
    private List<App> getAllPermissionApp(){
        List<App> appList = appDB.getAllPermissionApp();
        if(appList!=null){
            return appList;
        }else{
            return null;
        }
    }

    public List<App> getAllPermissionAppWithoutItself(){
        appAllPermissionList = this.getAllPermissionApp();
        List<App> appList = appAllPermissionList;
        if(appList!=null && appList.size()!=1){
            for(int i=0; i<appList.size(); i++){
                if(appList.get(i).getPackageName().equals(myPackageName)){
                    appList.remove(i);
                    break;
                }
            }
            appAllPermissionList = this.getAllPermissionApp();
            return appList;
        }else{
            appAllPermissionList = this.getAllPermissionApp();
            return null;
        }
    }

    public void addPermissionApp(App app){
        appDB.addPermissionApp(app);
    }

    public void removePermissionApp(App app){
        appDB.removePermissionApp(app);
    }

    //return true if set success, false if password is empty
    public boolean setPassWord(String passwd){
        if(!passwd.isEmpty()){
            edit.putString(PASSWORD, passwd);
            edit.commit();
            return true;
        }else{
            return false;
        }
    }

    //return true if password is right, false if password is wrong
    public boolean checkPassWord(String passwd){
        if(passwd.equals(sp.getString(PASSWORD, null))){
            return true;
        }else{
            return false;
        }
    }

    //check if has password
    public boolean isPassWordEmpty(){
        this.addItSelfPermission();
        String passwd = sp.getString(PASSWORD, null);
        if(passwd!=null){
            return false;
        }else{
            return true;
        }
    }

    public void checkApp(){
        //appAllPermissionList = this.getAllPermissionApp();
        List<RunningAppProcessInfo> allRunningProcess = am.getRunningAppProcesses();
        List<RunningAppProcessInfo> runningProcess = new ArrayList<RunningAppProcessInfo>();
        for(int i = 0; i<allRunningProcess.size(); i++){
            if(allRunningProcess.get(i).importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
                runningProcess.add(allRunningProcess.get(i));
            }
        }
        boolean reset = true;
        for(int i = 0; i<runningProcess.size(); i++){ //or check at least top three process
            if(isProcessInstalledApp(runningProcess.get(i))){
                //Log.i("process", runningProcess.get(i).processName);
                if(isProcessPermission(runningProcess.get(i))){
                     reset = false;
                }
            }
        }
        if(reset){
            Intent resetIntent = pm.getLaunchIntentForPackage(MY_PACKAGE_NAME);
            context.startActivity(resetIntent);
            //detected denied process
        }
    }

    private boolean isAppInstalled(App app){
        boolean isInstalled = false;
        //List<App> appList = getAllApp();
        if(appAllList!=null){
            for(int i = 0; i<appAllList.size(); i++){
                if(app.getPackageName().equals(appAllList.get(i).getPackageName())){
                    isInstalled = true;
                }
            }
        }
        return isInstalled;
    }

    private boolean isProcessInstalledApp(RunningAppProcessInfo info){
        boolean isApp = false;
        //List<App> appList = getAllApp();
        if(appAllList!=null){
            for(int i = 0; i<appAllList.size(); i++){
                if(info.processName.equals(appAllList.get(i).getProcessName())){
                    isApp = true;
                }
            }
        }
        return isApp;
    }

    private boolean isProcessPermission(RunningAppProcessInfo info){
        boolean isPermission = false;
        for(int i = 0; i<appAllPermissionList.size(); i++){
            if(info.processName.equals(appAllPermissionList.get(i).getProcessName())){
                isPermission = true;
            }
        }
        return isPermission;
    }

    private void isAppInDBStillInstalled(){
        if(appAllPermissionList!=null){
            for(int i = 0; i<appAllPermissionList.size(); i++){
                if(!isAppInstalled(appAllPermissionList.get(i))){
                    appDB.removePermissionApp(appAllPermissionList.get(i));
                }
            }
        }
    }

    private void addItSelfPermission(){
        for(int i = 0; i<appAllList.size(); i++){
            if(appAllList.get(i).getPackageName().equals(myPackageName)){
                this.addPermissionApp(appAllList.get(i));
            }
        }
    }
}
