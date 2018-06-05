package com.app.fandirect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.app.fandirect.R;
import com.app.fandirect.entities.GetServicesEnt;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.interfaces.PostInterface;
import com.app.fandirect.ui.adapters.ArrayListAdapter;
import com.app.fandirect.ui.binders.selectCategoryBinder;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.ExpandedListView;
import com.app.fandirect.ui.views.TitleBar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.app.fandirect.global.WebServiceConstants.getMyServices;

/**
 * Created by saeedhyder on 5/11/2018.
 */
public class SelectedCategoryFragment extends BaseFragment {
    @BindView(R.id.lv_Categories)
    ExpandedListView lvCategories;
    Unbinder unbinder;
    PostInterface postInterface;
    @BindView(R.id.txt_no_data)
    AnyTextView txtNoData;

    private ArrayListAdapter<GetServicesEnt> adapter;
    private ArrayList<GetServicesEnt> collection;

    public static SelectedCategoryFragment newInstance() {

        return new SelectedCategoryFragment();
    }

    void setInterfaceListner(PostInterface postInterface) {
        this.postInterface = postInterface;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ArrayListAdapter<GetServicesEnt>(getDockActivity(), new selectCategoryBinder(getDockActivity(), prefHelper));

        if (getArguments() != null) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selected_category, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        serviceHelper.enqueueCall(headerWebService.getMyServices(prefHelper.getUser().getId()+""), getMyServices);

        setListner();

    }

    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        super.ResponseSuccess(result, Tag, message);
        switch (Tag) {

            case getMyServices:
                ArrayList<GetServicesEnt> entity = (ArrayList<GetServicesEnt>) result;
                setCategoryData(entity);

                break;
        }
    }

    private void setListner() {

        lvCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                postInterface.getCategoryData(collection.get(position));
                getMainActivity().popFragment();
                //getDockActivity().addDockableFragment(AddPostFragment.newInstance(collection.get(position)),"AddPostFragment");
            }
        });
    }

    private void setCategoryData(ArrayList<GetServicesEnt> entity) {

        collection = new ArrayList<>();
        collection.addAll(entity);

        if (collection.size() > 0) {
            lvCategories.setVisibility(View.VISIBLE);
            txtNoData.setVisibility(View.GONE);
        } else {
            lvCategories.setVisibility(View.GONE);
            txtNoData.setVisibility(View.VISIBLE);
        }


        adapter.clearList();
        lvCategories.setAdapter(adapter);
        adapter.addAll(collection);
    }

    @Override
    public void setTitleBar(TitleBar titleBar) {
        super.setTitleBar(titleBar);
        titleBar.hideButtons();
        titleBar.showTitleLogo();
        titleBar.showBackButton();
    }



}
