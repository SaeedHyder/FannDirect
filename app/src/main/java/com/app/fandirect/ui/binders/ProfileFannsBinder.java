package com.app.fandirect.ui.binders;

import android.app.Activity;
import android.view.View;

import com.app.fandirect.R;
import com.app.fandirect.activities.DockActivity;
import com.app.fandirect.entities.GetMyFannsEnt;
import com.app.fandirect.helpers.BasePreferenceHelper;
import com.app.fandirect.helpers.DialogHelper;
import com.app.fandirect.interfaces.UserItemClickInterface;
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
import static com.app.fandirect.global.WebServiceConstants.showProfile;

/**
 * Created by saeedhyder on 3/2/2018.
 */

public class ProfileFannsBinder extends ViewBinder<GetMyFannsEnt> {

    private DockActivity dockActivity;
    private BasePreferenceHelper prefHelper;
    private ImageLoader imageLoader;
    private UserItemClickInterface userItemClick;
    private String userId;
    private String id;
    private boolean isMutualFann;


    public ProfileFannsBinder(DockActivity dockActivity, BasePreferenceHelper prefHelper, UserItemClickInterface userItemClick, String userId,boolean isMutualFann) {
        super(R.layout.row_item_fanns);
        this.dockActivity = dockActivity;
        this.prefHelper = prefHelper;
        this.imageLoader = ImageLoader.getInstance();
        this.userItemClick = userItemClick;
        this.userId = userId;
        this.isMutualFann=isMutualFann;

    }

