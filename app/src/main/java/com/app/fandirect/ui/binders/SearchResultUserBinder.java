package com.app.fandirect.ui.binders;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.app.fandirect.R;
import com.app.fandirect.activities.DockActivity;
import com.app.fandirect.entities.UserEnt;
import com.app.fandirect.helpers.BasePreferenceHelper;
import com.app.fandirect.helpers.DialogHelper;
import com.app.fandirect.interfaces.userProfileClick;
import com.app.fandirect.ui.viewbinders.abstracts.ViewBinder;
import com.app.fandirect.ui.views.AnyTextView;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.app.fandirect.global.AppConstants.REQUEST_ACCEPTED;
import static com.app.fandirect.global.AppConstants.REQUEST_PENDING;
import static com.app.fandirect.global.WebServiceConstants.AddFann;
import static com.app.fandirect.global.WebServiceConstants.Unfriend;
import static com.app.fandirect.global.WebServiceConstants.cancelled;
import static com.app.fandirect.global.WebServiceConstants.confirmRequest;

/**
 * Created by saeedhyder on 5/14/2018.
 */

public class SearchResultUserBinder extends ViewBinder<UserEnt> {

    private DockActivity dockActivity;
    private BasePreferenceHelper prefHelper;
    private ImageLoader imageLoader;
    private userProfileClick profileClick;


    public SearchResultUserBinder(DockActivity dockActivity, BasePreferenceHelper prefHelper, userProfileClick profileClick) {
        super(R.layout.row_item_search_user);
        this.dockActivity = dockActivity;
        this.prefHelper = prefHelper;
        this.imageLoader = ImageLoader.getInstance();
        this.profileClick = profileClick;
    }

