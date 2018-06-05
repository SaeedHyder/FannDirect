package com.app.fandirect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.app.fandirect.R;
import com.app.fandirect.entities.commentEnt;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.helpers.UIHelper;
import com.app.fandirect.ui.adapters.ArrayListAdapter;
import com.app.fandirect.ui.binders.CommentBinder;
import com.app.fandirect.ui.views.AnyEditTextView;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.TitleBar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.fandirect.global.WebServiceConstants.getComments;
import static com.app.fandirect.global.WebServiceConstants.postComments;

/**
 * Created by saeedhyder on 5/17/2018.
 */
public class CommentFragment extends BaseFragment {
    @BindView(R.id.txt_no_data)
    AnyTextView txtNoData;
    @BindView(R.id.lv_comment)
    ListView lvComment;
    @BindView(R.id.txtMessageBox)
    AnyEditTextView txtMessageBox;
    @BindView(R.id.ll_send_btn)
    LinearLayout llSendBtn;
    Unbinder unbinder;

    private ArrayListAdapter<commentEnt> adapter;
    private ArrayList<commentEnt> collection;
    private static String postIdKey = "postIdKey";
    private String postId;

    public static CommentFragment newInstance() {
        Bundle args = new Bundle();
        CommentFragment fragment = new CommentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static CommentFragment newInstance(String postId) {
        Bundle args = new Bundle();
        args.putString(postIdKey, postId);
        CommentFragment fragment = new CommentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new ArrayListAdapter<commentEnt>(getDockActivity(), new CommentBinder(getDockActivity(), prefHelper));
        if (getArguments() != null) {
            postId = getArguments().getString(postIdKey);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment_section, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getMainActivity().hideBottomBar();
        if (postId != null)
            serviceHelper.enqueueCall(headerWebService.getComments(postId), getComments);
    }

    @Override
    public void setTitleBar(TitleBar titleBar) {
        super.setTitleBar(titleBar);
        titleBar.hideButtons();
        titleBar.showBackButton();
        titleBar.showTitleLogo();
    }


    @OnClick(R.id.ll_send_btn)
    public void onViewClicked() {

        if (postId != null){
            if(isValidate())
            serviceHelper.enqueueCall(headerWebService.postComment(txtMessageBox.getText().toString(), postId), postComments);
        }

    }


    private boolean isValidate() {
        if (txtMessageBox == null || txtMessageBox.getText().toString().equals("") || txtMessageBox.getText().toString().trim().equals("")) {
            UIHelper.showShortToastInCenter(getDockActivity(), "Write message to proceed");
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        super.ResponseSuccess(result, Tag, message);
        switch (Tag) {

            case getComments:
                ArrayList<commentEnt> entity = (ArrayList<commentEnt>) result;
                bindListView(entity);
                break;

            case postComments:
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                UIHelper.hideSoftKeyboard(getDockActivity(), txtMessageBox);
                txtMessageBox.getText().clear();

                if (collection.size() > 0) {
                    commentEnt ent = (commentEnt) result;
                    adapter.add(ent);
                    adapter.notifyDataSetChanged();

                    lvComment.post(new Runnable() {
                        public void run() {
                            lvComment.setSelection(lvComment.getCount() - 1);
                        }
                    });
                }else{
                    if (postId != null)
                        serviceHelper.enqueueCall(headerWebService.getComments(postId), getComments);
                }

                break;


        }
    }

    private void bindListView(ArrayList<commentEnt> entity) {

        collection = new ArrayList<>();
        if (entity.size() > 0) {
            lvComment.setVisibility(View.VISIBLE);
            txtNoData.setVisibility(View.GONE);
        } else {
            lvComment.setVisibility(View.GONE);
            txtNoData.setVisibility(View.VISIBLE);
        }
        collection.addAll(entity);
        adapter.clearList();
        lvComment.setAdapter(adapter);
        adapter.addAll(collection);
        lvComment.post(new Runnable() {
            public void run() {
                lvComment.setSelection(lvComment.getCount() - 1);
            }
        });
    }
}
