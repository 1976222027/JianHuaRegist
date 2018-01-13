package com.jianhua.reg;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jianhua.reg.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {
    @BindView(R.id.btn_login)
    Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_login)
    public void onClicks(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                startActivity(MainActivity.class);
                break;
        }
    }
}
