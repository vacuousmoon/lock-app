package com.nckuesnclab.daniel.lockapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.nckuesnclab.daniel.lockapp.adapter.GridAdapter;
import com.nckuesnclab.daniel.lockapp.model.App;
import com.nckuesnclab.daniel.lockapp.model.AppManager;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private static CharSequence mTitle;

    private static final int LAUNCH = 0;
    private static final int INAPP = 1;
    private static final int QUIT = 2;
    private static final int RESET = 3;
    private static final int HOME = 1;
    private static final int ADDAPP = 2;
    private static final int REMOVEAPP = 3;
    private static Context context;
    private static Map<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();
    private static AppManager appManager;
    private LockAppService.MyBinder myBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        context = this.getApplicationContext();
        appManager = new AppManager(context);
        if(appManager.isPassWordEmpty()){
            this.setPassWord(LAUNCH);
        }else{
            this.checkPassWord(LAUNCH);
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        switch (position+1){
            case HOME:
            case ADDAPP:
            case REMOVEAPP:
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, PlaceholderFragment.newInstance(position+1))
                        .commit();
                break;
            case 4:
                this.checkPassWord(RESET);//變更密碼
                break;
            case 5:
                this.checkPassWord(QUIT);//離開
                break;
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("test","test");
            myBinder = (LockAppService.MyBinder)service;
            myBinder.setAppManager(appManager);
            myBinder.startCheck();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setPassWord(final int state){
        final EditText editText = new EditText(MainActivity.this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("請設定密碼")
                .setView(editText)
                .setCancelable(false)
                .setPositiveButton("確認",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                                    field.setAccessible(true);
                                    field.set(dialog, false);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                String password = editText.getText().toString();
                                if(appManager.setPassWord(password)){
                                    try {
                                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                                        field.setAccessible(true);
                                        field.set(dialog, true);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    Toast.makeText(MainActivity.this, "密碼不得為空", Toast.LENGTH_SHORT).show();
                                }

                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(state == LAUNCH){
                                    MainActivity.this.finish();
                                }else{
                                    try {
                                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                                        field.setAccessible(true);
                                        field.set(dialog, true);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    mTitle = getString(R.string.title_section1);
                                    MainActivity.this.restoreActionBar();
                                    FragmentManager fragmentManager = getSupportFragmentManager();
                                    fragmentManager.beginTransaction()
                                            .replace(R.id.container, PlaceholderFragment.newInstance(1))
                                            .commit();
                                }
                            }
                        })
                .show();
    }

    public void checkPassWord(final int state){
        Log.i("test", Integer.toString(state));
        final EditText editText = new EditText(MainActivity.this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("請輸入密碼")
                .setView(editText)
                .setCancelable(false)
                .setPositiveButton("確認",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String password = editText.getText().toString();
                                try {
                                    Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                                    field.setAccessible(true);
                                    field.set(dialog, false);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if(!appManager.checkPassWord(password)){
                                    Toast.makeText(MainActivity.this, "密碼錯誤", Toast.LENGTH_SHORT).show();
                                }else{
                                    try {
                                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                                        field.setAccessible(true);
                                        field.set(dialog, true);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if(state == LAUNCH){
                                        Intent startIntent = new Intent(MainActivity.this, LockAppService.class);
                                        context.startService(startIntent);
                                        Intent bindIntent = new Intent(MainActivity.this, LockAppService.class);
                                        MainActivity.this.bindService(bindIntent, connection, Context.BIND_AUTO_CREATE);
                                    }else if(state == QUIT){
                                        myBinder.stopCheck();
                                        MainActivity.this.unbindService(connection);
                                        Intent stopIntent = new Intent(MainActivity.this, LockAppService.class);
                                        context.stopService(stopIntent);
                                        MainActivity.this.finish();
                                    }else if(state == RESET){
                                        MainActivity.this.setPassWord(INAPP);
                                    }
                                }
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(state == LAUNCH){
                                    MainActivity.this.finish();
                                }else{
                                    try {
                                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                                        field.setAccessible(true);
                                        field.set(dialog, true);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    mTitle = getString(R.string.title_section1);
                                    MainActivity.this.restoreActionBar();
                                    FragmentManager fragmentManager = getSupportFragmentManager();
                                    fragmentManager.beginTransaction()
                                            .replace(R.id.container, PlaceholderFragment.newInstance(1))
                                            .commit();
                                }
                            }
                        })
                .show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static int sectionNum;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            sectionNum = sectionNumber;
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            GridView gridView = (GridView)rootView.findViewById(R.id.gridView);
            Button button = (Button)rootView.findViewById(R.id.button);
            GridAdapter gridAdapter;
            switch (sectionNum){
                case HOME:
                    final List<App> appList = appManager.getAllPermissionAppWithoutItself();
                    if(appList != null){
                        gridAdapter = new GridAdapter(context, appList);
                        gridView.setAdapter(gridAdapter);
                        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                App app = appList.get(position);
                                appManager.launchApp(app);
                            }
                        });
                    }
                    button.setVisibility(View.GONE);
                    break;
                case ADDAPP:
                    ((MainActivity)getActivity()).checkPassWord(INAPP);
                    final List<App> appListAll = appManager.getALLAppWithoutItself();
                    for(int i = 0; i < appListAll.size(); i++){
                        mSelectMap.put(i, false);
                    }
                    gridView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    gridAdapter = new GridAdapter(context, appListAll);
                    gridView.setAdapter(gridAdapter);
                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            boolean temp = mSelectMap.get(position);
                            mSelectMap.put(position, !temp);
                        }
                    });
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for(int i = 0; i < appListAll.size(); i++){
                                if(mSelectMap.get(i)){
                                    appManager.addPermissionApp(appListAll.get(i));
                                }
                            }
                            mTitle = getString(R.string.title_section1);
                            ((MainActivity)getActivity()).restoreActionBar();
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.container, PlaceholderFragment.newInstance(1))
                                    .commit();
                        }
                    });
                    break;
                case REMOVEAPP:
                    ((MainActivity)getActivity()).checkPassWord(INAPP);
                    final List<App> appListRemove = appManager.getAllPermissionAppWithoutItself();
                    if(appListRemove != null){
                        for(int i = 0; i < appListRemove.size(); i++){
                            mSelectMap.put(i, false);
                        }
                        gridView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                        gridAdapter = new GridAdapter(context, appListRemove);
                        gridView.setAdapter(gridAdapter);
                        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                boolean temp = mSelectMap.get(position);
                                mSelectMap.put(position, !temp);
                            }
                        });
                    }
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(appListRemove!=null){
                                for(int i = 0; i < appListRemove.size(); i++){
                                    if(mSelectMap.get(i)){
                                        appManager.removePermissionApp(appListRemove.get(i));
                                    }
                                }
                            }
                            mTitle = getString(R.string.title_section1);
                            ((MainActivity)getActivity()).restoreActionBar();
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.container, PlaceholderFragment.newInstance(1))
                                    .commit();
                        }
                    });
                    break;
            }
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
