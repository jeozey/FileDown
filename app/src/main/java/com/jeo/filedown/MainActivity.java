package com.jeo.filedown;

import android.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView(){
        final ActionBar actionBar = this.getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

        actionBar.addTab(actionBar.newTab().setText("未下载").setTabListener(new MyTabListener<UnDownFragment>(this,UnDownFragment.class)));
        actionBar.addTab(actionBar.newTab().setText("已下载").setTabListener(new MyTabListener<HasDownFragment>(this,HasDownFragment.class)));
    }

}
