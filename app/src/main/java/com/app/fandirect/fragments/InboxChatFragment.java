package com.app.fandirect.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.app.fandirect.R;
import com.app.fandirect.entities.MessageThreadsEnt;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.global.AppConstants;
import com.app.fandirect.helpers.DialogHelper;
import com.app.fandirect.helpers.InternetHelper;
import com.app.fandirect.helpers.UIHelper;
import com.app.fandirect.interfaces.OnLongTap;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.ui.binders.ChatItemBinder;
import com.app.fandirect.ui.views.AnyEditTextView;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.CustomRecyclerView;
import com.app.fandirect.ui.views.TitleBar;
import com.google.gson.Gson;
import com.xw.repo.XEditText;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.fandirect.global.AppConstants.messagePush;
import static com.app.fandirect.global.WebServiceConstants.DeleteChat;
import static com.app.fandirect.global.WebServiceConstants.getConversation;
import static com.app.fandirect.global.WebServiceConstants.getConversationFromMsg;
import static com.app.fandirect.global.WebServiceConstants.sendMessage;

/**
 * Created by saeedhyder on 3/15/2018.
 */
public class InboxChatFragment extends BaseFragment implements RecyclerViewItemListener, OnLongTap {
    @BindView(R.id.lv_chatBox)
    CustomRecyclerView lvChatBox;
    @BindView(R.id.ll_send_btn)
    LinearLayout llSendBtn;
    Unbinder unbinder;
    @BindView(R.id.txt_no_data)
    AnyTextView txtNoData;
    @BindView(R.id.txtDeleted)
    AnyTextView txtDeleted;
    @BindView(R.id.txtMessageBox)
    XEditText txtMessageBox;
    @BindView(R.id.ll_messageBox)
    LinearLayout llMessageBox;
    @BindView(R.id.mainFrameLayout)
    LinearLayout mainFrameLayout;
    private Integer jobDonePosition;
    private ArrayList<MessageThreadsEnt> collection;


