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
import com.app.fandirect.helpers.DialogHelper;
import com.app.fandirect.helpers.UIHelper;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.ui.adapters.ArrayListAdapter;
import com.app.fandirect.ui.binders.NotificationItemBinder;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.TitleBar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.app.fandirect.global.AppConstants.Accept_Request;
import static com.app.fandirect.global.AppConstants.Complete_Request;
import static com.app.fandirect.global.AppConstants.New_Request;
import static com.app.fandirect.global.AppConstants.Rejected_Request;
import static com.app.fandirect.global.AppConstants.UserRoleId;
import static com.app.fandirect.global.AppConstants.accept_request;
import static com.app.fandirect.global.AppConstants.block_user;
import static com.app.fandirect.global.AppConstants.comment;
import static com.app.fandirect.global.AppConstants.delete_user;
import static com.app.fandirect.global.AppConstants.friend_request;
import static com.app.fandirect.global.AppConstants.messagePush;
import static com.app.fandirect.global.AppConstants.post;
import static com.app.fandirect.global.AppConstants.promotion;
import static com.app.fandirect.global.WebServiceConstants.feedback;
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

        serviceHelper.enqueueCall(headerWebService.getNotification(), getNotification);



    }

    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        super.ResponseSuccess(result, Tag, message);
        switch (Tag) {

            case getNotification:
                ArrayList<NotificationEnt> entity = (ArrayList<NotificationEnt>) result;

                setListViewData(entity);

                break;

            case feedback:
                UIHelper.showShortToastInCenter(getDockActivity(), "Rating submitted successfully");
                getDockActivity().popBackStackTillEntry(0);
                getDockActivity().replaceDockableFragment(HomeFragment.newInstance(), "HomeFragment");
                break;
        }
    }

    private void setListViewData(ArrayList<NotificationEnt> entity) {

        collection = new ArrayList<>();
        collection.addAll(entity);

        if (entity.size() > 0) {
            lvNotification.setVisibility(View.VISIBLE);
            txtNoData.setVisibility(View.GONE);
        } else {
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
        NotificationEnt entity = (NotificationEnt) Ent;

        String Type = entity.getType();
        final String ActionId = entity.getAction_id();
        final String SenderId = entity.getSenderId();
        final String ReceiverId = entity.getReceiverId();

        String userId = "";

        if (SenderId.equals(String.valueOf(prefHelper.getUser().getId()))) {
            userId = ReceiverId;
        } else {
            userId = SenderId;
        }


        if (Type != null && Type.equals(messagePush)) {
            inboxChat(SenderId, ReceiverId);
        } else if (Type != null && Type.equals(post)) {
            getDockActivity().replaceDockableFragment(FeedFragment.newInstance(), "FeedFragment");
        } else if (Type != null && Type.equals(promotion)) {
            getDockActivity().replaceDockableFragment(PromotionsFragment.newInstance(), "PromotionsFragment");
        }  else if (Type != null && Type.equals(New_Request)) {
            getDockActivity().replaceDockableFragment(ServiceHistoryMainSpFragment.newInstance(), "ServiceHistoryMainSpFragment");
        } else if (Type != null && Type.equals(Rejected_Request)) {
            getDockActivity().replaceDockableFragment(HomeFragment.newInstance(), "HomeMapFragment");
        } else if (Type != null && Type.equals(Accept_Request)) {
            getDockActivity().replaceDockableFragment(ServiceHistoryMainUserFragment.newInstance(), "ServiceHistoryMainUserFragment");
        } else if (Type != null && Type.equals(friend_request)) {
            if (ActionId!=null && ActionId.equals(UserRoleId)) {
                getDockActivity().replaceDockableFragment(UserProfileFragment.newInstance(userId), "UserProfileFragment");
            } else {
                getDockActivity().replaceDockableFragment(SpProfileFragment.newInstance(userId), "SpProfileFragment");
            }
          //  getDockActivity().replaceDockableFragment(FriendRequestFragment.newInstance(), "FriendRequestFragment");
        } else if (Type != null && Type.equals(accept_request)) {
            if (ActionId!=null && ActionId.equals(UserRoleId)) {
                getDockActivity().replaceDockableFragment(UserProfileFragment.newInstance(userId), "UserProfileFragment");
            } else {
                getDockActivity().replaceDockableFragment(SpProfileFragment.newInstance(userId), "SpProfileFragment");
            }
          //  getDockActivity().replaceDockableFragment(FannsFragment.newInstance(), "FannsFragment");
        } else if (Type != null && Type.equals(comment) && ActionId != null) {
            getDockActivity().replaceDockableFragment(PostDetailFragment.newInstance(ActionId), "CommentFragment");
        } else if (Type != null && Type.equals(Complete_Request) && ActionId != null) {
            final DialogHelper dialogHelper = new DialogHelper(getDockActivity());
            dialogHelper.initRating(R.layout.submit_rating_dialoge, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    feedbackService(ActionId, SenderId, ReceiverId, dialogHelper.getRatingTextView().getText().toString(), dialogHelper.getRatingScore(), dialogHelper);
                }
            });
            dialogHelper.showDialog();
        }
    }

    private void inboxChat(String senderId, String receiverId) {

        String userId = "";
        if (senderId.equals(prefHelper.getUser().getId())) {
            userId = receiverId;
        } else {
            userId = senderId;
        }

        getDockActivity().replaceDockableFragment(InboxChatFragment.newInstance(userId, true), "InboxChatFragment");
    }

    private void feedbackService(String requestId, String senderId, String receiverId, String text, Float ratingScore, DialogHelper dialogHelper) {

        String userId = "";
        if (senderId.equals(String.valueOf(prefHelper.getUser().getId()))) {
            userId = receiverId;
        } else {
            userId = senderId;
        }

        if (text != null && !text.trim().equals("")) {
            dialogHelper.hideDialog();
            serviceHelper.enqueueCall(headerWebService.feedback(userId, requestId, String.valueOf(Math.round(ratingScore)), text), feedback);
        } else {
            UIHelper.showShortToastInCenter(getDockActivity(), "Write Feedback to proceed");
        }


    }

    @Override
    public void onCategoryClick(Object Ent, int position) {

    }
}
