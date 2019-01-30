package com.app.fandirect.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.app.fandirect.R;
import com.app.fandirect.entities.AllRequestEnt;
import com.app.fandirect.entities.FannListEnt;
import com.app.fandirect.entities.GetMyFannsEnt;
import com.app.fandirect.entities.Post;
import com.app.fandirect.entities.ReceiverDetail;
import com.app.fandirect.entities.SenderDetail;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.global.AppConstants;
import com.app.fandirect.interfaces.MyFannsInterface;
import com.app.fandirect.ui.adapters.ArrayListAdapter;
import com.app.fandirect.ui.binders.FannsItemBinder;
import com.app.fandirect.ui.views.AnyEditTextView;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.TitleBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;

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
    @BindView(R.id.txt_fanns)
    AnyTextView txt_fanns;
    @BindView(R.id.fann_requests)
    AnyTextView fann_requests;
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

    String fannsCount = "";
    String fannsRequestCount = "";

    private ArrayListAdapter<FannListEnt> adapter;
    private ArrayList<FannListEnt> collection = new ArrayList<>();
    private ArrayList<AllRequestEnt> collectionFannRqeuests = new ArrayList<>();


    public static FannsFragment newInstance() {
        Bundle args = new Bundle();

        FannsFragment fragment = new FannsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ArrayListAdapter<FannListEnt>(getDockActivity(), new FannsItemBinder(getDockActivity(), prefHelper, this));

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

        setTextWatecher();

    }

    private void setTextWatecher() {
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                bindData(getSearchedArray(s.toString()));
            }
        });
    }

    public ArrayList<FannListEnt> getSearchedArray(String keyword) {
        if (collection == null) {
            return new ArrayList<>();
        }
        if (collection != null && collection.isEmpty()) {
            return new ArrayList<>();
        }

        ArrayList<FannListEnt> arrayList = new ArrayList<>();

        String UserName = "";
        for (FannListEnt item : collection) {
           /* if (item.getSenderDetail().getId().equals(String.valueOf(prefHelper.getUser().getId()))) {
                if (item.getReceiverDetail() != null) {
                    UserName = item.getReceiverDetail().getUserName();
                }
            } else if (item.getSenderDetail() != null) {
                UserName = item.getSenderDetail().getUserName();
            }*/

            if (Pattern.compile(Pattern.quote(keyword), Pattern.CASE_INSENSITIVE).matcher(item.getName()).find()) {
                arrayList.add(item);
            }
        }
        return arrayList;

    }

    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        super.ResponseSuccess(result, Tag, message);
        switch (Tag) {

            case getMyFanns:
                ArrayList<GetMyFannsEnt> entity = (ArrayList<GetMyFannsEnt>) result;

                ArrayList<FannListEnt> sortedList = new ArrayList<>();

                for (GetMyFannsEnt item : entity) {
                    if (item.getSenderDetail()!=null && item.getSenderDetail().getId()!=null && item.getSenderDetail().getId().equals(String.valueOf(prefHelper.getUser().getId()))) {
                        if (item.getReceiverDetail() != null) {
                            sortedList.add(new FannListEnt(item.getReceiverDetail().getUserName(), item.getReceiverDetail().getImageUrl(), item.getReceiverDetail().getId(), item.getReceiverDetail().getRoleId()));
                        }
                    } else if (item.getSenderDetail() != null) {
                        sortedList.add(new FannListEnt(item.getSenderDetail().getUserName(), item.getSenderDetail().getImageUrl(), item.getSenderDetail().getId(), item.getSenderDetail().getRoleId()));
                    }
                }
                Collections.sort(sortedList, new Comparator<FannListEnt>() {
                    @Override
                    public int compare(FannListEnt fannListEnt, FannListEnt t1) {
                        return fannListEnt.getName().toLowerCase().compareTo(t1.getName().toLowerCase());
                    }
                });

             /*   Collections.sort(entity, new Comparator<GetMyFannsEnt>() {
                    @Override
                    public int compare(GetMyFannsEnt getMyFannsEnt, GetMyFannsEnt t1) {
                        if (getMyFannsEnt.getSenderDetail().getId().equals(String.valueOf(prefHelper.getUser().getId()))) {
                            if (getMyFannsEnt.getReceiverDetail() != null) {
                                return getMyFannsEnt.getReceiverDetail().getUserName().toLowerCase().compareTo(t1.getReceiverDetail().getUserName().toLowerCase());
                            }
                        } else if (getMyFannsEnt.getSenderDetail() != null) {
                            return getMyFannsEnt.getSenderDetail().getUserName().toLowerCase().compareTo(t1.getSenderDetail().getUserName().toLowerCase());
                        }
                        return 0;
                    }
                });*/

                fannsCount = String.valueOf(sortedList.size());
                if (fannsCount.equals("1")) {
                    txt_fanns.setText(R.string.fann);
                } else {
                    txt_fanns.setText(R.string.fanns);
                }
                txtFannsNo.setText(fannsCount);

                setFannsData(sortedList);
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
                fannsRequestCount = String.valueOf(collectionFannRqeuests.size());
                if (fannsRequestCount.equals("1")) {
                    fann_requests.setText(R.string.fann_request);
                } else {
                    fann_requests.setText(R.string.fann_requests);
                }
                txtFannsRequestNo.setText(fannsRequestCount);
                break;

        }
    }

    private void setFannsData(ArrayList<FannListEnt> entity) {
        collection = new ArrayList<>();
        collection.addAll(entity);
        bindData(collection);


    }

    private void bindData(ArrayList<FannListEnt> collection) {

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
    public void getMyFanns(FannListEnt entity, int position) {

       /* String userId = "";
        String roleId = "";

        if (entity != null && entity.getSenderDetail() != null && entity.getReceiverDetail() != null) {
            if (entity.getSenderDetail().getId().equals(prefHelper.getUser().getId())) {

                userId = entity.getReceiverDetail().getId() + "";
                roleId = entity.getReceiverDetail().getRoleId() + "";

            } else {
                userId = entity.getSenderDetail().getId() + "";
                roleId = entity.getSenderDetail().getRoleId() + "";
            }
        }*/
        if (entity.getRoleId().equals(UserRoleId)) {
            getDockActivity().replaceDockableFragment(UserProfileFragment.newInstance(entity.getUserId()), "UserProfileFragment");
        } else {
            getDockActivity().replaceDockableFragment(SpProfileFragment.newInstance(entity.getUserId()), "SpProfileFragment");

        }

    }
}
