package com.jianhua.reg.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.WindowManager;

import com.jianhua.reg.base.app.MyApplication;
import com.jianhua.reg.dialog.LoadingDialog;
import com.jianhua.reg.utils.MLog;

import java.lang.ref.WeakReference;
import java.util.List;


/**
 * actvitiy基类
 * SupportActivity 支持fragment的操作
 * 继承了 libs的拍照功能，用于拍照使用
 */

public class BaseFragmentActivity extends FragmentActivity {
    //自定义弹窗 加载
    public LoadingDialog loadingDialog;
    public final Handler handler = new WeakRefHandler(this);
    //RxBus 用于取消订阅
    //    private CompositeSubscription mCompositeSubscription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        // 避免fragment切换时黑屏
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void init() {
        //弹窗显示登录状态
        loadingDialog = new LoadingDialog(BaseFragmentActivity.this);
        loadingDialog.setCanceledOnTouchOutside(false);//弹窗之外触摸无效
        loadingDialog.setCancelable(true);//true:可以按返回键back取消


        //将act保存,退出使用
        MyApplication.getInstance().addActOfAll(this);
    }

    /**
     * 用于处理 fragment多层嵌套的无返回处理
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @SuppressLint("RestrictedApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentManager fragmentManager = getSupportFragmentManager();
        for (int index = 0; index < fragmentManager.getFragments().size(); index++) {
            Fragment fragment = fragmentManager.getFragments().get(index); //找到第一层Fragment
            if (fragment == null)
                MLog.e("Activity result no fragment exists for index: 0x" + Integer.toHexString(requestCode));
            else
                handleResult(fragment, requestCode, resultCode, data);
        }
    }

    /**
     * 递归调用，对所有的子Fragment生效
     *
     * @param fragment
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @SuppressLint("RestrictedApi")
    private void handleResult(Fragment fragment, int requestCode, int resultCode, Intent data) {
        fragment.onActivityResult(requestCode, resultCode, data);//调用每个Fragment的onActivityResult
        List<Fragment> childFragment = fragment.getChildFragmentManager().getFragments(); //找到第二层Fragment
        if (childFragment != null)
            for (Fragment f : childFragment)
                if (f != null) {
                    handleResult(f, requestCode, resultCode, data);
                }
        if (childFragment == null)
            MLog.e("BaseActivity");
    }

    /**
     * act页面跳转简化操作
     *
     * @param newClass
     */
    public void startActivity(Class<?> newClass) {
        Intent intent = new Intent(this, newClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    // (2)
    public void startActivity(Class<?> newClass, Bundle extras) {
        Intent intent = new Intent(this, newClass);
        intent.putExtras(extras);
        //退出多个Activity的程序
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * =================================================
     * ================设置异步handler操作 弱引用==================
     * =================================================
     */
    public static final int MESSAGE_TOAST = 1001;

    public class WeakRefHandler extends Handler {

        private final WeakReference<BaseFragmentActivity> mFragmentReference;

        public WeakRefHandler(BaseFragmentActivity activity) {
            mFragmentReference = new WeakReference<BaseFragmentActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final BaseFragmentActivity activity = mFragmentReference.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }

    /**
     * @param msg
     */
    protected void handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_TOAST:
                break;
            default:
                break;
        }
    }

    //    public void addSubscription(Subscription s) {
    //        if (this.mCompositeSubscription == null) {
    //            this.mCompositeSubscription = new CompositeSubscription();
    //        }
    //        this.mCompositeSubscription.add(s);
    //    }
    //
    //    public void removeSubscription() {
    //        if (this.mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
    //            this.mCompositeSubscription.unsubscribe();
    //        }
    //    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //退出程序的广播
        //		unRegisterExitReceiver();
        MyApplication.getInstance().removeOneActOfAll(this);
        //界面消除时
        loadingDialog.dismiss();
        //取消RxBus的注册
        //        removeSubscription();
    }
}
