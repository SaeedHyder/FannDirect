package com.app.fandirect.ui.binders;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.app.fandirect.R;
import com.app.fandirect.activities.DockActivity;
import com.app.fandirect.entities.GetServicesEnt;
import com.app.fandirect.entities.MessageThreadsEnt;
import com.app.fandirect.fragments.SpProfileFragment;
import com.app.fandirect.fragments.UserProfileFragment;
import com.app.fandirect.helpers.BasePreferenceHelper;
import com.app.fandirect.helpers.DateHelper;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.ui.viewbinders.abstracts.RecyclerViewBinder;
import com.app.fandirect.ui.viewbinders.abstracts.ViewBinder;
import com.app.fandirect.ui.views.AnyTextView;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.app.fandirect.global.AppConstants.UserRoleId;

/**
 * Created by saeedhyder on 3/15/2018.
 */

public class ChatItemBinder extends RecyclerViewBinder<MessageThreadsEnt> {

    private DockActivity dockActivity;
    private BasePreferenceHelper prefHelper;
    private ImageLoader imageLoader;
    private RecyclerViewItemListener clickListner;


    public ChatItemBinder(DockActivity dockActivity, BasePreferenceHelper prefHelper, RecyclerViewItemListener clickListner) {
        super(R.layout.row_item_chat);
        this.dockActivity = dockActivity;
        this.prefHelper = prefHelper;
        this.imageLoader = ImageLoader.getInstance();
        this.clickListner = clickListner;
    }

    @Override
    public BaseViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }



    @Override
    public void bindView(final MessageThreadsEnt entity, int position, Object viewHolder, Context context) {

        ViewHolder holder = (ViewHolder) viewHolder;

        boolean isSender = false;


        if (entity.getSenderId().equals(String.valueOf(prefHelper.getUser().getId()))) {
            isSender = true;
        } else if (entity.getReceiverId().equals(String.valueOf(prefHelper.getUser().getId()))) {
            isSender = false;
        }

        if (isSender) {
            if (entity.getSenderDetail().getImageUrl() != null)
                imageLoader.displayImage(entity.getSenderDetail().getImageUrl(), holder.imageViewRight);
            holder.txtReceiverChatRight.setText(entity.getMessage());
            holder.txtReceiverDatetRight.setText(DateHelper.getLocalDateTime(entity.getCreatedAt()));
            holder.leftLayout.setVisibility(View.GONE);


        } else {
            if (entity.getSenderDetail().getImageUrl() != null)
            imageLoader.displayImage(entity.getSenderDetail().getImageUrl(), holder.imageViewLeft);
            holder.txtSenderChatLeft.setText(entity.getMessage());
            holder.txtSenderDateLeft.setText(DateHelper.getLocalDateTime(entity.getCreatedAt()));
            holder.RightLayout.setVisibility(View.GONE);

        }


        holder.imageViewRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (entity.getSenderDetail().getRoleId().equals(UserRoleId)) {
                    dockActivity.replaceDockableFragment(UserProfileFragment.newInstance(entity.getSenderDetail().getId() + ""), "UserProfileFragment");
                } else {
                    dockActivity.replaceDockableFragment(SpProfileFragment.newInstance(entity.getSenderDetail().getId() + ""), "SpProfileFragment");
                }
            }
        });

        holder.imageViewLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (entity.getSenderDetail().getRoleId().equals(UserRoleId)) {
                    dockActivity.replaceDockableFragment(UserProfileFragment.newInstance(entity.getSenderDetail().getId() + ""), "UserProfileFragment");
                } else {
                    dockActivity.replaceDockableFragment(SpProfileFragment.newInstance(entity.getSenderDetail().getId() + ""), "SpProfileFragment");
                }
            }
        });


    }


    static class ViewHolder extends BaseViewHolder {
        @BindView(R.id.imageViewLeft)
        CircleImageView imageViewLeft;
        @BindView(R.id.txtSenderChatLeft)
        AnyTextView txtSenderChatLeft;
        @BindView(R.id.leftLayoutChild)
        LinearLayout leftLayoutChild;
        @BindView(R.id.leftLayout)
        RelativeLayout leftLayout;
        @BindView(R.id.imageViewRight)
        ImageView imageViewRight;
        @BindView(R.id.txtReceiverChatRight)
        AnyTextView txtReceiverChatRight;
        @BindView(R.id.txtReceiverDatetRight)
        AnyTextView txtReceiverDatetRight;
        @BindView(R.id.txtSenderDateLeft)
        AnyTextView txtSenderDateLeft;
        @BindView(R.id.rightLayout)
        LinearLayout rightLayout;
        @BindView(R.id.RightLayout)
        RelativeLayout RightLayout;
        @BindView(R.id.mainFrameLayout)
        LinearLayout mainFrameLayout;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
