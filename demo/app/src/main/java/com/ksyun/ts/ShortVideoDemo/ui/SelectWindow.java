package com.ksyun.ts.ShortVideoDemo.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ksyun.ts.ShortVideoDemo.R;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by xiaoqiang on 2017/11/21.
 */

public class SelectWindow {

    public final static int ITEM1_CLICK = 1;
    public final static int ITEM2_CLICK = 2;
    public final static int CANCEL_CLICK = 3;

    private PopupWindow mWindow;
    private Context mContext;
    private String mItem1;
    private String mItem2;
    private String mCancel;
    private View mWindowView;
    private TextView mItemView1;
    private TextView mItemView2;
    private TextView mCancelView;


    public SelectWindow(Context context) {
        this(context, null, null);
    }

    public SelectWindow(Context context, String item1, String item2) {
        this(context, item1, item2, null);
    }

    public SelectWindow(Context context, String item1, String item2, String cancel) {
        this.mContext = context;
        this.mItem1 = item1;
        this.mItem2 = item2;
        this.mCancel = cancel;
        initView();
        initWindow(mWindowView);
    }

    private void initView() {
        mWindowView = View.inflate(mContext, R.layout.layout_select_window, null);
        mItemView1 = mWindowView.findViewById(R.id.tv_select_item1);
        mItemView2 = mWindowView.findViewById(R.id.tv_select_item2);
        mCancelView = mWindowView.findViewById(R.id.tv_select_cancel);

        if (!TextUtils.isEmpty(mItem1)) {
            mItemView1.setText(mItem1);
        }
        if (!TextUtils.isEmpty(mItem2)) {
            mItemView2.setText(mItem2);
        }
        if (!TextUtils.isEmpty(mCancel)) {
            mCancelView.setText(mCancel);
        }
    }

    private void initWindow(View view) {
        mWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        mWindow.setAnimationStyle(R.style.SelectWindowStyle);
        mWindow.setTouchable(true);
        mWindow.setFocusable(true);
        mWindow.setOutsideTouchable(true);
        mWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = ((Activity) mContext).getWindow()
                        .getAttributes();
                lp.alpha = 1f;
                ((Activity) mContext).getWindow().setAttributes(lp);
            }
        });
    }

    public void setViewOnClick(Consumer onClick) {
        DefaultOnClick onclick = new DefaultOnClick(new Function<View, View>() {
            @Override
            public View apply(View view) throws Exception {
                if (mWindow.isShowing()) {
                    mWindow.dismiss();
                }
                return view;
            }
        }, onClick);
        mItemView1.setOnClickListener(onclick);
        mItemView1.setTag(ITEM1_CLICK);
        mItemView2.setOnClickListener(onclick);
        mItemView2.setTag(ITEM2_CLICK);
        mCancelView.setOnClickListener(onclick);
        mCancelView.setTag(CANCEL_CLICK);
    }

    public void showWindow(View anchor) {
        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow()
                .getAttributes();
        lp.alpha = 0.5f;
        ((Activity) mContext).getWindow().setAttributes(lp);
        mWindow.showAtLocation(anchor, Gravity.BOTTOM | Gravity.LEFT, 0,
                0);
    }

    public void dismissWindow() {
        if (mWindow != null) {
            mWindow.dismiss();
        }
    }

}
