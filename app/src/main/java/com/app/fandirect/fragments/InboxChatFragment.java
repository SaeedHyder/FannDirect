package com.app.fandirect.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.app.fandirect.R;
import com.app.fandirect.entities.MessageThreadsEnt;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.global.AppConstants;
import com.app.fandirect.helpers.UIHelper;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.ui.adapters.ArrayListAdapter;
import com.app.fandirect.ui.binders.CategoryRecyclerBinder;
import com.app.fandirect.ui.binders.ChatItemBinder;
import com.app.fandirect.ui.views.AnyEditTextView;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.CustomRecyclerView;
import com.app.fandirect.ui.views.TitleBar;
import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.fandirect.global.AppConstants.messagePush;
import static com.app.fandirect.global.WebServiceConstants.getConversation;
import static com.app.fandirect.global.WebServiceConstants.getConversationFromMsg;
import static com.app.fandirect.global.WebServiceConstants.sendMessage;

/**
 * Created by saeedhyder on 3/15/2018.
 */
public class InboxChatFragment extends BaseFragment implements RecyclerViewItemListener {
    @BindView(R.id.lv_chatBox)
    CustomRecyclerView lvChatBox;
    @BindView(R.id.ll_send_btn)
    LinearLayout llSendBtn;
    Unbinder unbinder;
    @BindView(R.id.txt_no_data)
    AnyTextView txtNoData;
    @BindView(R.id.txtMessageBox)
    AnyEditTextView txtMessageBox;

    private ArrayList<MessageThreadsEnt> collection;

    private static String ReceivedIdKey = "ReceivedIdKey";
    private static String MsgBoolenKey = "MsgBoolenKey";
    private static String ThreadEntKey = "ThreadIdKey";
    private String threadEnt;
    private String ReceivedId;
    private MessageThreadsEnt threadEntitiy;
    private boolean isFromMessage = false;
    protected BroadcastReceiver broadcastReceiver;

    public static InboxChatFragment newInstance() {
        Bundle args = new Bundle();

        InboxChatFragment fragment = new InboxChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static InboxChatFragment newInstance(String receiverId, boolean IsMessage) {
        Bundle args = new Bundle();
        args.putString(ReceivedIdKey, receiverId);
        args.putBoolean(MsgBoolenKey, IsMessage);
        InboxChatFragment fragment = new InboxChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static InboxChatFragment newInstance(MessageThreadsEnt threadEnt) {
        Bundle args = new Bundle();
        args.putString(ThreadEntKey, new Gson().toJson(threadEnt));
        InboxChatFragment fragment = new InboxChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            ReceivedId = getArguments().getString(ReceivedIdKey);
            threadEnt = getArguments().getString(ThreadEntKey);
            isFromMessage = getArguments().getBoolean(MsgBoolenKey);
        }
        if (threadEnt != null) {
            threadEntitiy = new Gson().fromJson(threadEnt, MessageThreadsEnt.class);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbox_chat, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMainActivity().hideBottomBar();

        onNotificationReceived();

        if (threadEntitiy != null && threadEntitiy.getId() != null && !isFromMessage) {
            serviceHelper.enqueueCall(headerWebService.getConversation(threadEntitiy.getId() + ""), getConversation);
        } else if (ReceivedId != null) {
            serviceHelper.enqueueCall(headerWebService.getConversationFromMsg(ReceivedId + ""), getConversationFromMsg);
        }


        if (!isFromMessage) {
            if (threadEntitiy != null && threadEntitiy.getSenderId().equals(String.valueOf(prefHelper.getUser().getId()))) {
                ReceivedId = threadEntitiy.getReceiverId();
            } else {
                ReceivedId = threadEntitiy.getSenderId();
            }
        }

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

                        String userId = "";
                        if (SenderId.equals(prefHelper.getUser().getId())) {
                            userId = ReceiverId;
                        } else {
                            userId = SenderId;
                        }

                        if (Type != null && Type.equals(messagePush)) {
                           // getDockActivity().replaceDockableFragment(InboxChatFragment.newInstance(userId, true), "InboxChatFragment");
                            serviceHelper.enqueueCall(headerWebService.getConversationFromMsg(userId), getConversationFromMsg,false);
                        }
                    }

                } else {
                    UIHelper.showShortToastInCenter(getDockActivity(), "Notification Data is Empty");
                }
            }

        };
    }


    @Override
    public void setTitleBar(TitleBar titleBar) {
        super.setTitleBar(titleBar);
        titleBar.hideButtons();
        titleBar.showBackButton();
        titleBar.setSubHeading(getString(R.string.chat_box));
    }


    @OnClick(R.id.ll_send_btn)
    public void onViewClicked() {
        if (isValidate())
            serviceHelper.enqueueCall(headerWebService.sendMessage(ReceivedId, txtMessageBox.getText().toString()), sendMessage);
    }

    private boolean isValidate() {
        if (txtMessageBox == null || txtMessageBox.getText().toString().equals("") || txtMessageBox.getText().toString().trim().equals("")) {
            UIHelper.showShortToastInCenter(getDockActivity(), "Write message to proceed");
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRecyclerItemClicked(Object Ent, int position) {

    }

    @Override
    public void onCategoryClick(Object Ent, int position) {

    }

    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        super.ResponseSuccess(result, Tag, message);
        switch (Tag) {

            case sendMessage:
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                UIHelper.hideSoftKeyboard(getDockActivity(), txtMessageBox);
                txtMessageBox.getText().clear();
                if (collection.size() > 0) {
                    MessageThreadsEnt ent = (MessageThreadsEnt) result;

                    lvChatBox.getAdapter().add(ent);
                    lvChatBox.getAdapter().notifyItemChanged(lvChatBox.getAdapter().getItemCount()-1);
                  //  lvChatBox.scrollToPosition(collection.size()-1);

                } else {
                    if (threadEntitiy != null && threadEntitiy.getId() != null && !isFromMessage) {
                        serviceHelper.enqueueCall(headerWebService.getConversation(threadEntitiy.getId() + ""), getConversation);
                    } else if (ReceivedId != null) {
                        serviceHelper.enqueueCall(headerWebService.getConversationFromMsg(ReceivedId + ""), getConversationFromMsg);
                    }
                }


                break;

            case getConversation:
                ArrayList<MessageThreadsEnt> conversationEnt = (ArrayList<MessageThreadsEnt>) result;
                bindChatData(conversationEnt);
                break;

            case getConversationFromMsg:
                ArrayList<MessageThreadsEnt> conversationEntity = (ArrayList<MessageThreadsEnt>) result;
                bindChatData(conversationEntity);
                break;


        }
    }

    private void bindChatData(ArrayList<MessageThreadsEnt> conversationEnt) {

        collection = new ArrayList<>();
        collection.addAll(conversationEnt);

        if (collection.size() <= 0) {
            txtNoData.setVisibility(View.VISIBLE);
            lvChatBox.setVisibility(View.GONE);
        } else {
            txtNoData.setVisibility(View.GONE);
            lvChatBox.setVisibility(View.VISIBLE);

        }

        lvChatBox.BindRecyclerView(new ChatItemBinder(getDockActivity(), prefHelper, this), collection,
                new LinearLayoutManager(getDockActivity(), LinearLayoutManager.VERTICAL, false)
                , new DefaultItemAnimator());

        lvChatBox.scrollToPosition(collection.size()-1);

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
