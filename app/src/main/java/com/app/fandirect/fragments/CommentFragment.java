package com.app.fandirect.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.app.fandirect.R;
import com.app.fandirect.entities.GetMyFannsEnt;
import com.app.fandirect.entities.TagUser;
import com.app.fandirect.entities.commentEnt;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.helpers.DialogHelper;
import com.app.fandirect.helpers.UIHelper;
import com.app.fandirect.interfaces.DeleteComment;
import com.app.fandirect.ui.adapters.ArrayListAdapter;
import com.app.fandirect.ui.adapters.TagUserAdapter;
import com.app.fandirect.ui.binders.CommentBinder;
import com.app.fandirect.ui.views.AnyEditTextView;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.TitleBar;
import com.xw.repo.XEditText;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.fandirect.global.WebServiceConstants.deleteComment;
import static com.app.fandirect.global.WebServiceConstants.getComments;
import static com.app.fandirect.global.WebServiceConstants.getMyFanns;
import static com.app.fandirect.global.WebServiceConstants.postComments;

/**
 * Created by saeedhyder on 5/17/2018.
 */
public class CommentFragment extends BaseFragment implements DeleteComment {
    @BindView(R.id.txt_no_data)
    AnyTextView txtNoData;
    @BindView(R.id.lv_comment)
    ListView lvComment;
    @BindView(R.id.txtMessageBox)
    XEditText txtMessageBox;
    @BindView(R.id.ll_send_btn)
    LinearLayout llSendBtn;
    Unbinder unbinder;
    private int pos = -1;
    private commentEnt deleteEnt;

    int previousLength = 0;
    boolean isTwise = false;

    private CategoryDropdownMenu menu;
    private ListPopupWindow popupWindow;
    private TagUserAdapter adapterTag;
    private HashMap<String, GetMyFannsEnt> hashMap = new HashMap<>();
    private static ArrayList<TagUser> tagUserList = new ArrayList<>();
    private static ArrayList<String> tagIds = new ArrayList<>();
    private ArrayList<GetMyFannsEnt> userList = new ArrayList<>();
    private ArrayList<GetMyFannsEnt> filterList = new ArrayList<>();
    private Integer lengthOfEditText = 0;

    private ArrayListAdapter<commentEnt> adapter;
    private ArrayList<commentEnt> collection;
    private static String postIdKey = "postIdKey";
    private String postId;
    private String idComment;

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

        getDockActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getMainActivity().hideAdds();

        adapter = new ArrayListAdapter<commentEnt>(getDockActivity(), new CommentBinder(getDockActivity(), prefHelper, this));
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
        tagUserList = new ArrayList<>();
        tagIds = new ArrayList<>();
        initListPopupAdapter();

        double scaletype = getResources().getDisplayMetrics().density;
        if (scaletype >= 3.0) {
            isTwise = true;
        }

        serviceHelper.enqueueCall(headerWebService.getMyFanns(), getMyFanns);

        if (postId != null)
            serviceHelper.enqueueCall(headerWebService.getComments(postId), getComments);


