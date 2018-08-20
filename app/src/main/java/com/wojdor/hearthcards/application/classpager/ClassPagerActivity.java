package com.wojdor.hearthcards.application.classpager;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.wojdor.hearthcards.R;
import com.wojdor.hearthcards.application.base.BaseActivity;
import com.wojdor.hearthcards.domain.VersionInfo;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClassPagerActivity extends BaseActivity {

    @BindView(R.id.ClassPagerClassViewPager)
    ViewPager classViewPager;
    @BindView(R.id.ClassPagerClassTabLayout)
    TabLayout classTabLayout;

    private ClassPagerViewModel viewModel;
    private ClassPagerAdapter classPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_pager);
        ButterKnife.bind(this);
        initComponents();
        viewModel = ViewModelProviders.of(this).get(ClassPagerViewModel.class);
        viewModel.getLocalVersionInfo().observe(this, this::checkLocalVersionInfo);
    }

    private void initComponents() {
        classPagerAdapter = new ClassPagerAdapter(getSupportFragmentManager());
        classViewPager.setAdapter(classPagerAdapter);
        classTabLayout.setupWithViewPager(classViewPager);
    }

    private void checkLocalVersionInfo(VersionInfo localVersionInfo) {
        if (localVersionInfo == null) {
            // TODO: start Intent Service and download data first time
        } else {
            classPagerAdapter.setItems(localVersionInfo.getClassNames());
            checkForUpdate(localVersionInfo);
        }
    }

    private void checkForUpdate(VersionInfo localVersionInfo) {
        viewModel.getRemoteVersionInfo().observe(this, remoteVersionInfo -> {
            if (remoteVersionInfo == null) return;
            checkRemoteVersionInfo(localVersionInfo, remoteVersionInfo);
        });
    }

    private void checkRemoteVersionInfo(VersionInfo localVersionInfo, VersionInfo remoteVersionInfo) {
        if (localVersionInfo.equals(remoteVersionInfo)) return;
        viewModel.downloadNewData(remoteVersionInfo);
    }
}