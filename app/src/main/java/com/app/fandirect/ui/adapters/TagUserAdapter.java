package com.app.fandirect.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.fandirect.R;
import com.app.fandirect.activities.DockActivity;
import com.app.fandirect.activities.MainActivity;
import com.app.fandirect.entities.GetMyFannsEnt;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

/**
 * Created by developer.ingic on 4/25/2018.
 */

public class TagUserAdapter extends BaseAdapter {
    LayoutInflater mLayoutInflater;
    ArrayList<GetMyFannsEnt> mItemList;
    DockActivity mainActivity;
    String userId;

    public TagUserAdapter(DockActivity context, ArrayList<GetMyFannsEnt> itemList, String userId) {
        mLayoutInflater = LayoutInflater.from(context);
        mItemList = itemList;
        mainActivity = context;
        this.userId = userId;
    }

    @Override
    public int getCount() {
        return mItemList.size();
    }

    @Override
    public GetMyFannsEnt getItem(int i) {
        return mItemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void clearAllList() {
        mItemList.clear();
    }

    public void addAllList(ArrayList<GetMyFannsEnt> data) {
        mItemList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_tag_friend, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (getItem(position).getSenderDetail().getId().equals(userId)) {
            if (getItem(position).getReceiverDetail() != null) {
                holder.tvName.setText(getItem(position).getReceiverDetail().getUserName());
                Picasso.with(mainActivity).load(getItem(position).getReceiverDetail().getImageUrl()).into(holder.ivImage);

            }
        } else if (getItem(position).getSenderDetail() != null) {
            holder.tvName.setText(getItem(position).getSenderDetail().getUserName());
            Picasso.with(mainActivity).load(getItem(position).getSenderDetail().getImageUrl()).into(holder.ivImage);
        }


        return convertView;
    }

    static class ViewHolder {
        TextView tvName;
        ImageView ivImage;

        ViewHolder(View view) {
            tvName = view.findViewById(R.id.tvUserName);
            ivImage = view.findViewById(R.id.ivUserImage);
        }
    }
}