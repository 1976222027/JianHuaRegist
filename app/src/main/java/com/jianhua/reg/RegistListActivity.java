package com.jianhua.reg;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.jianhua.reg.adapter.RegListFragmentAdapter;
import com.jianhua.reg.base.BaseFragmentActivity;
import com.jianhua.reg.widget.NoScrollViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 支持点击切换 和滑动切换
 */

public class RegistListActivity extends BaseFragmentActivity  {

    @BindView(R.id.layout_right)
    RelativeLayout layout_right;

    @BindView(R.id.layout_back)
    RelativeLayout layout_back;

    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;

    @BindView(R.id.radioWorker)
    RadioButton radioWorker;

    @BindView(R.id.radioStudent)
    RadioButton radioStudent;

    @BindView(R.id.viewPager)
    NoScrollViewPager viewPager;

    RegListFragmentAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_list);
        ButterKnife.bind(this);
        radioWorker.setSelected(true);
        radioStudent.setSelected(false);
        initFragment();
        initListener();
    }

    private void initFragment() {
        adapter = new RegListFragmentAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);
    }

    private void initListener() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioWorker:
                        radioWorker.setSelected(true);
                        radioStudent.setSelected(false);
                        //ViewPager点击切换
                        viewPager.setCurrentItem(0, false);
                        break;
                    case R.id.radioStudent:
                        radioWorker.setSelected(false);
                        radioStudent.setSelected(true);
                        //ViewPager点击切换
                        viewPager.setCurrentItem(1, false);
                        break;
                }
            }
        });
    }

    @OnClick({R.id.layout_right, R.id.layout_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_right:
                startActivity(MainActivity.class);
                break;
            case R.id.layout_back:
                this.finish();
                break;
        }
    }
}
