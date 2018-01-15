package com.jianhua.reg;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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
 * 职工详情
 */
public class DetailWorkerActivity extends BaseActivity {


    //分组
    @BindView(R.id.spinner)
    MaterialSpinner spinner;

    //身份
    @BindView(R.id.tv_student)
    TextView tv_student;

    @BindView(R.id.tv_worker)
    TextView tv_worker;

    // 门禁
    @BindView(R.id.tv_breakfast_right)
    TextView tv_breakfast_right;

    @BindView(R.id.tv_breakfast_wrong)
    TextView tv_breakfast_wrong;
    // 食堂
    @BindView(R.id.tv_lunch_right)
    TextView tv_lunch_right;
    @BindView(R.id.tv_lunch_wrong)
    TextView tv_lunch_wrong;

    //姓名
    @BindView(R.id.et_name)
    EditText et_name;

    //手机
    @BindView(R.id.et_phone)
    EditText et_phone;

    private String[] typeArrays;
    private int defaultType = 0;
    private boolean isWorker = true;//默认是worker
    private boolean commitState = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_detail_worker);
        ButterKnife.bind(this);
        initSpinner();
        initMyView();
    }

    //假数据：跳转的值
    private void initMyView() {
        //身份
        tv_worker.setSelected(true);
        tv_student.setSelected(false);
        //门禁
        tv_breakfast_right.setSelected(true);
        tv_breakfast_wrong.setSelected(false);

        //食堂
        tv_lunch_right.setSelected(true);
        tv_lunch_wrong.setSelected(false);

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

    @OnClick({R.id.layout_back, R.id.tv_delete, R.id.tv_save
            , R.id.tv_student, R.id.tv_worker
            , R.id.tv_lunch_right, R.id.tv_lunch_wrong
            , R.id.tv_breakfast_right, R.id.tv_breakfast_wrong})
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
