package com.app.fandirect.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.app.fandirect.R;
import com.app.fandirect.entities.MessageThreadsEnt;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.global.AppConstants;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.ui.adapters.ArrayListAdapter;
import com.app.fandirect.ui.binders.MessageBinder;
import com.app.fandirect.ui.views.AnyEditTextView;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.TitleBar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.app.fandirect.global.WebServiceConstants.getAllMsgThread;

/**
 * Created by saeedhyder on 3/14/2018.
 */
public class MessageFragment extends BaseFragment implements RecyclerViewItemListener {
    @BindView(R.id.txt_search)
    AnyEditTextView txtSearch;
    @BindView(R.id.iv_search_icon)
    ImageView ivSearchIcon;
    @BindView(R.id.ll_search)
    LinearLayout llSearch;
    @BindView(R.id.ll_search_box)
    LinearLayout llSearchBox;
    @BindView(R.id.lv_message)
    ListView lvMessage;
    Unbinder unbinder;
    @BindView(R.id.txt_no_data)
    AnyTextView txtNoData;

    private ArrayListAdapter<MessageThreadsEnt> Adapter;
    private ArrayList<MessageThreadsEnt> collection = new ArrayList<>();

    public static MessageFragment newInstance() {
        Bundle args = new Bundle();

        MessageFragment fragment = new MessageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Adapter = new ArrayListAdapter<MessageThreadsEnt>(getDockActivity(), new MessageBinder(getDockActivity(), prefHelper, this));

        if (getArguments() != null) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMainActivity().showBottomBar(AppConstants.message);

        serviceHelper.enqueueCall(headerWebService.getMyThreadMsges(), getAllMsgThread);
        setListListner();


    }

    private void setListListner() {

/*
        lvMessage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (collection != null)
                    getDockActivity().replaceDockableFragment(InboxChatFragment.newInstance(collection.get(position)), "InboxChatFragment");
            }
        });*/

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

    public ArrayList<MessageThreadsEnt> getSearchedArray(String keyword) {
        if (collection.isEmpty()) {
            return new ArrayList<>();
        }

        ArrayList<MessageThreadsEnt> arrayList = new ArrayList<>();

        String UserName = "";
        for (MessageThreadsEnt item : collection) {
            if (item.getSenderId().equals(String.valueOf(prefHelper.getUser().getId()))) {
                UserName = item.getReceiverDetail().getUserName();
            } else {
                UserName = item.getSenderDetail().getUserName();
            }

            if (UserName.contains(keyword)) {
                arrayList.add(item);
            }
        }
        return arrayList;

    }

    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        super.ResponseSuccess(result, Tag, message);
        switch (Tag) {

            case getAllMsgThread:
                ArrayList<MessageThreadsEnt> entity = (ArrayList<MessageThreadsEnt>) result;
                setMessageListData(entity);
                break;


        }
    }

    private void setMessageListData(ArrayList<MessageThreadsEnt> entity) {

        collection = new ArrayList<>();

        collection.addAll(entity);

        if (collection.size() > 0) {
            lvMessage.setVisibility(View.VISIBLE);
            txtNoData.setVisibility(View.GONE);
        } else {
            lvMessage.setVisibility(View.GONE);
            txtNoData.setVisibility(View.VISIBLE);
        }


        bindData(collection);

    }

    private void bindData(ArrayList<MessageThreadsEnt> collection) {
        Adapter.clearList();
        lvMessage.setAdapter(Adapter);
        Adapter.addAll(collection);
    }

    @Override
    public void setTitleBar(TitleBar titleBar) {
        super.setTitleBar(titleBar);
        titleBar.hideButtons();
        titleBar.showBackButton();
        titleBar.setSubHeading(getString(R.string.chat));
    }


    @Override
    public void onRecyclerItemClicked(Object Ent, int position) {
        MessageThreadsEnt entity=(MessageThreadsEnt)Ent;
        getDockActivity().replaceDockableFragment(InboxChatFragment.newInstance(entity), "InboxChatFragment");

    }

    @Override
    public void onCategoryClick(Object Ent, int position) {

    }

}
