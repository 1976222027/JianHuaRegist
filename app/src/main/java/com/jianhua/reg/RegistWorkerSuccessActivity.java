package com.jianhua.reg;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jianhua.reg.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 职工注册界面
 */
public class RegistWorkerSuccessActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.tv_breakfast_right)
    TextView tv_breakfast_right;
    @BindView(R.id.tv_breakfast_wrong)
    TextView tv_breakfast_wrong;
    @BindView(R.id.tv_lunch_right)
    TextView tv_lunch_right;
    @BindView(R.id.tv_lunch_wrong)
    TextView tv_lunch_wrong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_reg_worker_success);
        ButterKnife.bind(this);
        initMyView();
    }

    private void initMyView() {
        tv_title.setText(getResources().getString(R.string.reg_success_title));
        //默认选中
        tv_breakfast_right.setSelected(true);
        tv_breakfast_wrong.setSelected(false);
        tv_lunch_right.setSelected(true);
        tv_lunch_wrong.setSelected(false);
    }

    @OnClick({R.id.btn_sure, R.id.tv_breakfast_right, R.id.tv_breakfast_wrong, R.id.tv_lunch_right, R.id.tv_lunch_wrong})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sure:
                startActivity(MainActivity.class);
                break;
            case R.id.tv_breakfast_right:
                tv_breakfast_right.setSelected(true);
                tv_breakfast_wrong.setSelected(false);
                break;
            case R.id.tv_breakfast_wrong:
                tv_breakfast_right.setSelected(false);
                tv_breakfast_wrong.setSelected(true);
                break;
            case R.id.tv_lunch_right:
                tv_lunch_right.setSelected(true);
                tv_lunch_wrong.setSelected(false);
                break;
            case R.id.tv_lunch_wrong:
                tv_lunch_right.setSelected(false);
                tv_lunch_wrong.setSelected(true);
                break;

        }
    }
}
