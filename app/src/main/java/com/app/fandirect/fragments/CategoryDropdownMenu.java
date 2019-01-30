package com.app.fandirect.fragments;

import android.content.Context;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.app.fandirect.R;
import com.app.fandirect.entities.Category;
import com.app.fandirect.entities.GetMyFannsEnt;
import com.app.fandirect.ui.adapters.CategoryDropdownAdapter;

import java.util.ArrayList;


public class CategoryDropdownMenu extends PopupWindow {
    private Context context;
    private RecyclerView rvCategory;
    private CategoryDropdownAdapter dropdownAdapter;
    private ArrayList<GetMyFannsEnt> collection=new ArrayList<>();
    private String UserId="";

    public CategoryDropdownMenu(Context context,ArrayList<GetMyFannsEnt> data,String UserId) {
        super(context);
        this.context = context;
        collection=data;
        this.UserId=UserId;
        setupView();
    }

    public void setCategorySelectedListener(CategoryDropdownAdapter.CategorySelectedListener categorySelectedListener) {
        dropdownAdapter.setCategorySelectedListener(categorySelectedListener);
    }

    private void setupView() {
        View view = LayoutInflater.from(context).inflate(R.layout.popup_menu_item, null);

        rvCategory = view.findViewById(R.id.rvCategory);
        rvCategory.setNestedScrollingEnabled(false);
        rvCategory.setHasFixedSize(true);
        rvCategory.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        rvCategory.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));

        dropdownAdapter = new CategoryDropdownAdapter(collection,UserId);
        rvCategory.setAdapter(dropdownAdapter);

        setContentView(view);
    }


}

