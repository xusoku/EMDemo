package com.example.xusoku.bledemo.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;


/**
 * Author: Kejin ( Liang Ke Jin )
 * Date: 2015/11/23
 */
public class CustomTypefaceEditText extends EditText
{
    public CustomTypefaceEditText(Context context)
    {
        super(context);
        initializeTypeface();
    }

    public CustomTypefaceEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initializeTypeface();
    }

    public CustomTypefaceEditText(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initializeTypeface();
    }

    public void initializeTypeface()
    {
        setIncludeFontPadding(false);
//        if (CommonManager.mChineseTypeface == null) {
//            CommonManager.mChineseTypeface = Typeface.createFromAsset(getContext().getAssets(), CommonManager.FONT_CHINESE);
//        }
        setIncludeFontPadding(false);
//        setLineSpacing(DimenUtils.dp2px(getContext(), 4), (float) 1);//1倍间距
//        setTypeface(CommonManager.mChineseTypeface);
    }
}
