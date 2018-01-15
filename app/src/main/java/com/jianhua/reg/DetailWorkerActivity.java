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
 * 职工详情
 */
public class DetailWorkerActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.tv_right)
    TextView tv_right;

    @BindView(R.id.spinner)
    MaterialSpinner spinner;

    @BindView(R.id.tv_student)
    TextView tv_student;

    @BindView(R.id.tv_worker)
    TextView tv_worker;

    @BindView(R.id.img_pic)
    ImageView img_pic;

    private String[] typeArrays;
    private int defaultType = 0;
    private boolean isWorker = true;//默认是worker
    private boolean commitState = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_reg);
        ButterKnife.bind(this);
        initSpinner();
        initMyView();
    }

    private void initMyView() {
        //默认员工
        tv_worker.setSelected(true);
        tv_title.setText(getResources().getString(R.string.reg_title));
        tv_right.setText(getResources().getString(R.string.reg_reset));
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
