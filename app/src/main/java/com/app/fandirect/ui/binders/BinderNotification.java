package com.app.fandirect.ui.binders;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.app.fandirect.R;
import com.app.fandirect.entities.NotificationEnt;
import com.app.fandirect.global.AppConstants;
import com.app.fandirect.helpers.BasePreferenceHelper;
import com.app.fandirect.helpers.DateHelper;
import com.app.fandirect.ui.viewbinders.abstracts.ViewBinder;
import com.app.fandirect.ui.views.AnyTextView;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by khan_muhammad on 9/15/2017.
 */

public class BinderNotification extends ViewBinder<NotificationEnt> {

    private Context context;
    private ImageLoader imageLoader;
    private BasePreferenceHelper preferenceHelper;

    public BinderNotification(Context context, BasePreferenceHelper prefHelper) {
        super(R.layout.fragment_notification_item);
        this.context = context;
        this.preferenceHelper = prefHelper;
        imageLoader = ImageLoader.getInstance();
    }

    @Override
    public BaseViewHolder createViewHolder(View view) {
        return new BinderNotification.ViewHolder(view);
    }

    @Override
    public void bindView(NotificationEnt entity, int position, int grpPosition, View view, Activity activity) {

        BinderNotification.ViewHolder viewHolder = (BinderNotification.ViewHolder) view.getTag();


        /*if(entity.getCreatedAt() != null && entity.getCreatedAt().length() > 0) {
            viewHolder.tv_date.setText(DateHelper.dateFormat(entity.getCreatedAt(), DateHelper.DATE_FORMAT, DateHelper.DATE_TIME_FORMAT));
            viewHolder.tv_time.setText(DateHelper.dateFormat(entity.getCreatedAt(), DateHelper.TIME_FORMAT, DateHelper.DATE_TIME_FORMAT));
        }*/
    }

    public static class ViewHolder extends BaseViewHolder {

        ImageView ivNotification;
        AnyTextView tv_msg;
        AnyTextView tv_date;
        AnyTextView tv_time;

        public ViewHolder(View view) {

            ivNotification = (ImageView) view.findViewById(R.id.ivNotification);
            tv_msg = (AnyTextView) view.findViewById(R.id.tv_msg);
            tv_date = (AnyTextView) view.findViewById(R.id.tv_date);
            tv_time = (AnyTextView) view.findViewById(R.id.tv_time);
        }
    }
}
