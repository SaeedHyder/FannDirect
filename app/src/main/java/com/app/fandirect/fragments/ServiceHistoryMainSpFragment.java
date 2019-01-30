package com.app.fandirect.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.app.fandirect.R;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.ui.views.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.app.fandirect.activities.DockActivity.KEY_FRAG_FIRST;

public class ServiceHistoryMainSpFragment extends BaseFragment {
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.fragmentContainer)
    FrameLayout fragmentContainer;
    Unbinder unbinder;

    private FragmentManager manager;

    public static ServiceHistoryMainSpFragment newInstance() {
        Bundle args = new Bundle();

        ServiceHistoryMainSpFragment fragment = new ServiceHistoryMainSpFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = getChildFragmentManager();
        if (getArguments() != null) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_main_sp, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setTabLayout();
        tabLayoutistner();
    }

    private void setTabLayout() {

        if (tabLayout != null) {
            tabLayout.removeAllTabs();
            tabLayout.addTab(tabLayout.newTab().setText(getResString(R.string.inprogress)));
            tabLayout.addTab(tabLayout.newTab().setText(getResString(R.string.history)));
            TabLayout.Tab tab = tabLayout.getTabAt(0);
            tab.select();
            setData(tab);
        }
    }
    private void tabLayoutistner() {

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                setData(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setData(TabLayout.Tab tab) {

        if (tab.getPosition() == 0) {
            replaceFragment(ServiceHistorySpFragment.newInstance());
        } else {
            replaceFragment(ServiceHistorySpFragment.newInstance(true));
        }
    }

    public void replaceFragment(Fragment frag) {

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.fragmentContainer, frag);
        transaction.addToBackStack(manager.getBackStackEntryCount() == 0 ? KEY_FRAG_FIRST : null).commit();

    }
    @Override
    public void setTitleBar(TitleBar titleBar) {
        super.setTitleBar(titleBar);
        titleBar.hideButtons();
        titleBar.showBackButton();
        titleBar.setSubHeading(getString(R.string.service_history));

    }


}
