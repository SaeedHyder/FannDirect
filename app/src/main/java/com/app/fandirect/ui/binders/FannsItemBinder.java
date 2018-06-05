package com.app.fandirect.ui.binders;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;

import com.app.fandirect.R;
import com.app.fandirect.activities.DockActivity;
import com.app.fandirect.entities.GetMyFannsEnt;
import com.app.fandirect.fragments.SpProfileFragment;
import com.app.fandirect.fragments.UserProfileFragment;
import com.app.fandirect.helpers.BasePreferenceHelper;
import com.app.fandirect.interfaces.MyFannsInterface;
import com.app.fandirect.ui.viewbinders.abstracts.ViewBinder;
import com.app.fandirect.ui.views.AnyTextView;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.app.fandirect.global.AppConstants.UserRoleId;

/**
 * Created by saeedhyder on 3/5/2018.
 */

public class FannsItemBinder extends ViewBinder<GetMyFannsEnt> {

    private DockActivity dockActivity;
    private BasePreferenceHelper prefHelper;
    private ImageLoader imageLoader;
    private MyFannsInterface myFannsInterface;
    private String userId;
    private String roleId;


    public FannsItemBinder(DockActivity dockActivity, BasePreferenceHelper prefHelper, MyFannsInterface myFannsInterface) {
        super(R.layout.row_item_friends);
        this.dockActivity = dockActivity;
        this.prefHelper = prefHelper;
        this.imageLoader = ImageLoader.getInstance();
        this.myFannsInterface = myFannsInterface;
    }

    @Override
    public BaseViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void bindView(final GetMyFannsEnt entity, final int position, int grpPosition, View view, Activity activity) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.ivImage.setImageResource(R.drawable.placeholder);

        if (entity.getSenderDetail().getId().equals(String.valueOf(prefHelper.getUser().getId()))) {
            if (entity.getReceiverDetail() != null) {
                if (entity.getReceiverDetail().getImageUrl() != null)
                    imageLoader.displayImage(entity.getReceiverDetail().getImageUrl(), viewHolder.ivImage);
                viewHolder.txtName.setText(entity.getReceiverDetail().getUserName() + "");

            }
        } else if (entity.getSenderDetail() != null) {
                if (entity.getSenderDetail().getImageUrl() != null)
                    imageLoader.displayImage(entity.getSenderDetail().getImageUrl(), viewHolder.ivImage);
                viewHolder.txtName.setText(entity.getSenderDetail().getUserName() + "");


        }
  /*viewHolder.ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (entity.getSenderDetail().getId().equals(String.valueOf(prefHelper.getUser().getId()))) {
                    if (entity.getReceiverDetail() != null) {
                        userId = entity.getReceiverDetail().getId() + "";
                        roleId = entity.getReceiverDetail().getRoleId() + "";
                    }
                } else {
                    userId = entity.getSenderDetail().getId() + "";
                    roleId = entity.getSenderDetail().getRoleId() + "";
                }

                if (roleId.equals(UserRoleId)) {
                    dockActivity.replaceDockableFragment(UserProfileFragment.newInstance(userId), "UserProfileFragment");
                } else {
                    dockActivity.replaceDockableFragment(SpProfileFragment.newInstance(userId), "SpProfileFragment");
                }
            }
        });*/


        viewHolder.llFanns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myFannsInterface.getMyFanns(entity, position);
            }
        });
    }


    static class ViewHolder extends BaseViewHolder {
        @BindView(R.id.iv_image)
        CircleImageView ivImage;
        @BindView(R.id.txt_name)
        AnyTextView txtName;
        @BindView(R.id.ll_fanns)
        LinearLayout llFanns;


        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
