package com.jianhua.reg;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegistFailActivity extends AppCompatActivity {
    @BindView(R.id.tv_title)
    TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_reg_failed);
        ButterKnife.bind(this);
        initMyView();
    }

    private void initMyView() {
        //默认员工
        tv_title.setText(getResources().getString(R.string.reg_fail_title));
    }

    @OnClick({R.id.btn_back})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_back:
                this.finish();
                break;

        }
    }
}
