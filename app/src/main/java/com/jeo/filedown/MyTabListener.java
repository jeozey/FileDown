package com.jeo.filedown;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;

/**
 * Created by 志文 on 2015/10/29 0029.
 */
public class MyTabListener<T extends Fragment> implements ActionBar.TabListener {
    private Fragment fragment;
    private Activity mActivity;
    private final Class<T> mClass;

    public MyTabListener(Activity act, Class<T> clz) {
        mActivity = act;
        mClass = clz;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        if (fragment == null) {
            fragment = Fragment.instantiate(mActivity, mClass.getName());
            ft.add(R.id.mainContent, fragment, null);
        }
        ft.attach(fragment);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        if (fragment != null) {
            ft.detach(fragment);
        }
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}
