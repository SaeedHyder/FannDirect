package com.app.fandirect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.fandirect.R;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.ui.views.TitleBar;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FullScreenImageFragment extends BaseFragment {
    @BindView(R.id.image)
    ImageView image;
    Unbinder unbinder;
    private static String ImageLink;
    private ImageLoader imageLoader;
    public static FullScreenImageFragment newInstance(String image) {
        Bundle args = new Bundle();
        ImageLink=image;
        FullScreenImageFragment fragment = new FullScreenImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageLoader=ImageLoader.getInstance();
        if (getArguments() != null) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fullscreen, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getMainActivity().hideBottomBar();
        imageLoader.displayImage(ImageLink, image);

    }

    @Override
    public void setTitleBar(TitleBar titleBar) {
        super.setTitleBar(titleBar);
        titleBar.hideButtons();
        titleBar.setSubHeading("Full Screen");
        titleBar.showBackButton();
    }
}
