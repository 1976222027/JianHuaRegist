package com.jianhua.reg;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jianhua.reg.base.BaseActivity;
import com.jianhua.reg.utils.MLog;
import com.jianhua.reg.utils.SPUtil;
import com.jianhua.reg.utils.ToastUtil;
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
    TextView tv_breakfast_wrong;
    //午餐
    @BindView(R.id.tv_lunch_right)
    TextView tv_lunch_right;

    @BindView(R.id.tv_lunch_wrong)
    TextView tv_lunch_wrong;
    //晚餐
    @BindView(R.id.tv_dinner_right)
    TextView tv_dinner_right;

    @BindView(R.id.tv_dinner_wrong)
    TextView tv_dinner_wrong;

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
        //身份
        tv_worker.setSelected(true);
        tv_student.setSelected(false);
        //早餐
        tv_breakfast_right.setSelected(true);
        tv_breakfast_wrong.setSelected(false);

        //午餐
        tv_lunch_right.setSelected(true);
        tv_lunch_wrong.setSelected(false);

        //晚餐
        tv_dinner_right.setSelected(true);
        tv_dinner_wrong.setSelected(false);

        et_name.setText("李佳欣");
        et_phone.setText("18210199639");
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


    @OnClick({
            R.id.layout_back, R.id.tv_delete, R.id.tv_save,
            R.id.tv_worker, R.id.tv_student,
            R.id.tv_breakfast_right, R.id.tv_breakfast_wrong,
            R.id.tv_lunch_right, R.id.tv_lunch_wrong,
            R.id.tv_dinner_right, R.id.tv_dinner_wrong})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.layout_back:
                this.finish();
                break;
            case R.id.tv_delete:
                ToastUtil.ToastShort(this, "已删除");
                this.finish();
                break;
            case R.id.tv_save:
                ToastUtil.ToastShort(this, "保存成功");
                this.finish();
                break;
            case R.id.tv_worker:
                tv_student.setSelected(false);
                tv_worker.setSelected(true);
                isWorker = true;
                break;
            case R.id.tv_student:
                tv_student.setSelected(true);
                tv_worker.setSelected(false);
                isWorker = true;
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
