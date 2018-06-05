package com.app.fandirect.ui.binders;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;

import com.app.fandirect.R;
import com.app.fandirect.activities.DockActivity;
import com.app.fandirect.entities.MessageThreadsEnt;
import com.app.fandirect.fragments.SpProfileFragment;
import com.app.fandirect.fragments.UserProfileFragment;
import com.app.fandirect.helpers.BasePreferenceHelper;
import com.app.fandirect.helpers.DateHelper;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.ui.viewbinders.abstracts.ViewBinder;
import com.app.fandirect.ui.views.AnyTextView;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.app.fandirect.global.AppConstants.UserRoleId;

/**
 * Created by saeedhyder on 3/14/2018.
 */

public class MessageBinder extends ViewBinder<MessageThreadsEnt> {

    private DockActivity dockActivity;
    private BasePreferenceHelper prefHelper;
    private ImageLoader imageLoader;
    private RecyclerViewItemListener clickListner;


    public MessageBinder(DockActivity dockActivity, BasePreferenceHelper prefHelper, RecyclerViewItemListener clickListner) {
        super(R.layout.row_item_message);
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
    public void bindView(final MessageThreadsEnt entity, final int position, int grpPosition, View view, Activity activity) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.mainFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListner.onRecyclerItemClicked(entity,position);
            }
        });

        if (entity != null && entity.getSenderDetail() != null && entity.getReceiverDetail() != null) {
            if (entity.getSenderDetail().getId().equals(prefHelper.getUser().getId())) {

                if (entity.getReceiverDetail().getImageUrl() != null)
                    imageLoader.displayImage(entity.getReceiverDetail().getImageUrl(), viewHolder.ivImage);
                viewHolder.txtName.setText(entity.getReceiverDetail().getUserName() + "");
                //  viewHolder.txtTime.setText(DateHelper.getFormatedDate("yyyy-MM-dd HH:mm:ss", "hh:mm a", entity.getCreatedAt()));
                viewHolder.txtTime.setText(DateHelper.getLocalDate(entity.getCreatedAt()));
                viewHolder.txtMessage.setText(entity.getMessage() + "");

                viewHolder.ivImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (entity.getReceiverDetail().getRoleId().equals(UserRoleId)) {
                            dockActivity.replaceDockableFragment(UserProfileFragment.newInstance(entity.getReceiverDetail().getId() + ""), "UserProfileFragment");
                        } else {
                            dockActivity.replaceDockableFragment(SpProfileFragment.newInstance(entity.getReceiverDetail().getId() + ""), "SpProfileFragment");
                        }
                    }
                });

            } else {
                if (entity.getSenderDetail().getImageUrl() != null)
                    imageLoader.displayImage(entity.getSenderDetail().getImageUrl(), viewHolder.ivImage);
                viewHolder.txtName.setText(entity.getSenderDetail().getUserName() + "");
                //    viewHolder.txtTime.setText(DateHelper.getFormatedDate("yyyy-MM-dd HH:mm:ss", "hh:mm a", entity.getCreatedAt()));
                viewHolder.txtTime.setText(DateHelper.getLocalDate(entity.getCreatedAt()));
                viewHolder.txtMessage.setText(entity.getMessage() + "");

                viewHolder.ivImage.setOnClickListener(new View.OnClickListener() {
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
        }


    }

    static class ViewHolder extends BaseViewHolder {
        @BindView(R.id.iv_image)
        CircleImageView ivImage;
        @BindView(R.id.txt_name)
        AnyTextView txtName;
        @BindView(R.id.txt_time)
        AnyTextView txtTime;
        @BindView(R.id.txt_message)
        AnyTextView txtMessage;
        @BindView(R.id.ll_message)
        LinearLayout llMessage;
        @BindView(R.id.mainFrameLayout)
        LinearLayout mainFrameLayout;


        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