    @Override
    public BaseViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void bindView(UserEnt entity, int position, int grpPosition, View view, Activity activity) {
        final int pos = position;
        final UserEnt ent = entity;
        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.image.setImageResource(R.drawable.placeholder);
        if (entity.getImageUrl() != null) {
            imageLoader.displayImage(entity.getImageUrl() + "", viewHolder.image);
        }
        viewHolder.txtName.setText(entity.getUserName() + "");
        viewHolder.txtLocation.setText(entity.getLocation() + "");

        if (entity.getFanStatus() != null && entity.getFanStatus().getSenderId() != null && entity.getFanStatus().getStatus() != null) {
            if (entity.getFanStatus().getSenderId().equals(prefHelper.getUser().getId())) {
                if (entity.getFanStatus().getStatus().equals(REQUEST_PENDING)) {
                    viewHolder.btnRequest.setBackground(dockActivity.getResources().getDrawable(R.drawable.rounded_button));
                    viewHolder.btnRequest.setTextColor(dockActivity.getResources().getColor(R.color.white));
                    viewHolder.btnRequest.setText(R.string.request_sent);
                } else if (entity.getFanStatus().getStatus().equals(REQUEST_ACCEPTED)) {
                    viewHolder.btnRequest.setBackground(dockActivity.getResources().getDrawable(R.drawable.rounded_button));
                    viewHolder.btnRequest.setTextColor(dockActivity.getResources().getColor(R.color.white));
                    viewHolder.btnRequest.setText(R.string.fann);
                } else {
                    viewHolder.btnRequest.setBackground(dockActivity.getResources().getDrawable(R.drawable.rounder_button_white));
                    viewHolder.btnRequest.setTextColor(dockActivity.getResources().getColor(R.color.app_blue));
                    viewHolder.btnRequest.setText(R.string.add_fann);
                }
            } else if (entity.getFanStatus().getReceiverId().equals(prefHelper.getUser().getId())) {
                if (entity.getFanStatus().getStatus().equals(REQUEST_PENDING)) {
                    viewHolder.btnRequest.setBackground(dockActivity.getResources().getDrawable(R.drawable.rounded_button));
                    viewHolder.btnRequest.setTextColor(dockActivity.getResources().getColor(R.color.white));
                    viewHolder.btnRequest.setText(R.string.confirm);
                } else if (entity.getFanStatus().getStatus().equals(REQUEST_ACCEPTED)) {
                    viewHolder.btnRequest.setBackground(dockActivity.getResources().getDrawable(R.drawable.rounded_button));
                    viewHolder.btnRequest.setTextColor(dockActivity.getResources().getColor(R.color.white));
                    viewHolder.btnRequest.setText(R.string.fann);
                } else {
                    viewHolder.btnRequest.setBackground(dockActivity.getResources().getDrawable(R.drawable.rounder_button_white));
                    viewHolder.btnRequest.setTextColor(dockActivity.getResources().getColor(R.color.app_blue));
                    viewHolder.btnRequest.setText(R.string.add_fann);
                }
            }else {
                viewHolder.btnRequest.setBackground(dockActivity.getResources().getDrawable(R.drawable.rounder_button_white));
                viewHolder.btnRequest.setTextColor(dockActivity.getResources().getColor(R.color.app_blue));
                viewHolder.btnRequest.setText(R.string.add_fann);
            }
        }else {
            viewHolder.btnRequest.setBackground(dockActivity.getResources().getDrawable(R.drawable.rounder_button_white));
            viewHolder.btnRequest.setTextColor(dockActivity.getResources().getColor(R.color.app_blue));
            viewHolder.btnRequest.setText(R.string.add_fann);
        }

        viewHolder.btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRequestClick(viewHolder, ent, pos);
            }
        });
        viewHolder.llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileClick.onUserProfileClick(ent, pos);
            }
        });
    }

    private void btnRequestClick(final ViewHolder viewHolder, final UserEnt ent, final int pos) {

        if (viewHolder.btnRequest.getText().toString().equals(dockActivity.getResources().getString(R.string.add_fann))) {
            viewHolder.btnRequest.setBackground(dockActivity.getResources().getDrawable(R.drawable.rounded_button));
            viewHolder.btnRequest.setTextColor(dockActivity.getResources().getColor(R.color.white));
            viewHolder.btnRequest.setText(dockActivity.getResources().getString(R.string.request_sent));
            profileClick.onUserAddFriendBtn(ent, pos, AddFann);

        } else if (viewHolder.btnRequest.getText().toString().equals(dockActivity.getResources().getString(R.string.request_sent))) {
            final DialogHelper dialogHelper = new DialogHelper(dockActivity);
            dialogHelper.initCancelDialoge(R.layout.cancel_request_dialoge, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.btnRequest.setBackground(dockActivity.getResources().getDrawable(R.drawable.rounder_button_white));
                    viewHolder.btnRequest.setTextColor(dockActivity.getResources().getColor(R.color.app_blue));
                    viewHolder.btnRequest.setText(R.string.add_fann);
                    profileClick.onUserAddFriendBtn(ent, pos, cancelled);
                    //  serviceHelper.enqueueCall(headerWebService.markFannRequset(entity.getFanStatus().getId() + "", REQUEST_CANCELLED), cancelled);
                    dialogHelper.hideDialog();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogHelper.hideDialog();
                }
            });
            dialogHelper.showDialog();

        } else if (viewHolder.btnRequest.getText().toString().equals(dockActivity.getResources().getString(R.string.fann))) {
            final DialogHelper dialogHelper = new DialogHelper(dockActivity);

            dialogHelper.initUnfriendDialoge(R.layout.unfriend_dialoge, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.btnRequest.setBackground(dockActivity.getResources().getDrawable(R.drawable.rounder_button_white));
                    viewHolder.btnRequest.setTextColor(dockActivity.getResources().getColor(R.color.app_blue));
                    viewHolder.btnRequest.setText(R.string.add_fann);
                    profileClick.onUserAddFriendBtn(ent, pos, Unfriend);
                    // serviceHelper.enqueueCall(headerWebService.markFannRequset(entity.getFanStatus().getId() + "", REQUEST_UNFRIEND), Unfriend);
                    dialogHelper.hideDialog();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialogHelper.hideDialog();
                }
            });
            dialogHelper.showDialog();

        } else if (viewHolder.btnRequest.getText().toString().equals(dockActivity.getResources().getString(R.string.confirm))) {
            viewHolder.btnRequest.setBackground(dockActivity.getResources().getDrawable(R.drawable.rounded_button));
            viewHolder.btnRequest.setTextColor(dockActivity.getResources().getColor(R.color.white));
            viewHolder.btnRequest.setText(R.string.fann);
            profileClick.onUserAddFriendBtn(ent, pos, confirmRequest);
            //.enqueueCall(headerWebService.markFannRequset(entity.getFanStatus().getId() + "", accepted), confirmRequest);

        }
    }

    static class ViewHolder extends BaseViewHolder {
        @BindView(R.id.image)
        CircleImageView image;
        @BindView(R.id.txt_name)
        AnyTextView txtName;
        @BindView(R.id.txt_location)
        AnyTextView txtLocation;
        @BindView(R.id.btn_request)
        Button btnRequest;
        @BindView(R.id.ll_item)
        LinearLayout llItem;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
