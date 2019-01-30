package com.app.fandirect.ui.binders;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.app.fandirect.R;
import com.app.fandirect.activities.DockActivity;
import com.app.fandirect.entities.TabsEnt;
import com.app.fandirect.helpers.BasePreferenceHelper;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.interfaces.TabsInterface;
import com.app.fandirect.ui.viewbinders.abstracts.RecyclerViewBinder;
import com.app.fandirect.ui.viewbinders.abstracts.ViewBinder;
import com.app.fandirect.ui.views.AnyTextView;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TabBinder extends RecyclerViewBinder<TabsEnt> {

    private DockActivity dockActivity;
    private BasePreferenceHelper prefHelper;
    private ImageLoader imageLoader;
    private TabsInterface clickListner;

    public TabBinder(DockActivity dockActivity, BasePreferenceHelper prefHelper, TabsInterface clickListner) {
        super(R.layout.row_item_tabs);
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
    public void bindView(final TabsEnt entity, final int position, Object viewHolder, Context context) {

        ViewHolder holder = (ViewHolder) viewHolder;

        if(entity.isSelected()){
            holder.txtTab.setBackgroundColor(dockActivity.getResources().getColor(R.color.app_blue));
            holder.txtTab.setTextColor(dockActivity.getResources().getColor(R.color.white));
        }else{
            holder.txtTab.setBackgroundColor(dockActivity.getResources().getColor(R.color.transparent));
            holder.txtTab.setTextColor(dockActivity.getResources().getColor(R.color.gray_dark));
        }

        holder.txtTab.setText(entity.getCount()+" "+entity.getName());

        holder.txtTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListner.onTabItemClicked(entity,position);
            }
        });



    }

    static class ViewHolder extends BaseViewHolder{
        @BindView(R.id.txt_tab)
        AnyTextView txtTab;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
