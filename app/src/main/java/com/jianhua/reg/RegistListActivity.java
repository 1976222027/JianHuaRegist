package com.jianhua.reg;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.jianhua.reg.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sjy on 2018/1/15.
 */

public class RegistListActivity extends BaseActivity {

    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;

    @BindView(R.id.radioWorker)
    RadioButton radioWorker;

    @BindView(R.id.radioStudent)
    RadioButton radioStudent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_list);
        ButterKnife.bind(this);
        radioWorker.setSelected(true);
        radioStudent.setSelected(false);
        initListener();
    }

    private void initListener() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioWorker:
                        radioWorker.setSelected(true);
                        radioStudent.setSelected(false);
                        break;
                    case R.id.radioStudent:
                        radioWorker.setSelected(false);
                        radioStudent.setSelected(true);
                        break;
                }
            }
        });
    }
}
