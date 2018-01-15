package com.jianhua.reg;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jianhua.reg.base.BaseActivity;
import com.jianhua.reg.utils.MLog;
import com.jianhua.reg.utils.SPUtil;
import com.jianhua.spinner_lib.MaterialSpinner;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 学生详情
 */
public class DetailStudentActivity extends BaseActivity {


    //分组
    @BindView(R.id.spinner)
    MaterialSpinner spinner;

    //    身份
    @BindView(R.id.tv_student)
    TextView tv_student;

    @BindView(R.id.tv_worker)
    TextView tv_worker;

    //姓名
    @BindView(R.id.et_name)
    TextView et_name;
    //手机
    @BindView(R.id.et_phone)
    TextView et_phone;

    //早餐
    @BindView(R.id.tv_breakfast_right)
    TextView tv_breakfast_right;

    @BindView(R.id.tv_breakfast_wrong)
    ImageView tv_breakfast_wrong;
    //午餐
    @BindView(R.id.tv_lunch_right)
    TextView tv_lunch_right;

    @BindView(R.id.tv_lunch_wrong)
    ImageView tv_lunch_wrong;
    //晚餐
    @BindView(R.id.tv_dinner_right)
    TextView tv_dinner_right;

    @BindView(R.id.tv_dinner_wrong)
    ImageView tv_dinner_wrong;

    private String[] typeArrays;
    private int defaultType = 0;
    private boolean isWorker = true;//默认是worker
    private boolean commitState = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_detail_stu);
        ButterKnife.bind(this);
        initSpinner();
        initMyView();
    }

    private void initMyView() {
        //默认员工
        tv_worker.setSelected(true);
    }

    private void initSpinner() {
        //数据源位置
        typeArrays = getResources().getStringArray(R.array.identify_types);
        spinner.setItems(typeArrays);
        spinner.setSelectedIndex(defaultType);//默认选中
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                defaultType = position;
                //保存选中的证件类型
                SPUtil.setType(defaultType);
            }
        });

        spinner.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {

            @Override
            public void onNothingSelected(MaterialSpinner spinner) {
                defaultType = spinner.getSelectedIndex();
                MLog.d("没有选类型defaultType=" + defaultType);
                //保存选中的证件类型
                SPUtil.setType(defaultType);
            }
        });
    }


    @OnClick({R.id.tv_student, R.id.tv_worker, R.id.btn_commit, R.id.tv_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_student:
                tv_student.setSelected(true);
                tv_worker.setSelected(false);
                isWorker = false;
                break;
            case R.id.tv_right:
                startActivity(MainActivity.class);
                break;
            case R.id.tv_worker:
                tv_student.setSelected(false);
                tv_worker.setSelected(true);
                isWorker = true;
                break;
            case R.id.btn_commit:
                if (commitState) {
                    startActivity(RegistFailActivity.class);
                    commitState = false;
                } else {
                    if (isWorker) {
                        startActivity(RegistWorkerSuccessActivity.class);
                    } else {
                        startActivity(RegistStudentSuccessActivity.class);
                    }
                    commitState = true;
                }
                break;

        }
    }
}
