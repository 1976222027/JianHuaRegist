package com.jianhua.reg.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.jianhua.reg.RegStudentFragment;
import com.jianhua.reg.RegWorkerFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sjy on 2018/1/15.
 */

public class RegListFragmentAdapter extends FragmentPagerAdapter {
    protected List<Fragment> fragments = new ArrayList<>();

    public RegListFragmentAdapter(FragmentManager fm) {
        super(fm);
        fragments.clear();
        fragments.add(RegWorkerFragment.newInstance());
        fragments.add(RegStudentFragment.newInstance());
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
