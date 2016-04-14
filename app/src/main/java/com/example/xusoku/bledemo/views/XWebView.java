package com.example.xusoku.bledemo.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Author: Kejin ( Liang Ke Jin )
 * Date: 2015/12/7
 */
public class XWebView extends WebView
{
    public final static String TAG = "XWebView";

    public XWebView(Context context)
    {
        super(context);
        initializeWebView();
    }

    public XWebView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initializeWebView();
    }

    public XWebView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initializeWebView();
    }

    private void initializeWebView()
    {
        this.setWebViewClient(new SimpleWebViewClient());
//        this.setWebChromeClient(new SimpleChromeViewClient());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        }
        else {
            this.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }

        this.getSettings().setJavaScriptEnabled(false);
        this.getSettings().setDomStorageEnabled(true);
        this.setHorizontalScrollBarEnabled(false);
        this.setVerticalScrollBarEnabled(false);
        this.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });
    }

    @Override
    public void onDetachedFromWindow()
    {
        this.clearCache(true);
        super.onDetachedFromWindow();
    }

    /**
     * 简单的webview client
     */
    protected class SimpleWebViewClient extends WebViewClient
    {

        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            //Loge(TAG, "Url: " + url);
            view.loadUrl(url);
            return true;
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {
            //Loge(TAG, "onPageStarted: " + url);
        }

        public void onPageFinished(WebView view, String url)
        {
            //Loge(TAG, "onPageFinished: " + url);
        }

//        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error)
//        {
//            super.onReceivedError(view, request, error);
//
//            //Loge(TAG, "onReceivedError: " + error.toString());
//        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }

    protected class SimpleChromeViewClient extends WebChromeClient
    {

        public void onProgressChanged(WebView view, int newProgress)
        {
            //Loge(TAG, "onProgressChanged: " + String.valueOf(newProgress));
        }

        public boolean onJsAlert(WebView view, String url, String message, JsResult result)
        {
            //Loge(TAG, "onJsAlert: " + url + "  Msg: " + message + "  Result: " + result.toString());
            return false;
        }

        public boolean onJsConfirm(WebView view, String url, String message, JsResult result)
        {
            //Loge(TAG, "onJsConfirm: " + url + "  Msg: " + message + "  Result: " + result.toString());
            return false;
        }

        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result)
        {
            //Loge(TAG, "onJsPrompt: " + url + "  Msg: " + message + "  Result: " + result.toString());
            return false;
        }
    }

}
