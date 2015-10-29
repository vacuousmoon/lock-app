package com.nckuesnclab.daniel.lockapp.model;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel Wang on 2015/10/26.
 */
public class AppDB {

    private static final String DATABASE_NAME = "lockApp.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "app";
    private static final String PERMISSION_TRUE = "permissionTrue";
    private static final String PERMISSION_FALSE = "permissionFalse";

    private DBHelper mDBHelper;
    private SQLiteDatabase mDatabase;
    private PackageManager pm;

    public AppDB(Context context){
        mDBHelper = new DBHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        mDatabase = mDBHelper.getWritableDatabase();
        pm = context.getPackageManager();
    }

    public void addPermissionApp(App app){
        String[] selectionArgs = {app.getPackageName()};
        Cursor cursor = mDatabase.query(TABLE_NAME, null, "packageName=?", selectionArgs, null, null, null, null);
        ContentValues cv = new ContentValues();
        if(cursor.getCount()>0){
            cv.put("permission", PERMISSION_TRUE);
            mDatabase.update(TABLE_NAME, cv, "packageName=?", selectionArgs);
        }else{
            cv.put("name", app.getName());
            cv.put("packageName", app.getPackageName());
            cv.put("processName", app.getProcessName());
            cv.put("permission", PERMISSION_TRUE);
            mDatabase.insert(TABLE_NAME, null, cv);
        }
    }

    public void removePermissionApp(App app){
        String[] selectionArgs = {app.getPackageName()};
        ContentValues cv = new ContentValues();
        cv.put("permission", PERMISSION_FALSE);
        mDatabase.update(TABLE_NAME, cv, "packageName=?", selectionArgs);
    }

    private App getPermissionApp(Cursor cursor){
        String name = cursor.getString(1);
        String packageName = cursor.getString(2);
        String processName = cursor.getString(3);
        try {
            Drawable icon = pm.getApplicationIcon(packageName);
            App app = new App(name, packageName, processName, icon);
            return app;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Drawable icon = null;
            App app = new App(name, packageName, processName, icon);
            return app;
        }
    }

    public List<App> getAllPermissionApp(){
        List<App> appList = new ArrayList<App>();
        String[] selectionArgs = {PERMISSION_TRUE};
        Cursor cursor = mDatabase.query(TABLE_NAME, null, "permission=?", selectionArgs, null, null, null, null);
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            if(getPermissionApp(cursor)!=null){
                appList.add(getPermissionApp(cursor));
            }
            while(cursor.moveToNext()){
                if(getPermissionApp(cursor)!=null){
                    appList.add(getPermissionApp(cursor));
                }
            }
            cursor.close();
            return appList;
        }else{
            return null;
        }
    }

    public void closeDB(){
        mDatabase.close();
    }
}

class DBHelper extends SQLiteOpenHelper{

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS app" + "(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, packageName TEXT, processName TEXT, permission TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
