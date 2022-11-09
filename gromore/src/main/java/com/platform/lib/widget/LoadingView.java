package com.platform.lib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.platform.lib.R;

/**
 * created by hty
 * 2022/11/8
 * Desc:加载中
 */
public class LoadingView extends LinearLayout {

    private ProgressBar mProgressBar;
    private TextView mTvContent;

    public LoadingView(Context context) {
        this(context,null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.lib_view_loading,this);
        mProgressBar = (ProgressBar) findViewById(R.id.lib_view_progress);
        mTvContent = (TextView) findViewById(R.id.lib_view_content);
    }

    public void showRequst(String message) {
        if(null!=mProgressBar){
            mProgressBar.setVisibility(VISIBLE);
            mProgressBar.setIndeterminate(true);
        }
        if(null!=mTvContent) mTvContent.setText(message);
    }

    public void showResult(String message) {
        if (null != mProgressBar) {
            mProgressBar.setIndeterminate(false);
            mProgressBar.setVisibility(GONE);
        }
        if (null != mTvContent) mTvContent.setText(message);
    }
}