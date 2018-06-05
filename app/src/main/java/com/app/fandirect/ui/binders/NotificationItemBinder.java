package com.app.fandirect.ui.binders;

import android.app.Activity;
import android.view.View;

import com.app.fandirect.R;
import com.app.fandirect.activities.DockActivity;
import com.app.fandirect.entities.NotificationEnt;
import com.app.fandirect.helpers.BasePreferenceHelper;
import com.app.fandirect.helpers.DateHelper;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.ui.viewbinders.abstracts.ViewBinder;
import com.app.fandirect.ui.views.AnyTextView;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by saeedhyder on 3/12/2018.
 */

public class NotificationItemBinder extends ViewBinder<NotificationEnt> {

    private DockActivity dockActivity;
    private BasePreferenceHelper prefHelper;
    private ImageLoader imageLoader;
    private RecyclerViewItemListener clickListner;


    public NotificationItemBinder(DockActivity dockActivity, BasePreferenceHelper prefHelper, RecyclerViewItemListener clickListner) {
        super(R.layout.row_item_notifications);
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
    public void bindView(NotificationEnt entity, int position, int grpPosition, View view, Activity activity) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        imageLoader.displayImage(entity.getSenderDetail().getImageUrl(), viewHolder.ivImage);
        viewHolder.txtName.setText(entity.getSenderDetail().getUserName() + "");
        viewHolder.txtDescription.setText(entity.getMessage() + "");
        viewHolder.txtTimeDate.setText(DateHelper.getLocalDateTime2(entity.getCreatedAt()));

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

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
