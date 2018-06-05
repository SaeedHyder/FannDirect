package com.app.fandirect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.app.fandirect.R;
import com.app.fandirect.entities.NotificationEnt;
import com.app.fandirect.entities.UserEnt;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.global.AppConstants;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.ui.adapters.ArrayListAdapter;
import com.app.fandirect.ui.binders.NotificationItemBinder;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.TitleBar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.app.fandirect.global.WebServiceConstants.getNotification;

/**
 * Created by saeedhyder on 3/12/2018.
 */
public class NotificationFragment extends BaseFragment implements RecyclerViewItemListener {
    @BindView(R.id.txt_no_data)
    AnyTextView txtNoData;
    @BindView(R.id.lv_notification)
    ListView lvNotification;
    Unbinder unbinder;

    private ArrayListAdapter<NotificationEnt> adapter;
    private ArrayList<NotificationEnt> collection;

    public static NotificationFragment newInstance() {
        Bundle args = new Bundle();

        NotificationFragment fragment = new NotificationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ArrayListAdapter<NotificationEnt>(getDockActivity(), new NotificationItemBinder(getDockActivity(), prefHelper, this));
        if (getArguments() != null) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMainActivity().hideBottomBar();

        serviceHelper.enqueueCall(headerWebService.getNotification(),getNotification);


    }

    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        super.ResponseSuccess(result, Tag, message);
        switch (Tag) {

            case getNotification:
                ArrayList<NotificationEnt> entity = (ArrayList<NotificationEnt>) result;
                setListViewData(entity);

                break;
        }
    }

    private void setListViewData(ArrayList<NotificationEnt> entity) {

        collection = new ArrayList<>();
       collection.addAll(entity);

        if(entity.size()>0){
            lvNotification.setVisibility(View.VISIBLE);
            txtNoData.setVisibility(View.GONE);
        }
        else {
            lvNotification.setVisibility(View.GONE);
            txtNoData.setVisibility(View.VISIBLE);
        }

        adapter.clearList();
        lvNotification.setAdapter(adapter);
        adapter.addAll(collection);



    }

    @Override
    public void setTitleBar(TitleBar titleBar) {
        super.setTitleBar(titleBar);
        titleBar.hideButtons();
        titleBar.showBackButton();
        titleBar.setSubHeading(getString(R.string.notifications));
    }


    @Override
    public void onRecyclerItemClicked(Object Ent, int position) {

    }

    @Override
    public void onCategoryClick(Object Ent, int position) {

    }
}
