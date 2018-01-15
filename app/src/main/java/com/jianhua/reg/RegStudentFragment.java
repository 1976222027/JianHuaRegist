package com.jianhua.reg;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jianhua.reg.adapter.WorkerDataAdapter;
import com.jianhua.reg.base.BaseFragment;
import com.jianhua.reg.utils.MLog;
import com.jianhua.reg.utils.SPUtil;
import com.jianhua.spinner_lib.WhiteSpinner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 资讯页根fragment
 */

public class RegStudentFragment extends BaseFragment {
    @BindView(R.id.recylcerView)
    RecyclerView recylcerView;

    @BindView(R.id.spinner)
    WhiteSpinner spinner;

    private Unbinder unbinder;
    private String[] typeArrays;
    private int defaultType = 0;
    private List<String> listData;
    LinearLayoutManager manager;
    WorkerDataAdapter dataAdapter;

    public static RegWorkerFragment newInstance() {
        RegWorkerFragment fragment = new RegWorkerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initSpinner();
        initMyView();
        /**
         * 因为启动时先走lazyLoad()再走onActivityCreated，
         * 所以此处要额外调用lazyLoad(),不然最初不会加载内容
         */
        lazyLoad();
    }

    /**
     * 初始化数据
     */
    private void initMyView() {

        listData = new ArrayList<>();
        listData.add("1");
        listData.add("1");
        listData.add("1");
        listData.add("1");
        listData.add("1");
        listData.add("1");
        listData.add("1");
        listData.add("1");
        listData.add("1");
        listData.add("1");
        listData.add("1");
        listData.add("1");
        listData.add("1");
        listData.add("1");
        listData.add("1");
        listData.add("1");
        listData.add("1");
        listData.add("1");
        listData.add("1");
        listData.add("1");
        listData.add("1");
        listData.add("1");
        listData.add("1");
        listData.add("1");
        listData.add("1");
        listData.add("1");
        listData.add("1");

        initRecyclerView();
    }

    private void initSpinner() {
        //数据源位置
        typeArrays = getResources().getStringArray(R.array.student_array);
        spinner.setItems(typeArrays);
        spinner.setSelectedIndex(defaultType);//默认选中
        spinner.setOnItemSelectedListener(new WhiteSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(WhiteSpinner view, int position, long id, String item) {
                defaultType = position;
                //保存选中的证件类型
                SPUtil.setType(defaultType);
            }
        });

        spinner.setOnNothingSelectedListener(new WhiteSpinner.OnNothingSelectedListener() {

            @Override
            public void onNothingSelected(WhiteSpinner spinner) {
                defaultType = spinner.getSelectedIndex();
                MLog.d("没有选类型defaultType=" + defaultType);
                //保存选中的证件类型
                SPUtil.setType(defaultType);
            }
        });
    }

    @Override
    protected void lazyLoad() {

    }

    //
    private void initRecyclerView() {
        //
        dataAdapter = new WorkerDataAdapter(getActivity());
        dataAdapter.setList(listData);
        //
        manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recylcerView.setLayoutManager(manager);
        recylcerView.setAdapter(dataAdapter);
    }

    @Override
    protected void handleMessage(Message msg) {

        super.handleMessage(msg);

    }


    /**
     * =========================================================================
     * ===================================生命周期相关======================================
     * =========================================================================
     */
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
    /**
     * =========================================================================
     * ===================================接口======================================
     * =========================================================================
     *
     */


    /**
     * =========================================================================
     * ===================================View接口回调======================================
     * =========================================================================
     */

}