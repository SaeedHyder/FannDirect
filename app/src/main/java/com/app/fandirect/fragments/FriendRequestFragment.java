package com.app.fandirect.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.app.fandirect.R;
import com.app.fandirect.entities.AllRequestEnt;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.global.AppConstants;
import com.app.fandirect.helpers.UIHelper;
import com.app.fandirect.interfaces.FannRequestInterface;
import com.app.fandirect.ui.adapters.ArrayListAdapter;
import com.app.fandirect.ui.binders.FriendRequestBinder;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.TitleBar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.app.fandirect.global.AppConstants.accept_request;
import static com.app.fandirect.global.AppConstants.accepted;
import static com.app.fandirect.global.AppConstants.cancel_request;
import static com.app.fandirect.global.AppConstants.declined;
import static com.app.fandirect.global.AppConstants.declined_request;
import static com.app.fandirect.global.AppConstants.unfriend;
import static com.app.fandirect.global.WebServiceConstants.AlRequests;
import static com.app.fandirect.global.WebServiceConstants.confirmRequest;
import static com.app.fandirect.global.WebServiceConstants.deleteRequest;

/**
 * Created by saeedhyder on 3/5/2018.
 */
public class FriendRequestFragment extends BaseFragment implements FannRequestInterface {
    @BindView(R.id.lv_fanns)
    ListView lvFanns;
    Unbinder unbinder;
    @BindView(R.id.txt_no_data)
    AnyTextView txtNoData;
    protected BroadcastReceiver broadcastReceiver;


    private ArrayListAdapter<AllRequestEnt> adapter;
    private ArrayList<AllRequestEnt> collection;

    public static FriendRequestFragment newInstance() {
        Bundle args = new Bundle();

        FriendRequestFragment fragment = new FriendRequestFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new ArrayListAdapter<AllRequestEnt>(getDockActivity(), new FriendRequestBinder(getDockActivity(), prefHelper, this));

        if (getArguments() != null) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_request, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMainActivity().showBottomBar(AppConstants.search);

        serviceHelper.enqueueCall(headerWebService.getAllFannRequests(), AlRequests);
        onNotificationReceived();


    }

    private void onNotificationReceived() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(AppConstants.REGISTRATION_COMPLETE)) {
                    System.out.println("registration complete");
                    System.out.println(prefHelper.getFirebase_TOKEN());

                } else if (intent.getAction().equals(AppConstants.PUSH_NOTIFICATION)) {
                    Bundle bundle = intent.getExtras();
                    if (bundle != null) {
                        String Type = bundle.getString("pushtype");

                        if (Type != null && (Type.equals(cancel_request) || Type.equals(declined_request) || Type.equals(unfriend))) {
                            serviceHelper.enqueueCall(headerWebService.getAllFannRequests(), AlRequests);
                        }
                    }

                } else {
                    UIHelper.showShortToastInCenter(getDockActivity(), "Notification Data is Empty");
                }
            }

        };
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getDockActivity()).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getDockActivity()).registerReceiver(broadcastReceiver,
                new IntentFilter(AppConstants.REGISTRATION_COMPLETE));

        LocalBroadcastManager.getInstance(getDockActivity()).registerReceiver(broadcastReceiver,
                new IntentFilter(AppConstants.PUSH_NOTIFICATION));


    }

    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        super.ResponseSuccess(result, Tag, message);
        switch (Tag) {

            case AlRequests:
                ArrayList<AllRequestEnt> ent = (ArrayList<AllRequestEnt>) result;
                collection = new ArrayList<>();

                for (AllRequestEnt item : ent) {
                    if (item.getSenderId().equals(prefHelper.getUser().getId())) {
                        if (!item.getStatus().equals("pending")) {
                            collection.add(item);
                        }
                    } else {
                     //   if (!item.getStatus().equals("pending")) {
                            collection.add(item);
                     //   }

                    }
                }


                if (collection.size() > 0) {
                    lvFanns.setVisibility(View.VISIBLE);
                    txtNoData.setVisibility(View.GONE);
                } else {
                    lvFanns.setVisibility(View.GONE);
                    txtNoData.setVisibility(View.VISIBLE);
                }

                adapter.clearList();
                lvFanns.setAdapter(adapter);
                adapter.addAll(collection);
                adapter.notifyDataSetChanged();

                break;

            case confirmRequest:

                UIHelper.showShortToastInCenter(getDockActivity(), "Confirmed successfully");
                serviceHelper.enqueueCall(headerWebService.getAllFannRequests(), AlRequests);


                break;

            case deleteRequest:

                UIHelper.showShortToastInCenter(getDockActivity(), "Deleted successfully");
                serviceHelper.enqueueCall(headerWebService.getAllFannRequests(), AlRequests);

                break;
        }
    }


    @Override
    public void setTitleBar(TitleBar titleBar) {
        super.setTitleBar(titleBar);
        titleBar.hideButtons();
        titleBar.showBackButton();
        titleBar.setSubHeading(getString(R.string.fann_requestss));
    }


    @Override
    public void confirmRequest(AllRequestEnt entity, int position) {

        serviceHelper.enqueueCall(headerWebService.markFannRequset(entity.getId() + "", accepted,prefHelper.getUser().getRoleId() + ""), confirmRequest);
    }

    @Override
    public void deleteRequest(AllRequestEnt entity, int position) {

        serviceHelper.enqueueCall(headerWebService.markFannRequset(entity.getId() + "", declined,prefHelper.getUser().getRoleId() + ""), deleteRequest);

    }


}
