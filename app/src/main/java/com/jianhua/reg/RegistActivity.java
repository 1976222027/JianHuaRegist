package com.jianhua.reg;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jianhua.reg.utils.MLog;
import com.jianhua.reg.utils.SPUtil;
import com.jianhua.spinner_lib.MaterialSpinner;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegistActivity extends AppCompatActivity {
    @BindView(R.id.spinner)
    MaterialSpinner spinner;

    private String[] typeArrays;
    private int defaultType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_reg);
        ButterKnife.bind(this);
        initSpinner();
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
}
