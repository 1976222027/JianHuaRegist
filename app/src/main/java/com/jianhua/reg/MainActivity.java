package com.jianhua.reg;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jianhua.reg.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {
    @BindView(R.id.btn_photo)
    Button btn_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_photo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_photo:
                startActivity(RegistActivity.class);
                break;
        }
    }

}
