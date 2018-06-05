package com.app.fandirect.ui.binders;

import android.app.Activity;
import android.view.View;
import android.widget.Button;

import com.app.fandirect.R;
import com.app.fandirect.activities.DockActivity;
import com.app.fandirect.entities.ServiceHistoryEnt;
import com.app.fandirect.fragments.SpProfileFragment;
import com.app.fandirect.fragments.UserProfileFragment;
import com.app.fandirect.global.AppConstants;
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
 * Created by saeedhyder on 3/13/2018.
 */

public class ServiceHistoryBinder extends ViewBinder<ServiceHistoryEnt> {

    private DockActivity dockActivity;
    private BasePreferenceHelper prefHelper;
    private ImageLoader imageLoader;
    private RecyclerViewItemListener clickListner;
    private String userId="";
    private String roleId="";


    public ServiceHistoryBinder(DockActivity dockActivity, BasePreferenceHelper prefHelper, RecyclerViewItemListener clickListner) {
        super(R.layout.row_item_service_history);
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
    public void bindView(final ServiceHistoryEnt entity, final int position, int grpPosition, View view, Activity activity) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.ivImage.setImageResource(R.drawable.placeholder);

        if (entity.getReceiverDetail().getImageUrl() != null)
            imageLoader.displayImage(entity.getReceiverDetail().getImageUrl(), viewHolder.ivImage);
        viewHolder.txtName.setText(entity.getReceiverDetail().getUserName() + "");
        viewHolder.txtDescription.setText(entity.getDescription() + "");
        viewHolder.txtTimeDate.setText(DateHelper.getLocalDateTime2(entity.getCreatedAt()));

        if (entity.getStatus().equals(AppConstants.accepted)) {
            viewHolder.status.setBackground(dockActivity.getResources().getDrawable(R.drawable.rounded_btn_view_detail));
            viewHolder.status.setTextColor(dockActivity.getResources().getColor(R.color.app_dark_gray_2));
            viewHolder.status.setText("In Progress");
        } else if (entity.getStatus().equals(AppConstants.pending)) {
            viewHolder.status.setBackground(dockActivity.getResources().getDrawable(R.drawable.rounder_button_white));
            viewHolder.status.setTextColor(dockActivity.getResources().getColor(R.color.app_blue));
            viewHolder.status.setText("Pending");

        } else if (entity.getStatus().equals(AppConstants.completed)) {
            viewHolder.status.setBackground(dockActivity.getResources().getDrawable(R.drawable.rounded_button));
            viewHolder.status.setTextColor(dockActivity.getResources().getColor(R.color.white));
            viewHolder.status.setText("Completed");
        } else if (entity.getStatus().equals(AppConstants.rejected)) {
            viewHolder.status.setBackground(dockActivity.getResources().getDrawable(R.drawable.rounded_btn_inprogess));
            viewHolder.status.setTextColor(dockActivity.getResources().getColor(R.color.white));
            viewHolder.status.setText(R.string.rejected);

        } else {
            viewHolder.status.setBackground(dockActivity.getResources().getDrawable(R.drawable.rounded_button));
            viewHolder.status.setTextColor(dockActivity.getResources().getColor(R.color.white));
            viewHolder.status.setText("Completed");
        }

        viewHolder.status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListner.onRecyclerItemClicked(entity, position);
            }
        });



        viewHolder.ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(entity.getSenderId().equals(String.valueOf(prefHelper.getUser().getId()))){
                    userId=entity.getReceiverId();
                    roleId=entity.getReceiverDetail().getRoleId();
                }else{
                    userId=entity.getSenderId();
                    roleId=entity.getSenderDetail().getRoleId();
                }
                if (roleId.equals(UserRoleId)) {
                    dockActivity.replaceDockableFragment(UserProfileFragment.newInstance(userId), "UserProfileFragment");
                } else {
                    dockActivity.replaceDockableFragment(SpProfileFragment.newInstance(userId), "SpProfileFragment");
                }
            }
        });
    }


    static class ViewHolder extends BaseViewHolder {
        @BindView(R.id.iv_image)
        CircleImageView ivImage;
        @BindView(R.id.txt_name)
        AnyTextView txtName;
        @BindView(R.id.txt_description)
        AnyTextView txtDescription;
        @BindView(R.id.txt_time_date)
        AnyTextView txtTimeDate;
        @BindView(R.id.status)
        Button status;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