    @Override
    public BaseViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void bindView(final GetMyFannsEnt entity, final int position, int grpPosition, View view, Activity activity) {

        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.ivImage.setImageResource(R.drawable.placeholder);

        if (userId != null) {
            id = userId;
        } else {
            id = prefHelper.getUser().getId();
        }
        if(isMutualFann){
            viewHolder.btnAddFann.setVisibility(View.GONE);
        }else{
            viewHolder.btnAddFann.setVisibility(View.VISIBLE);
        }


        if (entity.getSenderDetail()!=null && entity.getSenderDetail().getId() != null && entity.getSenderDetail().getId().equals(id)) {
            if (entity.getReceiverDetail() != null) {
                if (entity.getReceiverDetail().getImageUrl() != null)
                    imageLoader.displayImage(entity.getReceiverDetail().getImageUrl(), viewHolder.ivImage);
                viewHolder.txtName.setText(entity.getReceiverDetail().getUserName() + "");
                //  id = entity.getReceiverDetail().getId();

            }
        } else {
            if (entity.getSenderDetail() != null) {
                if (entity.getSenderDetail().getImageUrl() != null)
                    imageLoader.displayImage(entity.getSenderDetail().getImageUrl(), viewHolder.ivImage);
                viewHolder.txtName.setText(entity.getSenderDetail().getUserName() + "");
                //id = entity.getSenderDetail().getId();

            }
        }

        viewHolder.btnAddFann.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRequestClick(viewHolder, entity, position, id);
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userItemClick.profileClick(entity, position, userId);
            }
        });

        if (userId != null && !userId.equals(prefHelper.getUser().getId())) {
            if (!(entity.getSenderId().equals(prefHelper.getUser().getId()) || entity.getReceiverId().equals(prefHelper.getUser().getId()))) {
                if (entity.getFanStatus() != null && entity.getFanStatus().getSenderId() != null && entity.getFanStatus().getStatus() != null) {
                    if (entity.getFanStatus().getSenderId().equals(prefHelper.getUser().getId())) {
                        if (entity.getFanStatus().getStatus().equals(REQUEST_PENDING)) {
                            viewHolder.btnAddFann.setBackground(dockActivity.getResources().getDrawable(R.drawable.rounded_button));
                            viewHolder.btnAddFann.setTextColor(dockActivity.getResources().getColor(R.color.white));
                            viewHolder.btnAddFann.setText(R.string.request_sent);
                        } else if (entity.getFanStatus().getStatus().equals(REQUEST_ACCEPTED)) {
                            viewHolder.btnAddFann.setBackground(dockActivity.getResources().getDrawable(R.drawable.rounded_button));
                            viewHolder.btnAddFann.setTextColor(dockActivity.getResources().getColor(R.color.white));
                            viewHolder.btnAddFann.setText(R.string.fann);
                        } else {
                            viewHolder.btnAddFann.setBackground(dockActivity.getResources().getDrawable(R.drawable.rounder_button_white));
                            viewHolder.btnAddFann.setTextColor(dockActivity.getResources().getColor(R.color.app_blue));
                            viewHolder.btnAddFann.setText(R.string.add_fann);
                        }
                    } else if (entity.getFanStatus().getReceiverId().equals(prefHelper.getUser().getId())) {
                        if (entity.getFanStatus().getStatus().equals(REQUEST_PENDING)) {
                            viewHolder.btnAddFann.setBackground(dockActivity.getResources().getDrawable(R.drawable.rounded_button));
                            viewHolder.btnAddFann.setTextColor(dockActivity.getResources().getColor(R.color.white));
                            viewHolder.btnAddFann.setText(R.string.confirm);
                        } else if (entity.getFanStatus().getStatus().equals(REQUEST_ACCEPTED)) {
                            viewHolder.btnAddFann.setBackground(dockActivity.getResources().getDrawable(R.drawable.rounded_button));
                            viewHolder.btnAddFann.setTextColor(dockActivity.getResources().getColor(R.color.white));
                            viewHolder.btnAddFann.setText(R.string.fann);
                        } else {
                            viewHolder.btnAddFann.setBackground(dockActivity.getResources().getDrawable(R.drawable.rounder_button_white));
                            viewHolder.btnAddFann.setTextColor(dockActivity.getResources().getColor(R.color.app_blue));
                            viewHolder.btnAddFann.setText(R.string.add_fann);
                        }
                    } else {
                        viewHolder.btnAddFann.setBackground(dockActivity.getResources().getDrawable(R.drawable.rounder_button_white));
                        viewHolder.btnAddFann.setTextColor(dockActivity.getResources().getColor(R.color.app_blue));
                        viewHolder.btnAddFann.setText(R.string.add_fann);
                    }
                } else {
                    viewHolder.btnAddFann.setBackground(dockActivity.getResources().getDrawable(R.drawable.rounder_button_white));
                    viewHolder.btnAddFann.setTextColor(dockActivity.getResources().getColor(R.color.app_blue));
                    viewHolder.btnAddFann.setText(R.string.add_fann);
                }
            } else {
                viewHolder.btnAddFann.setBackground(dockActivity.getResources().getDrawable(R.drawable.rounder_button_white));
                viewHolder.btnAddFann.setTextColor(dockActivity.getResources().getColor(R.color.app_blue));
                viewHolder.btnAddFann.setText(R.string.show_profile);
            }
        } else {
            viewHolder.btnAddFann.setBackground(dockActivity.getResources().getDrawable(R.drawable.rounded_button));
            viewHolder.btnAddFann.setTextColor(dockActivity.getResources().getColor(R.color.white));
            viewHolder.btnAddFann.setText(R.string.fann);
        }

    }

    private void btnRequestClick(final ViewHolder viewHolder, final GetMyFannsEnt entity, final int position, final String id) {

        if (viewHolder.btnAddFann.getText().toString().equals(dockActivity.getResources().getString(R.string.add_fann))) {
            viewHolder.btnAddFann.setBackground(dockActivity.getResources().getDrawable(R.drawable.rounded_button));
            viewHolder.btnAddFann.setTextColor(dockActivity.getResources().getColor(R.color.white));
            viewHolder.btnAddFann.setText(dockActivity.getResources().getString(R.string.request_sent));
            //  addFriendService(entity);
            userItemClick.addFriend(entity, position, AddFann, id);
        } else if (viewHolder.btnAddFann.getText().toString().equals(dockActivity.getResources().getString(R.string.request_sent))) {
            final DialogHelper dialogHelper = new DialogHelper(dockActivity);
            dialogHelper.initCancelDialoge(R.layout.cancel_request_dialoge, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.btnAddFann.setBackground(dockActivity.getResources().getDrawable(R.drawable.rounder_button_white));
                    viewHolder.btnAddFann.setTextColor(dockActivity.getResources().getColor(R.color.app_blue));
                    viewHolder.btnAddFann.setText(R.string.add_fann);
                    userItemClick.addFriend(entity, position, cancelled, id);
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

        } else if (viewHolder.btnAddFann.getText().toString().equals(dockActivity.getResources().getString(R.string.fann))) {
            final DialogHelper dialogHelper = new DialogHelper(dockActivity);

            dialogHelper.initUnfriendDialoge(R.layout.unfriend_dialoge, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.btnAddFann.setBackground(dockActivity.getResources().getDrawable(R.drawable.rounder_button_white));
                    viewHolder.btnAddFann.setTextColor(dockActivity.getResources().getColor(R.color.app_blue));
                    viewHolder.btnAddFann.setText(R.string.add_fann);
                    userItemClick.addFriend(entity, position, Unfriend, id);
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

        } else if (viewHolder.btnAddFann.getText().toString().equals(dockActivity.getResources().getString(R.string.confirm))) {
            viewHolder.btnAddFann.setBackground(dockActivity.getResources().getDrawable(R.drawable.rounded_button));
            viewHolder.btnAddFann.setTextColor(dockActivity.getResources().getColor(R.color.white));
            viewHolder.btnAddFann.setText(R.string.fann);
            userItemClick.addFriend(entity, position, confirmRequest, id);
            //.enqueueCall(headerWebService.markFannRequset(entity.getFanStatus().getId() + "", accepted), confirmRequest);

        } else if (viewHolder.btnAddFann.getText().toString().equals(dockActivity.getResources().getString(R.string.show_profile))) {
            userItemClick.addFriend(entity, position, showProfile, id);

        }
    }

    /*private void addFriendService(GetMyFannsEnt entity) {

        String userId = "";

        if (entity != null && entity.getSenderDetail() != null && entity.getReceiverDetail() != null) {
            if (entity.getSenderDetail().getId().equals(prefHelper.getUser().getId())) {

                userId = entity.getReceiverDetail().getId() + "";

            } else {
                userId = entity.getSenderDetail().getId() + "";
            }
        }

        serviceHelper.enqueueCall(headerwebservice.addFann(userId), AddFann);
    }


    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        switch (Tag) {

            case AddFann:
                FanStatus ent=(FanStatus)result;
                entity.setFanStatus(ent);
                UIHelper.showShortToastInCenter(dockActivity, message);
                break;
        }
    }



    @Override
    public void ResponseFailure(String tag) {

    }*/

    static class ViewHolder extends BaseViewHolder {
        @BindView(R.id.iv_image)
        CircleImageView ivImage;
        @BindView(R.id.txt_name)
        AnyTextView txtName;
        @BindView(R.id.btn_add_fann)
        AnyTextView btnAddFann;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
