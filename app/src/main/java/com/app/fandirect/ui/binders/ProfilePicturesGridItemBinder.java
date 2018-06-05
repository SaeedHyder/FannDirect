package com.app.fandirect.ui.binders;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.fandirect.R;
import com.app.fandirect.activities.DockActivity;
import com.app.fandirect.entities.Picture;
import com.app.fandirect.fragments.ProfilePictureDetail;
import com.app.fandirect.helpers.BasePreferenceHelper;
import com.app.fandirect.helpers.DialogHelper;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.ui.viewbinders.abstracts.ViewBinder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import rm.com.longpresspopup.LongPressPopup;
import rm.com.longpresspopup.LongPressPopupBuilder;
import rm.com.longpresspopup.PopupInflaterListener;

/**
 * Created by saeedhyder on 3/1/2018.
 */

public class ProfilePicturesGridItemBinder extends ViewBinder<Picture> {

    private DockActivity dockActivity;
    private BasePreferenceHelper prefHelper;
    private ImageLoader imageLoader;
    private LongPressPopup popup ;


    public ProfilePicturesGridItemBinder(DockActivity dockActivity, BasePreferenceHelper prefHelper) {
        super(R.layout.row_item_profile_pictures);
        this.dockActivity = dockActivity;
        this.prefHelper = prefHelper;
        this.imageLoader = ImageLoader.getInstance();

    }

    @Override
    public BaseViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void bindView(final Picture entity, final int position, int grpPosition, View view, Activity activity) {

        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        imageLoader.displayImage(entity.getImageUrl(),viewHolder.image);

        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            dockActivity.replaceDockableFragment(ProfilePictureDetail.newInstance(entity),"ProfilePictureDetail");
            }
        });




      /*   popup = new LongPressPopupBuilder(dockActivity)// A Context object for the builder constructor
                .setTarget(viewHolder.image)
                 .setPopupView(R.layout.row_item_photo_dialoge, new PopupInflaterListener() {
                     @Override
                     public void onViewInflated(@Nullable String popupTag, View root) {

                         ImageView imageView=(ImageView)root.findViewById(R.id.imagedialoge);
                         Picasso.with(dockActivity).load(imagePath).fit().into(imageView);
                    //     imageLoader.displayImage(imagePath,imageView);

                     }
                 })
                 .setAnimationType(LongPressPopup.ANIMATION_TYPE_FROM_CENTER)
                .build();// This will give you a LongPressPopup object

        popup.register();*/


    }



    static class ViewHolder extends BaseViewHolder{
        @BindView(R.id.image)
        ImageView image;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
