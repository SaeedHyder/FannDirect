package com.app.fandirect.ui.binders;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.app.fandirect.R;
import com.app.fandirect.entities.LikeDetailEnt;
import com.app.fandirect.helpers.DateHelper;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.ui.viewbinders.abstracts.RecyclerViewBinder;
import com.app.fandirect.ui.views.AnyTextView;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class LikesBinder extends RecyclerViewBinder<LikeDetailEnt> {

    private ImageLoader imageLoader;
    private RecyclerViewItemListener recyclerViewItemListener;

    public LikesBinder(RecyclerViewItemListener recyclerViewItemListener) {
        super(R.layout.row_item_like);
        imageLoader = ImageLoader.getInstance();
        this.recyclerViewItemListener = recyclerViewItemListener;
    }

    @Override
    public BaseViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void bindView(final LikeDetailEnt entity, final int position, Object viewHolder, Context context) {
        ViewHolder holder = (ViewHolder) viewHolder;


        if (entity != null) {
            if (entity.getUserDetail() != null && entity.getUserDetail().getUserName() != null) {
                holder.txtName.setText(entity.getUserDetail().getUserName() + "");
            }
            holder.txtTimeDate.setText(DateHelper.getLocalDateTime(entity.getCreatedAt()));

            if (entity.getUserDetail() != null && entity.getUserDetail().getImageUrl() != null) {
                imageLoader.displayImage(entity.getUserDetail().getImageUrl(), holder.ivImage);
            }

            holder.mainFrameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerViewItemListener.onRecyclerItemClicked(entity, position);
                }
            });
        }
    }

    static class ViewHolder extends BaseViewHolder {
        @BindView(R.id.iv_image)
        CircleImageView ivImage;
        @BindView(R.id.txt_name)
        AnyTextView txtName;
        @BindView(R.id.txt_time_date)
        AnyTextView txtTimeDate;
        @BindView(R.id.mainFrameLayout)
        LinearLayout mainFrameLayout;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
