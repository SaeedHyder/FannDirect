package com.app.fandirect.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
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
import com.app.fandirect.global.WebServiceConstants;
import com.app.fandirect.helpers.DialogHelper;
import com.app.fandirect.helpers.UIHelper;
import com.app.fandirect.interfaces.OnLongTap;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.ui.adapters.ArrayListAdapter;
import com.app.fandirect.ui.binders.BinderNotification;
import com.app.fandirect.ui.binders.FeedsRecyclerBinder;
import com.app.fandirect.ui.binders.MessageBinder;
import com.app.fandirect.ui.views.AnyEditTextView;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.CustomRecyclerView;
import com.app.fandirect.ui.views.TitleBar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.leolin.shortcutbadger.ShortcutBadger;

import static com.app.fandirect.global.AppConstants.messagePush;
import static com.app.fandirect.global.WebServiceConstants.DeleteChat;
import static com.app.fandirect.global.WebServiceConstants.cancelled;
import static com.app.fandirect.global.WebServiceConstants.getAllMsgThread;
import static com.app.fandirect.global.WebServiceConstants.getConversationFromMsg;

/**
 * Created by saeedhyder on 3/14/2018.
 */
public class MessageFragment extends BaseFragment implements RecyclerViewItemListener, OnLongTap {
    @BindView(R.id.txt_search)
    AnyEditTextView txtSearch;
    @BindView(R.id.iv_search_icon)
    ImageView ivSearchIcon;
    @BindView(R.id.ll_search)
    LinearLayout llSearch;
    @BindView(R.id.ll_search_box)
    LinearLayout llSearchBox;
    @BindView(R.id.lv_message)
    CustomRecyclerView lvMessage;
    Unbinder unbinder;
    @BindView(R.id.txt_no_data)
    AnyTextView txtNoData;

    // private ArrayListAdapter<MessageThreadsEnt> Adapter;
    private ArrayList<MessageThreadsEnt> collection = new ArrayList<>();
    private Integer jobDonePosition;
    protected BroadcastReceiver broadcastReceiver;

    public static MessageFragment newInstance() {
        Bundle args = new Bundle();

        MessageFragment fragment = new MessageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  Adapter = new ArrayListAdapter<MessageThreadsEnt>(getDockActivity(), new MessageBinder(getDockActivity(), prefHelper, this, this));

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
        onNotificationReceived();

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
                UserName = item.getReceiverDetail().getUserName().toLowerCase();
            } else {
                UserName = item.getSenderDetail().getUserName().toLowerCase();
            }

            if (UserName.contains(keyword.toLowerCase())) {
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
                if (getTitleBar() != null) {
                    getTitleBar().hideNotificationBell();
                }
                ArrayList<MessageThreadsEnt> entity = (ArrayList<MessageThreadsEnt>) result;
                setMessageListData(entity);
                break;

            case DeleteChat:
                if (jobDonePosition != null) {
                    collection.remove((int) jobDonePosition);
                    lvMessage.notifyDataSetChanged();

                    if (collection.size() > 0) {
                        lvMessage.setVisibility(View.VISIBLE);
                        txtNoData.setVisibility(View.GONE);
                    } else {
                        lvMessage.setVisibility(View.GONE);
                        txtNoData.setVisibility(View.VISIBLE);
                    }
                }
                break;
        }
    }

    private void setMessageListData(ArrayList<MessageThreadsEnt> entity) {

        collection = new ArrayList<>();
        collection.addAll(entity);

        bindData(collection);

    }

    private void bindData(ArrayList<MessageThreadsEnt> collection) {


        if (collection.size() <= 0) {
            lvMessage.setVisibility(View.GONE);
            txtNoData.setVisibility(View.VISIBLE);
        } else {
            txtNoData.setVisibility(View.GONE);
            lvMessage.setVisibility(View.VISIBLE);
            lvMessage.BindRecyclerView(new MessageBinder(getDockActivity(), prefHelper, this, this), collection, new LinearLayoutManager(getDockActivity()), new DefaultItemAnimator());

        }
       /* Adapter.clearList();
        lvMessage.setAdapter(Adapter);
        Adapter.addAll(collection);*/
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
        MessageThreadsEnt entity = (MessageThreadsEnt) Ent;
        getDockActivity().replaceDockableFragment(InboxChatFragment.newInstance(entity), "InboxChatFragment");

    }

    @Override
    public void onCategoryClick(Object Ent, int position) {

    }

    @Override
    public void onClick(final Object entity, final int position) {

        final MessageThreadsEnt ent = (MessageThreadsEnt) entity;

        final DialogHelper dialogHelper = new DialogHelper(getDockActivity());
        dialogHelper.initDeleteDialoge(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jobDonePosition = position;
                serviceHelper.enqueueCall(headerWebService.deleteThread(ent.getId() + ""), DeleteChat);
                dialogHelper.hideDialog();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogHelper.hideDialog();
            }
        }, getDockActivity().getResources().getString(R.string.delete_chat), getDockActivity().getResources().getString(R.string.are_you_sure_you_want_to_delete_chat));

        dialogHelper.showDialog();
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
                        final String SenderId = bundle.getString("sender_id");
                        final String ReceiverId = bundle.getString("receiver_id");

                        if (Type != null && Type.equals(messagePush)) {
                            serviceHelper.enqueueCall(headerWebService.getMyThreadMsges(), getAllMsgThread, false);
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
}