    private static String ReceivedIdKey = "ReceivedIdKey";
    private static String MsgBoolenKey = "MsgBoolenKey";
    private static String ThreadEntKey = "ThreadIdKey";
    private String threadEnt;
    private String ReceivedId;
    private MessageThreadsEnt threadEntitiy;
    private boolean isFromMessage = false;
    protected BroadcastReceiver broadcastReceiver;
    private static String username = "";
    private boolean isDeletedThread = false;

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
        username = "";
        InboxChatFragment fragment = new InboxChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static InboxChatFragment newInstance(String receiverId, String UserName, boolean IsMessage) {
        Bundle args = new Bundle();
        args.putString(ReceivedIdKey, receiverId);
        args.putBoolean(MsgBoolenKey, IsMessage);
        username = UserName;
        InboxChatFragment fragment = new InboxChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static InboxChatFragment newInstance(MessageThreadsEnt threadEnt) {
        Bundle args = new Bundle();
        username = "";
        args.putString(ThreadEntKey, new Gson().toJson(threadEnt));
        InboxChatFragment fragment = new InboxChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getDockActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


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

        getMainActivity().hideAdds();
        getMainActivity().hideBottomBar();
        mainFrameLayout.setVisibility(View.GONE);
        onNotificationReceived();


        if (InternetHelper.CheckInternetConectivityandShowToast(getDockActivity())) {
            txtNoData.setVisibility(View.GONE);
            lvChatBox.setVisibility(View.VISIBLE);
            if (threadEntitiy != null && threadEntitiy.getId() != null && !isFromMessage) {
                serviceHelper.enqueueCall(headerWebService.getConversation(threadEntitiy.getId() + ""), getConversation);
            } else if (ReceivedId != null) {
                serviceHelper.enqueueCall(headerWebService.getConversationFromMsg(ReceivedId + ""), getConversationFromMsg);
            }
        } else {
            lvChatBox.setVisibility(View.GONE);
            txtNoData.setText(getResources().getString(R.string.connection_lost));
            txtNoData.setVisibility(View.VISIBLE);
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

                if (getTitleBar() != null) {
                    getTitleBar().hideNotificationBell();
                }
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
                            if (ReceivedId != null && (ReceivedId.equals(userId))) {
                                serviceHelper.enqueueCall(headerWebService.getConversationFromMsg(userId), getConversationFromMsg, false);
                            }
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

        String userName = "";
        if (threadEntitiy != null && threadEntitiy.getId() != null && !isFromMessage) {
            if (threadEntitiy.getSenderId().equals(prefHelper.getUser().getId() + "")) {
                userName = threadEntitiy.getReceiverDetail().getUserName() + "";
            } else {
                userName = threadEntitiy.getSenderDetail().getUserName() + "";
            }
        } else if (username != null && !username.equals("")) {
            userName = username;
        } else {
            userName = "";
        }

        titleBar.setSubHeading(userName);

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
                UIHelper.showShortToastInCenter(getDockActivity(), getString(R.string.message_send_succesfully));
                //   UIHelper.hideSoftKeyboard(getDockActivity(), txtMessageBox);
                txtMessageBox.getText().clear();
                if (collection != null && collection.size() > 0) {
                    MessageThreadsEnt ent = (MessageThreadsEnt) result;
                    collection.add(ent);
                    lvChatBox.notifyDataSetChanged();
                    lvChatBox.scrollToPosition(collection.size() - 1);

                   /* lvChatBox.getAdapter().add(ent);
                    lvChatBox.getAdapter().notifyItemChanged(lvChatBox.getAdapter().getItemCount()-1);*/
                } else {
                    if (threadEntitiy != null && threadEntitiy.getId() != null && !isFromMessage) {
                        serviceHelper.enqueueCall(headerWebService.getConversation(threadEntitiy.getId() + ""), getConversation);
                    } else if (ReceivedId != null) {
                        serviceHelper.enqueueCall(headerWebService.getConversationFromMsg(ReceivedId + ""), getConversationFromMsg);
                    }
                }


                break;

            case getConversation:
                if (getTitleBar() != null) {
                    getTitleBar().hideNotificationBell();
                }
                ArrayList<MessageThreadsEnt> conversationEnt = (ArrayList<MessageThreadsEnt>) result;
                bindChatDataFromMsg(conversationEnt);
                break;

            case getConversationFromMsg:
                if (getTitleBar() != null) {
                    getTitleBar().hideNotificationBell();
                }
                ArrayList<MessageThreadsEnt> conversationEntity = (ArrayList<MessageThreadsEnt>) result;

                if (conversationEntity.size() > 0 && username.equals("") && threadEntitiy == null) {
                    String UserName = "";
                    if (conversationEntity.get(0).getSenderId().equals(prefHelper.getUser().getId() + "")) {
                        UserName = conversationEntity.get(0).getReceiverDetail().getUserName();
                    } else {
                        UserName = conversationEntity.get(0).getSenderDetail().getUserName();
                    }
                    if (getTitleBar() != null) {
                        getTitleBar().setSubHeading(UserName);
                    }
                }

                bindChatDataFromMsg(conversationEntity);
                break;

            case DeleteChat:
                if (jobDonePosition != null) {
                    collection.remove((int) jobDonePosition);
                    lvChatBox.notifyDataSetChanged();

                    if (collection.size() > 0) {
                        lvChatBox.setVisibility(View.VISIBLE);
                        txtNoData.setVisibility(View.GONE);
                    } else {
                        lvChatBox.setVisibility(View.GONE);
                        txtNoData.setVisibility(View.VISIBLE);
                    }
                }
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

        lvChatBox.BindRecyclerView(new ChatItemBinder(getDockActivity(), prefHelper, this, this), collection,
                new LinearLayoutManager(getDockActivity(), LinearLayoutManager.VERTICAL, false)
                , new DefaultItemAnimator());

        lvChatBox.scrollToPosition(collection.size() - 1);

    }

    private void bindChatDataFromMsg(ArrayList<MessageThreadsEnt> conversationEnt) {


        collection = new ArrayList<>();
        for (MessageThreadsEnt item : conversationEnt) {
            if (item.getSenderDetail().getDeletedAt() != null && !item.getSenderDetail().getDeletedAt().equals("") && !item.getSenderDetail().getDeletedAt().equals("null") && !item.getSenderDetail().getDeletedAt().isEmpty()) {
                isDeletedThread = true;
            } else if (item.getReceiverDetail().getDeletedAt() != null && !item.getReceiverDetail().getDeletedAt().equals("") && !item.getReceiverDetail().getDeletedAt().equals("null") && !item.getReceiverDetail().getDeletedAt().isEmpty()) {
                isDeletedThread = true;
            }

            if (item.getSenderId().equals(String.valueOf(prefHelper.getUser().getId()))) {
                if (item.getSenderDelete() != null && item.getSenderDelete().equals("0")) {
                    collection.add(item);
                }
            } else if (item.getReceiverId().equals(String.valueOf(prefHelper.getUser().getId()))) {
                if (item.getReceiverDelete() != null && item.getReceiverDelete().equals("0")) {
                    collection.add(item);
                }
            }
        }
        //collection.addAll(conversationEnt);


        if (collection.size() <= 0) {
            txtNoData.setText(getResString(R.string.no_message_found));
            txtNoData.setVisibility(View.VISIBLE);
            lvChatBox.setVisibility(View.GONE);
        } else {
            txtNoData.setVisibility(View.GONE);
            lvChatBox.setVisibility(View.VISIBLE);
            prefHelper.setChatReceiver_id(getUserId(collection.get(0).getSenderId() + "", collection.get(0).getReceiverId() + "", prefHelper.getUser().getId() + ""));
        }

        setTouchListner(collection.size() - 1);
        setChatisEnable();

        mainFrameLayout.setVisibility(View.VISIBLE);

        lvChatBox.BindRecyclerView(new ChatItemBinder(getDockActivity(), prefHelper, this, this), collection,
                new LinearLayoutManager(getDockActivity(), LinearLayoutManager.VERTICAL, false)
                , new DefaultItemAnimator());

        lvChatBox.scrollToPosition(collection.size() - 1);

    }

    private void setChatisEnable() {

        if(isDeletedThread){
            llMessageBox.setVisibility(View.GONE);
            txtDeleted.setVisibility(View.VISIBLE);
        }
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
    public void onClick(Object entity, final int position) {

        final MessageThreadsEnt ent = (MessageThreadsEnt) entity;

        final DialogHelper dialogHelper = new DialogHelper(getDockActivity());
        dialogHelper.initDeleteDialoge(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jobDonePosition = position;
                serviceHelper.enqueueCall(headerWebService.deleteMessage(ent.getId() + ""), DeleteChat);
                dialogHelper.hideDialog();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogHelper.hideDialog();
            }
        }, getDockActivity().getResources().getString(R.string.delete_message), getDockActivity().getResources().getString(R.string.are_you_sure_you_want_to_delete_message));

        dialogHelper.showDialog();

    }


    @OnClick({R.id.ll_send_btn, R.id.txtMessageBox})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_send_btn:
                if (isValidate())
                    serviceHelper.enqueueCall(headerWebService.sendMessage(ReceivedId, txtMessageBox.getText().toString()), sendMessage);
                break;
            case R.id.txtMessageBox:

                break;
        }
    }

    private void setTouchListner(final int position) {
        txtMessageBox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            lvChatBox.scrollToPosition(position);
                        }
                    }, 1000);
                }
                return false;
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getDockActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getMainActivity().showAdds();
    }

}
