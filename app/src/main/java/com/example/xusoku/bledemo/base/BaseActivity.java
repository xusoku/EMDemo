package com.example.xusoku.bledemo.base;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xusoku.bledemo.R;
import com.example.xusoku.bledemo.util.AppManager;
import com.example.xusoku.bledemo.util.LogUtils;


public abstract class BaseActivity extends AppCompatActivity
{

    public Context mContext;
    public Activity mActivity;
    public LayoutInflater mInflater;

    private RelativeLayout layTopBar;
    private TextView tvTitle;
    private ImageButton btnLeft;
    private ImageButton btnRight;
    private FrameLayout layBody;
    private ViewStub stubLoadingFailed;
    private FrameLayout layLoadingFailed;
    private LinearLayout layClickReload;
    private ProgressBar loadingProgress;
    private View contentView;
    private boolean isFirstLoading = false;
    private Toast toast;

    /* 子类使用的时候无需再次调用onCreate(),如需要加载其他方法可重写该方法 */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        LogUtils.e("getSimpleName", this.getClass().getSimpleName().toString());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        AppManager.getAppManager().addActivity(this);
        initBase();
        addContentView();
        initVariable();
        findViews();
        initData();
        setListener();

    }


    private void initBase()
    {
        mContext = getApplicationContext();
        mActivity=this;
//        setTranslucentStatusBar(R.color.colorPrimary);
        mInflater = getLayoutInflater();
        // topbar相关
        layTopBar = (RelativeLayout) findViewById(R.id.layTopBar);
        tvTitle = (TextView) findViewById(R.id.tvTopBarTitle);
        btnLeft = (ImageButton) findViewById(R.id.btnLeft);
        btnRight = (ImageButton) findViewById(R.id.btnRight);

        // 内容区
        layBody = (FrameLayout) findViewById(R.id.layBody);
        stubLoadingFailed = (ViewStub) findViewById(R.id.stubLoadingFailed);

        hideTopBar();
    }


    /**
     * 不根据系统的配置改变
     * @return
     */
    @Override
    public Resources getResources()
    {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    public void hideTopBar()
    {
        layTopBar.setVisibility(View.GONE);
    }

    public void showTopBar()
    {
        layTopBar.setVisibility(View.VISIBLE);
    }

    /**
     * 得到左边的按钮
     */
    public ImageButton getLeftButton()
    {
        btnLeft.setVisibility(View.VISIBLE);
        return btnLeft;
    }

    /**
     * 得到右边的按钮
     */
    public ImageButton getRightButton()
    {
        btnRight.setVisibility(View.VISIBLE);
        return btnRight;
    }

    /**
     * 设置标题
     */
    public void setTitle(String title)
    {
        tvTitle.setText(title);
    }

    /**
     * 获取标题
     */
    public TextView getTitleView()
    {
        return tvTitle;
    }

    /**
     * 设置自定义view
     */
    public void setCustomTopBar(int resId)
    {
        View view = mInflater.inflate(resId, null);
        layTopBar.removeAllViews();
        layTopBar.addView(view);
    }

    public boolean isTranslucentStatusBar()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            return true;
        }
        return false;
    }

    public void setTranslucentStatusBar(int ResId)
    {
        View customStatusBarView = $(R.id.customStatusBarView);
        if (customStatusBarView != null) {
            if (isTranslucentStatusBar()) {
                customStatusBarView.setVisibility(View.VISIBLE);
                customStatusBarView.setBackgroundResource(ResId);
            }
        }
    }

    /*如果首次加载先失败，就显示失败界面，
    对于一个界面多个接口，之间不会有影响
    isFirstLoading=false;
    */
    public void onActivityLoadingFailed()
    {
        if (layLoadingFailed == null || !isFirstLoading) {
            return;
        }
        layClickReload.setVisibility(View.VISIBLE);
        loadingProgress.setVisibility(View.INVISIBLE);
        isFirstLoading = false;
    }

    /*如果首次加载先成功，就显示成功界面，
       对于一个界面多个接口，之间不会有影响
       isFirstLoading=false;
       */
    public void onActivityLoadingSuccess()
    {

        if (layLoadingFailed == null || !isFirstLoading) {
            return;
        }
        layLoadingFailed.setVisibility(View.GONE);
        if (contentView != null) {
            contentView.setVisibility(View.VISIBLE);
        }
        isFirstLoading = false;
    }

    public void startActivityLoading()
    {
        if (layLoadingFailed == null) {
            layLoadingFailed = (FrameLayout) stubLoadingFailed.inflate();
            layClickReload = (LinearLayout) layLoadingFailed.findViewById(R.id.layClickReload);
            loadingProgress = (ProgressBar) layLoadingFailed.findViewById(R.id.loadingProgress);
            layClickReload.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    startActivityLoading();
                }
            });
        }
        layLoadingFailed.setVisibility(View.VISIBLE);
        layClickReload.setVisibility(View.INVISIBLE);
        loadingProgress.setVisibility(View.VISIBLE);

        if (contentView != null) {
            contentView.setVisibility(View.INVISIBLE);
        }
        isFirstLoading = true;
        onActivityLoading();
    }

    /***
     * 设置内容区域
     */
    public void addContentView()
    {
        int resId = setLayoutView();
        View layContentView = mInflater.inflate(resId, null);
        if (layContentView == null) {
            return;
        }
        layContentView.setBackgroundColor(0x00000000);
        contentView = layContentView.findViewById(R.id.content);
        if (contentView == null) {
            contentView = layContentView;
        }
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layContentView.setLayoutParams(layoutParams);
        layBody.addView(layContentView, 0);
    }

    /**
     * 得到内容的View
     */
    public View getContentView()
    {
        return contentView;
    }

    protected abstract int setLayoutView();

    protected abstract void initVariable();

    protected abstract void findViews();

    protected abstract void initData();

    protected abstract void setListener();

    public abstract void doClick(View view);

    protected void onActivityLoading()
    {

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        AppManager.getAppManager().finishActivity(this);
    }


    @SuppressWarnings("unchecked")
    protected final <T extends View> T $(@IdRes int id)
    {
        return (T) (findViewById(id));
    }

    @SuppressWarnings("unchecked")
    protected final <T extends View> T $(@NonNull View view, @IdRes int id)
    {
        return (T) (view.findViewById(id));
    }


    private void showTextToast(String msg) {
        if (toast == null) {
            toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }


}
