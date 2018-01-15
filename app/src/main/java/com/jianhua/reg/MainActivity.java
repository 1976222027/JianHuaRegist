package com.jianhua.reg;

import android.os.Bundle;
import android.view.View;

import com.jianhua.reg.base.BaseActivity;
import com.jianhua.reg.camera2.Camera2BasicFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        ButterKnife.bind(this);
        //启动fragment的拍照界面
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.layout_content, Camera2BasicFragment.newInstance(), "cameraFragment")
                .commit();
    }

    @OnClick({R.id.layout_back, R.id.layout_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                startActivity(LoginActivity.class);
                break;
            case R.id.layout_right:
                startActivity(RegistListActivity.class);
                break;
        }
    }

}
