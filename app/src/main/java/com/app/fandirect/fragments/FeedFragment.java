package com.app.fandirect.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.app.fandirect.R;
import com.app.fandirect.entities.Post;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.global.AppConstants;
import com.app.fandirect.helpers.ShareIntentHelper;
import com.app.fandirect.helpers.UIHelper;
import com.app.fandirect.interfaces.ImageSetter;
import com.app.fandirect.interfaces.PostClicksInterface;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.ui.adapters.ArrayListAdapter;
import com.app.fandirect.ui.binders.FeedsBinder;
import com.app.fandirect.ui.views.AnyEditTextView;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.ExpandedListView;
import com.app.fandirect.ui.views.TitleBar;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.app.fandirect.global.WebServiceConstants.favoritePost;
import static com.app.fandirect.global.WebServiceConstants.getAllPosts;
import static com.app.fandirect.global.WebServiceConstants.postLike;

/**
 * Created by saeedhyder on 3/13/2018.
 */
public class FeedFragment extends BaseFragment implements RecyclerViewItemListener, ImageSetter, PostClicksInterface {

    @BindView(R.id.txt_search)
    AnyEditTextView txtSearch;
    @BindView(R.id.iv_search_icon)
    ImageView ivSearchIcon;
    @BindView(R.id.ll_search)
    LinearLayout llSearch;
    @BindView(R.id.ll_search_box)
    LinearLayout llSearchBox;
    @BindView(R.id.image)
    CircleImageView image;
    @BindView(R.id.txt_update_post)
    AnyTextView txtUpdatePost;
    @BindView(R.id.btn_uploadPhoto)
    AnyTextView btnUploadPhoto;
    @BindView(R.id.btn_post)
    Button btnPost;
    @BindView(R.id.txt_no_data)
    AnyTextView txtNoData;
    @BindView(R.id.lv_feeds)
    ExpandedListView lvFeeds;
    Unbinder unbinder;
    private ImageLoader imageLoader;

    private ArrayListAdapter<Post> Adapter;
    private ArrayList<Post> collection;

    public static FeedFragment newInstance() {
        Bundle args = new Bundle();

        FeedFragment fragment = new FeedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageLoader = ImageLoader.getInstance();
        Adapter = new ArrayListAdapter<Post>(getDockActivity(), new FeedsBinder(getDockActivity(), prefHelper, this, this));

        if (getArguments() != null) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feeds, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMainActivity().setImageSetter(this);
        getMainActivity().showBottomBar(AppConstants.search);

        if (prefHelper.getUser().getImageUrl() != null) {
            imageLoader.displayImage(prefHelper.getUser().getImageUrl() + "", image);
        }


        lvFeeds.setAdapter(Adapter);
        serviceHelper.enqueueCall(headerWebService.getAllPosts(), getAllPosts);

        setTextWatecher();

    }

    private void setTextWatecher() {
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                bindData(getSearchedArray(s.toString()));
            }
        });
    }

    public ArrayList<Post> getSearchedArray(String keyword) {
        if (collection.isEmpty()) {
            return new ArrayList<>();
        }

        ArrayList<Post> arrayList = new ArrayList<>();

        String UserName = "";
        for (Post item : collection) {
            UserName = item.getUserDetail().getUserName();
          /*  if (UserName.contains(keyword)) {
                arrayList.add(item);
            }*/
            if (Pattern.compile(Pattern.quote(keyword), Pattern.CASE_INSENSITIVE).matcher(UserName).find()) {
                arrayList.add(item);
            }
        }
        return arrayList;

    }


    @Override
    public void setTitleBar(TitleBar titleBar) {
        super.setTitleBar(titleBar);
        titleBar.hideButtons();
        titleBar.showTitleLogo();
        titleBar.showBackButton();
        titleBar.showNotificationBell(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDockActivity().replaceDockableFragment(NotificationFragment.newInstance(), "NotificationFragment");
            }
        });
       /* titleBar.showMessageBtn(new View.OnClickListener() {
            @Override
            public void onUserProfileClick(View v) {
                getDockActivity().replaceDockableFragment(MessageFragment.newInstance(), "MessageFragment");
            }
        });*/
    }


    @Override
    public void onRecyclerItemClicked(Object Ent, int position) {
        getMainActivity().notImplemented();
    }

    @Override
    public void onCategoryClick(Object Ent, int position) {

    }

    @OnClick({R.id.ll_post})
    public void onViewClicked(View view) {

        switch (view.getId()) {

            case R.id.ll_post:
                getDockActivity().popFragment();
                getDockActivity().replaceDockableFragment(AddPostFragment.newInstance(false), "NewPostFragment");
                break;
        }
    }


    @Override
    public void setImage(String imagePath) {
       /* if (imagePath != null) {
            getDockActivity().replaceDockableFragment(AddPostFragment.newInstance(imagePath, false, txtPost.getText().toString()), "NewPostFragment");
        }*/
    }

    @Override
    public void setFilePath(String filePath) {

    }

    @Override
    public void setVideo(String videoPath, String VideoThumbail) {

    }

    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        super.ResponseSuccess(result, Tag, message);
        switch (Tag) {


            case getAllPosts:
                ArrayList<Post> entity = (ArrayList<Post>) result;
                setFeedsListData(entity);
                break;

            case postLike:
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;

            case favoritePost:
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;


        }
    }

    private void setFeedsListData(ArrayList<Post> entity) {

        collection = new ArrayList<>();
        collection.addAll(entity);
        bindData(collection);

    }

    private void bindData(ArrayList<Post> collection) {

        if (collection.size() > 0) {
            lvFeeds.setVisibility(View.VISIBLE);
            txtNoData.setVisibility(View.GONE);
        } else {
            lvFeeds.setVisibility(View.GONE);
            txtNoData.setVisibility(View.VISIBLE);
        }

        Adapter.clearList();
        lvFeeds.setAdapter(Adapter);
        Adapter.addAll(collection);
    }


    @Override
    public void like(Post entity, int position) {

        serviceHelper.enqueueCall(headerWebService.likePost(entity.getId() + ""), postLike);

    }

    @Override
    public void favorite(Post entity, int position) {
        serviceHelper.enqueueCall(headerWebService.favoritePost(entity.getId() + ""), favoritePost);
    }

    @Override
    public void onResume() {
        super.onResume();
        getDockActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void share(Post entity, int position) {

   //     getDockActivity().onLoadingStarted();

        if (entity.getImageUrl() != null && !entity.getImageUrl().equals("") && entity.getDescription() != null && !entity.getDescription().equals("")) {
            ShareIntentHelper.shareImageAndTextResultIntent(getDockActivity(), entity.getImageUrl(), entity.getDescription());
        } else if (entity.getImageUrl() != null && entity.getDescription() == null) {
            ShareIntentHelper.shareImageAndTextResultIntent(getDockActivity(), entity.getImageUrl(), "");
        } else if (entity.getImageUrl() == null && entity.getDescription() != null) {
            ShareIntentHelper.shareTextIntent(getDockActivity(), entity.getDescription());
        } else {
            UIHelper.showShortToastInCenter(getDockActivity(), "Description is not avaliable");
        }

    }

    @Override
    public void comment(Post entity, int position) {
        getDockActivity().replaceDockableFragment(CommentFragment.newInstance(entity.getId() + ""), "CommentFragment");
    }


}
