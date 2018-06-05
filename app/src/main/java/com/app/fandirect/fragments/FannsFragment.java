package com.app.fandirect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.app.fandirect.R;
import com.app.fandirect.entities.AllRequestEnt;
import com.app.fandirect.entities.GetMyFannsEnt;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.global.AppConstants;
import com.app.fandirect.interfaces.MyFannsInterface;
import com.app.fandirect.ui.adapters.ArrayListAdapter;
import com.app.fandirect.ui.binders.FannsItemBinder;
import com.app.fandirect.ui.views.AnyEditTextView;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.TitleBar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.fandirect.global.AppConstants.UserRoleId;
import static com.app.fandirect.global.WebServiceConstants.AlRequests;
import static com.app.fandirect.global.WebServiceConstants.getMyFanns;

/**
 * Created by saeedhyder on 3/5/2018.
 */
public class FannsFragment extends BaseFragment implements MyFannsInterface {
    @BindView(R.id.txt_fannsRequestNo)
    AnyTextView txtFannsRequestNo;
    @BindView(R.id.btnFannsRequest)
    LinearLayout btnFannsRequest;
    @BindView(R.id.txt_fannsNo)
    AnyTextView txtFannsNo;
    @BindView(R.id.btnFanns)
    LinearLayout btnFanns;
    @BindView(R.id.txt_search)
    AnyEditTextView txtSearch;
    @BindView(R.id.iv_search_icon)
    ImageView ivSearchIcon;
    @BindView(R.id.ll_search_box)
    LinearLayout llSearchBox;
    @BindView(R.id.lv_fanns)
    ListView lvFanns;
    Unbinder unbinder;
    @BindView(R.id.ll_search)
    LinearLayout llSearch;
    @BindView(R.id.txt_no_data)
    AnyTextView txtNoData;

    String fannsCount="";
    String fannsRequestCount="";

    private ArrayListAdapter<GetMyFannsEnt> adapter;
    private ArrayList<GetMyFannsEnt> collection;
    private ArrayList<AllRequestEnt> collectionFannRqeuests;

    public static FannsFragment newInstance() {
        Bundle args = new Bundle();

        FannsFragment fragment = new FannsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ArrayListAdapter<GetMyFannsEnt>(getDockActivity(), new FannsItemBinder(getDockActivity(), prefHelper, this));

        if (getArguments() != null) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fanns, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMainActivity().showBottomBar(AppConstants.search);

        serviceHelper.enqueueCall(headerWebService.getMyFanns(), getMyFanns);
        serviceHelper.enqueueCall(headerWebService.getAllFannRequests(), AlRequests);

    }

    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        super.ResponseSuccess(result, Tag, message);
        switch (Tag) {

            case getMyFanns:
                ArrayList<GetMyFannsEnt> entity = (ArrayList<GetMyFannsEnt>) result;
                fannsCount=String.valueOf(entity.size());
                txtFannsNo.setText(fannsCount);
                setFannsData(entity);
                break;

            case AlRequests:

                ArrayList<AllRequestEnt> ent = (ArrayList<AllRequestEnt>) result;

                collectionFannRqeuests = new ArrayList<>();

                for (AllRequestEnt item : ent) {
                    if (item.getSenderId().equals(prefHelper.getUser().getId())) {
                        if (!item.getStatus().equals("pending")) {
                            collectionFannRqeuests.add(item);
                        }
                    } else {
                        collectionFannRqeuests.add(item);
                    }
                }
                fannsRequestCount=String.valueOf(collectionFannRqeuests.size());
                txtFannsRequestNo.setText(fannsRequestCount);
                break;

        }
    }

    private void setFannsData(ArrayList<GetMyFannsEnt> entity) {

        if (entity.size() > 0) {
            lvFanns.setVisibility(View.VISIBLE);
            txtNoData.setVisibility(View.GONE);
        } else {
            lvFanns.setVisibility(View.GONE);
            txtNoData.setVisibility(View.VISIBLE);
        }

        adapter.clearList();
        lvFanns.setAdapter(adapter);
        adapter.addAll(entity);
        adapter.notifyDataSetChanged();


    }

    @Override
    public void setTitleBar(TitleBar titleBar) {
        super.setTitleBar(titleBar);
        titleBar.hideButtons();
        titleBar.showBackButton();
        titleBar.setSubHeading("Fann");
    }


    @OnClick({R.id.btnFanns, R.id.btnFannsRequest})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnFanns:
                //   getDockActivity().replaceDockableFragment(FannsFragment.newInstance(), "FannsFragment");
                break;
            case R.id.btnFannsRequest:
                getDockActivity().replaceDockableFragment(FriendRequestFragment.newInstance(), "FriendRequestFragment");

                break;
        }
    }


    @Override
    public void getMyFanns(GetMyFannsEnt entity, int position) {

        String userId = "";
        String roleId = "";

        if (entity != null && entity.getSenderDetail() != null && entity.getReceiverDetail() != null) {
            if (entity.getSenderDetail().getId().equals(prefHelper.getUser().getId())) {

                userId = entity.getReceiverDetail().getId() + "";
                roleId = entity.getReceiverDetail().getRoleId() + "";

            } else {
                userId = entity.getSenderDetail().getId() + "";
                roleId = entity.getSenderDetail().getRoleId() + "";
            }
        }
        if (roleId.equals(UserRoleId)) {
            getDockActivity().replaceDockableFragment(UserProfileFragment.newInstance(userId), "UserProfileFragment");
        } else {
            getDockActivity().replaceDockableFragment(SpProfileFragment.newInstance(userId), "SpProfileFragment");

        }

    }
}
