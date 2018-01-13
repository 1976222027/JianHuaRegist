package com.jianhua.reg;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jianhua.reg.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegistStudentSuccessActivity extends BaseActivity {
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
    @BindView(R.id.tv_dinner_right)
    TextView tv_dinner_right;
    @BindView(R.id.tv_dinner_wrong)
    TextView tv_dinner_wrong;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_reg_stu_success);
        ButterKnife.bind(this);
        initMyView();
    }

    private void initMyView() {
        tv_title.setText(getResources().getString(R.string.reg_success_title));
        //默认员工
        //默认选中
        tv_breakfast_right.setSelected(true);
        tv_breakfast_wrong.setSelected(false);
        tv_lunch_right.setSelected(true);
        tv_lunch_wrong.setSelected(false);
        tv_dinner_right.setSelected(true);
        tv_dinner_wrong.setSelected(false);
    }

    @OnClick({R.id.btn_commit, R.id.tv_breakfast_right, R.id.tv_breakfast_wrong
            , R.id.tv_lunch_right, R.id.tv_lunch_wrong, R.id.tv_dinner_right, R.id.tv_dinner_wrong})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_commit:
                this.finish();
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
            case R.id.tv_dinner_right:
                tv_dinner_right.setSelected(true);
                tv_dinner_wrong.setSelected(false);
                break;
            case R.id.tv_dinner_wrong:
                tv_dinner_right.setSelected(false);
                tv_dinner_wrong.setSelected(true);
                break;

        }
    }
}