        setEditTextListener();


    }


    public void initListPopupAdapter() {
        userList = new ArrayList<>();
        popupWindow = new ListPopupWindow(getDockActivity());
        adapterTag = new TagUserAdapter(getDockActivity(), userList, prefHelper.getUser().getId());
        popupWindow.setAnchorView(txtMessageBox);
        popupWindow.setAdapter(adapter);
    }


    public void setEditTextListener() {
        txtMessageBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (popupWindow != null)
                    popupWindow.dismiss();
            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, int before, int count) {

                Thread t = new Thread() {
                    public void run() {


                        if (s.toString().length() == 0) {
                            tagUserList = new ArrayList<>();
                            tagIds = new ArrayList<>();
                        }

                        try {
                            if (s.toString().substring(start).startsWith("@") || s.toString().substring(start).startsWith("#")) {
                                pos = start;
                            } else if (s.toString().substring(start).startsWith(" ") || s.toString().substring(start).startsWith("\n")) {
                                pos = -1;
                            }


                            if (pos != -1 && s.toString().length() >= pos && s.toString().substring(pos).startsWith("@")) {
                                if (s.toString().substring(pos).length() >= 3) {
                                    String tagUserName = s.toString().substring(pos).replace("@", "");
                                    showListPopupWindow(txtMessageBox, getFilteredArray(tagUserName), s.toString().substring(pos));
                                    //  showListPopupWindow(txtMessageBox, getFilteredArray(tagUserName), tagUserName);
                                }
                            } else if (pos != -1 && s.toString().length() >= pos && s.toString().substring(pos).startsWith("#")) {
                                if (s.toString().substring(pos).length() >= 3) {
                                    String tagUserName = s.toString().substring(pos).replace("#", "");
                                    showListPopupWindow(txtMessageBox, getFilteredArray(tagUserName), s.toString().substring(pos));
                                    //    showListPopupWindow(txtMessageBox, getFilteredArray(tagUserName), tagUserName);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                       /* if(s.toString().length()<=previousLength){
                            String textComment = txtMessageBox.getText().toString();

                            final SpannableString spannableString = new SpannableString(textComment);
                            for (int j = 0; j < userList.size(); j++) {
                                int startIndexOfLink = textComment.indexOf("@" + getUserName(userList.get(j)));
                                if (startIndexOfLink != -1) {
                                    spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), startIndexOfLink, startIndexOfLink + ("@" + getUserName(userList.get(j))).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                }
                            }

                            getDockActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    txtMessageBox.setHighlightColor(Color.TRANSPARENT); // prevent TextView change background when highlight
                                    txtMessageBox.setMovementMethod(LinkMovementMethod.getInstance());
                                    txtMessageBox.setText(spannableString, TextView.BufferType.SPANNABLE);
                                    popupWindow.dismiss();

                                }
                            });
                        }
                        previousLength=s.toString().length();*/


                    }
                };
                t.start();


            }

            @Override
            public void afterTextChanged(Editable s) {
                if (pos == -1)
                    popupWindow.dismiss();
            }
        });

    }

    public ArrayList<GetMyFannsEnt> getFilteredArray(String keyword) {
        if (userList.isEmpty()) {
            return new ArrayList<>();
        }

        ArrayList<GetMyFannsEnt> arrayList = new ArrayList<>();

        String UserName = "";
        for (GetMyFannsEnt item : userList) {

            UserName = getUserName(item);

            if (keyword.toString().length() > 3) {
                if (UserName.toLowerCase().toString().contains(keyword.substring(0, 2).toLowerCase().toString())) {
                    arrayList.add(item);
                }
            } else {
                if (UserName.toLowerCase().toString().contains(keyword.toLowerCase().toString())) {
                    arrayList.add(item);
                }
            }

           /* if (Pattern.compile(Pattern.quote(keyword.toLowerCase().toString()), Pattern.CASE_INSENSITIVE).matcher(UserName.toLowerCase().toString()).find()) {
                arrayList.add(item);
            }*/
        }
        return arrayList;

    }

    private void showListPopupWindow(final View anchor, final ArrayList<GetMyFannsEnt> list, final String name) {

        hashMap = new HashMap<>();
        tagIds = new ArrayList<>();
        for (GetMyFannsEnt item : list) {
            hashMap.put(getUserId(item), item);
        }

        for (int j = 0; j < list.size(); j++) {
            if (getUserName(list.get(j)) != null && !getUserName(list.get(j)).equals("") && (txtMessageBox.getText().toString().contains("@" + getUserName(list.get(j))))) {
                tagIds.add(getUserId(list.get(j)));
            }
        }
        for (String item : tagIds) {
            hashMap.remove(item);
        }

        filterList = new ArrayList<>();
        filterList.addAll(hashMap.values());


        getDockActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapterTag.clearAllList();
                adapterTag.addAllList(filterList);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (filterList.size() > 0) {
                        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_FROM_FOCUSABLE);
                        UIHelper.hideSoftKeyboard(getDockActivity(), txtMessageBox);
                    }
                }
                popupWindow.setAnchorView(anchor);
                popupWindow.setAdapter(adapterTag);
                popupWindow.show();


            }
        });


        popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                idComment = txtMessageBox.getText().toString();

                String textComment = txtMessageBox.getText().toString();
                String[] tagedName = name.split("\\s+");
                textComment = textComment.replace(tagedName[0], "@" + getUserName(filterList.get(i)));


                final SpannableStringBuilder spannableString = new SpannableStringBuilder(textComment);
                /*for (int j = 0; j < userList.size(); j++) {
                    int startIndexOfLink = textComment.indexOf("@" + getUserName(userList.get(j)));
                    if (startIndexOfLink != -1) {
                        spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), startIndexOfLink, startIndexOfLink + ("@" + getUserName(userList.get(j))).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }*/
                for (int j = 0; j < userList.size(); j++) {
                    if (getUserName(userList.get(j)) != null && !getUserName(userList.get(j)).equals("") && !getUserName(userList.get(j)).isEmpty()) {
                        int startIndexOfLink = textComment.indexOf("@" + getUserName(userList.get(j)));
                        if (startIndexOfLink != -1) {
                            BitmapDrawable bitmapDrawable = setTag("@" + getUserName(userList.get(j)));
                            spannableString.setSpan(new ImageSpan(bitmapDrawable), startIndexOfLink, startIndexOfLink + ("@" + getUserName(userList.get(j))).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannableString.append(" ");
                        }
                    }

                }

                getDockActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            UIHelper.showSoftKeyboard(getDockActivity(), txtMessageBox);
                        }

                        txtMessageBox.setHighlightColor(Color.TRANSPARENT); // prevent TextView change background when highlight
                        txtMessageBox.setMovementMethod(LinkMovementMethod.getInstance());
                        txtMessageBox.setText(spannableString, TextView.BufferType.SPANNABLE);
                        txtMessageBox.setSelection(txtMessageBox.getText().length());
                        popupWindow.dismiss();

                    }
                });
                pos = -1;

            }
        });


    }

    public BitmapDrawable setTag(String tagName) {
        String chip = tagName;

        LayoutInflater lf = (LayoutInflater) getDockActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        TextView textView = (TextView) lf.inflate(R.layout.tag_edittext, null);
        textView.setText(chip); // set text
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        textView.measure(spec, spec);
        textView.layout(0, 0, textView.getMeasuredWidth(), textView.getMeasuredHeight());
        Bitmap b = Bitmap.createBitmap(textView.getWidth(), textView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        canvas.translate(-textView.getScrollX(), -textView.getScrollY());
        textView.draw(canvas);
        textView.setDrawingCacheEnabled(true);
        Bitmap cacheBmp = textView.getDrawingCache();
        Bitmap viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_8888, true);
        textView.destroyDrawingCache(); // destory drawable
        BitmapDrawable bmpDrawable = new BitmapDrawable(viewBmp);
        int width = bmpDrawable.getIntrinsicWidth();
        int height = bmpDrawable.getIntrinsicHeight();
        if (isTwise) {
            width = width * 2;
            height = height * 2;
        }
        bmpDrawable.setBounds(0, 0, width, height);

        return bmpDrawable;


    }


    @OnClick({R.id.txtMessageBox, R.id.ll_send_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txtMessageBox:

                break;
            case R.id.ll_send_btn:
                if (postId != null) {
                    if (isValidate()) {
                        if (idComment != null && !idComment.equals("")) {
                            idComment = txtMessageBox.getText().toString();
                            tagIds = new ArrayList<>();

                            for (int j = 0; j < userList.size(); j++) {
                                if (getUserName(userList.get(j)) != null && !getUserName(userList.get(j)).equals("") && (idComment.toString().contains("@" + getUserName(userList.get(j))))) {
                                    tagIds.add(getUserId(userList.get(j)));
                                    idComment = idComment.replace("@" + getUserName(userList.get(j)), "@" + getUserId(userList.get(j)) + "@");
                                }
                            }
                            String Ids = TextUtils.join(",", tagIds);
                            serviceHelper.enqueueCall(headerWebService.postComment(Html.fromHtml(idComment + "").toString(), postId, Ids), postComments);

                        } else {
                            serviceHelper.enqueueCall(headerWebService.postComment(txtMessageBox.getText().toString(), postId, ""), postComments);
                        }
                    }
                }
                break;
        }
    }


    public String getUserName(GetMyFannsEnt entity) {
        if (entity.getSenderDetail().getId().equals(prefHelper.getUser().getId())) {
            if (entity.getReceiverDetail() != null) {
                return entity.getReceiverDetail().getUserName();

            }
        } else if (entity.getSenderDetail() != null) {
            return entity.getSenderDetail().getUserName();
        }

        return "";
    }

    public String getUserId(GetMyFannsEnt entity) {
        if (entity.getSenderDetail().getId().equals(prefHelper.getUser().getId())) {
            if (entity.getReceiverDetail() != null) {
                return entity.getReceiverDetail().getId();
            }
        } else if (entity.getSenderDetail() != null) {
            return entity.getSenderDetail().getId();
        }

        return "";
    }


    @Override
    public void setTitleBar(TitleBar titleBar) {
        super.setTitleBar(titleBar);
        titleBar.hideButtons();
        titleBar.showBackButton();
        titleBar.setSubHeading(getResString(R.string.comments));
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
                if (getTitleBar() != null) {
                    getTitleBar().hideNotificationBell();
                }
                ArrayList<commentEnt> entity = (ArrayList<commentEnt>) result;
                bindListView(entity);
                break;

            case deleteComment:
                UIHelper.showShortToastInCenter(getDockActivity(), message);

                if (deleteEnt != null) {
                    adapter.remove(deleteEnt);
                    adapter.notifyDataSetChanged();

                    if (collection.size() > 0) {
                        lvComment.setVisibility(View.VISIBLE);
                        txtNoData.setVisibility(View.GONE);
                    } else {
                        lvComment.setVisibility(View.GONE);
                        txtNoData.setVisibility(View.VISIBLE);
                    }
                }

                break;

            case getMyFanns:
                userList = (ArrayList<GetMyFannsEnt>) result;
                // menu = new CategoryDropdownMenu(getDockActivity(), userList, prefHelper.getUser().getId());

                break;

            case postComments:
                UIHelper.showShortToastInCenter(getDockActivity(), "Comment Posted successfully");
                UIHelper.hideSoftKeyboard(getDockActivity(), txtMessageBox);
                txtMessageBox.getText().clear();
                tagIds.clear();
                hashMap.clear();
                filterList.clear();

                if (collection.size() > 0) {
                    commentEnt ent = (commentEnt) result;
                    collection.add(ent);
                    adapter.add(ent);
                    adapter.notifyDataSetChanged();

                    lvComment.post(new Runnable() {
                        public void run() {
                            lvComment.setSelection(lvComment.getCount() - 1);
                        }
                    });
                } else {
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

        setTouchListner();
    }

    private void setTouchListner() {
        txtMessageBox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            lvComment.setSelection(lvComment.getCount() - 1);
                        }
                    }, 1000);
                }
                return false;
            }
        });
       /* txtMessageBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){

}
            }
        });*/

    }

    @Override
    public void onPause() {
        super.onPause();
        if (popupWindow != null && popupWindow.isShowing())
            popupWindow.dismiss();
    }


    @Override
    public void onStop() {
        super.onStop();
        if (popupWindow != null && popupWindow.isShowing())
            popupWindow.dismiss();
    }


    @Override
    public void DeleteComment(final Object entity, final int position) {

        final commentEnt ent = (commentEnt) entity;
        if(ent!=null && ent.getId()!=null) {
            deleteEnt = ent;
            serviceHelper.enqueueCall(headerWebService.deleteComment(ent.getId()), deleteComment);
            final DialogHelper dialogHelper = new DialogHelper(getDockActivity());
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getDockActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getMainActivity().showAdds();
    }


}
