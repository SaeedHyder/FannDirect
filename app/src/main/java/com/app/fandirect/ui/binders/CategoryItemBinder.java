package com.app.fandirect.ui.binders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.app.fandirect.R;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.ui.viewbinders.abstracts.RecyclerViewBinder;
import com.app.fandirect.ui.views.AnyTextView;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by saeedhyder on 2/28/2018.
 */

public class CategoryItemBinder extends RecyclerViewBinder<String> {

    private ImageLoader imageLoader;
    private RecyclerViewItemListener recyclerViewItemListener;
    private boolean isSignup;

    public CategoryItemBinder(RecyclerViewItemListener recyclerViewItemListener,boolean isSignup) {
        super(R.layout.row_item_service_type);
        imageLoader = ImageLoader.getInstance();
        this.recyclerViewItemListener = recyclerViewItemListener;
        this.isSignup=isSignup;
    }

    @Override
    public BaseViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void bindView(final String entity, final int position, Object viewHolder, Context context) {

        ViewHolder holder = (ViewHolder) viewHolder;

        if(isSignup){
            holder.ivCross.setVisibility(View.VISIBLE);
        }else{
            holder.ivCross.setVisibility(View.GONE);
        }

        if (entity != null && !entity.isEmpty())
            holder.serviceType.setText(entity + "");

        holder.llCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewItemListener.onRecyclerItemClicked(entity,position);
            }
        });

    }

    static class ViewHolder extends BaseViewHolder {
        @BindView(R.id.serviceType)
        AnyTextView serviceType;
        @BindView(R.id.iv_cross)
        ImageView ivCross;
        @BindView(R.id.ll_cross)
        LinearLayout llCross;


        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
