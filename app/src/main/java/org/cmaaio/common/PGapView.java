package org.cmaaio.common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

import org.apache.cordova.AuthenticationToken;
import org.apache.cordova.CordovaChromeClient;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewClient;
import org.apache.cordova.IceCreamCordovaWebViewClient;
import org.apache.cordova.api.CordovaInterface;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.LOG;
import org.cmaaio.activity.Cmaaio;
import org.cmaaio.activity.HomeActivity;
import org.cmaaio.activity.PGapActivity;
import org.cmaaio.util.CommonUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PGapView extends LinearLayout {
    public CordovaWebView appView;
    protected CordovaWebViewClient webViewClient;
    private Activity context = null;

    protected boolean cancelLoadUrl = false;
    protected ProgressDialog spinnerDialog = null;
    private final ExecutorService threadPool = Executors.newCachedThreadPool();
    protected boolean keepRunning = true;
    private boolean isWebViewTouch = true;// webview是否响应touch

    private int lastRequestCode;
    private Object responseCode;
    private Intent lastIntent;
    private Object lastResponseCode;
    private String initCallbackClass;
    // 进度
    private View loadingView = null;

    public void setLoadingView(View loading) {
        loadingView = loading;
    }

    public PGapView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public PGapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public PGapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public void setContext(Activity context) {
        this.context = context;
    }

    /**
     * Create and initialize web container with default web view objects.
     */
    public void init() {
        CordovaWebView webView = new CordovaWebView(this.getContext(),
                cordovaCall);
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            webViewClient = new CordovaWebViewClient(cordovaCall, webView) {
                // 开始加载
                @Override
                public void onPageStarted(WebView view, String url,
                                          Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    if (loadingView != null) {
                        Log.d("loading", "pagestarted");
                        loadingView.setVisibility(View.VISIBLE);
                    }
                }

                // 加载结束
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    if (loadingView != null) {
                        Log.d("loading", "pageend");
                        loadingView.setVisibility(View.GONE);
                    }
                    Cmaaio.getInstance().sendBroadcast(
                            new Intent(HomeActivity.HOME_INIT_OK_BROADCAST));
                }
            };
        } else {
            webViewClient = new IceCreamCordovaWebViewClient(cordovaCall,
                    webView) {
                // 开始加载
                @Override
                public void onPageStarted(WebView view, String url,
                                          Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    if (loadingView != null) {
                        Log.d("loading", "pagestarted");
                        loadingView.setVisibility(View.VISIBLE);
                    }

                }

                // 加载界面
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    if (loadingView != null) {
                        Log.d("loading", "pageend");
                        loadingView.setVisibility(View.GONE);

                    }
                    Cmaaio.getInstance().sendBroadcast(
                            new Intent(HomeActivity.HOME_INIT_OK_BROADCAST));
                }
            };
        }
        // ssl add by wj
        AuthenticationToken authenticationToken = new AuthenticationToken();
        authenticationToken.setUserName(Constants.userName);
        authenticationToken.setPassword(Constants.userPwd);
        webViewClient.setAuthenticationToken(authenticationToken, "", "");
        this.init(webView, webViewClient, new CordovaChromeClient(cordovaCall,
                webView));
    }

    /**
     * Initialize web container with web view objects.
     *
     * @param webView
     * @param webViewClient
     * @param webChromeClient
     */
    public void init(CordovaWebView webView,
                     CordovaWebViewClient webViewClient,
                     CordovaChromeClient webChromeClient) {

        // Set up web container
        this.appView = webView;
        //this.appView.setId(100);

        this.appView.setWebViewClient(webViewClient);
        this.appView.setWebChromeClient(webChromeClient);
        webViewClient.setWebView(this.appView);
        webChromeClient.setWebView(this.appView);

        this.appView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 1.0F));

        if (this.getBooleanProperty("disallowOverscroll", false)) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
                this.appView
                        .setOverScrollMode(CordovaWebView.OVER_SCROLL_NEVER);
            }
        }

        // Add web view but make it invisible while loading URL
        this.appView.setVisibility(View.INVISIBLE);
        this.addView(this.appView);
        // setContentView(this.root);

        // Clear cancel flag
        // this.cancelLoadUrl = false;

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        return !isWebViewTouch;// 是否截断touch事件
    }

    public void setWebViewTouch(boolean enable) {
        isWebViewTouch = enable;
    }

    /**
     * Load the url into the webview.
     *
     * @param url
     */
    public void loadUrl(String url) {

        // Init web view if not already done
        if (this.appView == null) {
            this.init();
        }
        // modify cache setting
        appView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        appView.clearHistory();
        appView.clearFormData();
        appView.clearCache(true);
        // CookieManager.getInstance().removeAllCookie();
        // Set backgroundColor
        this.setBackgroundColor(this.getIntegerProperty("backgroundColor",
                Color.WHITE));

        // If keepRunning
        this.keepRunning = this.getBooleanProperty("keepRunning", true);

        // Then load the spinner
        this.loadSpinner();
        this.appView.loadUrlIntoView(url);
        LOG.setLogLevel(Log.DEBUG);
        // this.appView.loadUrl(url);
    }

    void loadSpinner() {

        // If loadingDialog property, then show the App loading dialog for first
        // page of app
        String loading = null;
        if ((this.appView == null) || !this.appView.canGoBack()) {
            loading = this.getStringProperty("loadingDialog", null);
        } else {
            loading = this.getStringProperty("loadingPageDialog", null);
        }
        if (loading != null) {

            String title = "";
            String message = "Loading Application...";

            if (loading.length() > 0) {
                int comma = loading.indexOf(',');
                if (comma > 0) {
                    title = loading.substring(0, comma);
                    message = loading.substring(comma + 1);
                } else {
                    title = "";
                    message = loading;
                }
            }
            this.spinnerStart(title, message);
        }
    }

    public void spinnerStart(final String title, final String message) {
        if (this.spinnerDialog != null) {
            this.spinnerDialog.dismiss();
            this.spinnerDialog = null;
        }

        this.spinnerDialog = ProgressDialog.show(this.getContext(), title,
                message, true, true, new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        PGapView.this.spinnerDialog = null;
                    }
                });
    }

    /**
     * Cancel loadUrl before it has been loaded.
     */
    // TODO NO-OP
    @Deprecated
    public void cancelLoadUrl() {
        this.cancelLoadUrl = true;
    }

    /**
     * Clear the resource cache.
     */
    public void clearCache() {
        if (this.appView == null) {
            this.init();
        }
        this.appView.clearCache(true);
    }

    /**
     * Clear web history in this web view.
     */
    public void clearHistory() {
        this.appView.clearHistory();
    }

    /**
     * Go to previous page in history. (We manage our own history)
     *
     * @return true if we went back, false if we are already at top
     */
    public boolean backHistory() {
        if (this.appView != null) {
            return appView.backHistory();
        }
        return false;
    }

    /**
     * Get boolean property for activity.
     *
     * @param name
     * @param defaultValue
     * @return
     */
    public boolean getBooleanProperty(String name, boolean defaultValue) {
        Bundle bundle = null;// this.getIntent().getExtras();
        if (bundle == null) {
            return defaultValue;
        }
        Boolean p;
        try {
            p = (Boolean) bundle.get(name);
        } catch (ClassCastException e) {
            String s = bundle.get(name).toString();
            if ("true".equals(s)) {
                p = true;
            } else {
                p = false;
            }
        }
        if (p == null) {
            return defaultValue;
        }
        return p.booleanValue();
    }

    /**
     * Get int property for activity.
     *
     * @param name
     * @param defaultValue
     * @return
     */
    public int getIntegerProperty(String name, int defaultValue) {
        Bundle bundle = null;// this.getIntent().getExtras();
        if (bundle == null) {
            return defaultValue;
        }
        Integer p;
        try {
            p = (Integer) bundle.get(name);
        } catch (ClassCastException e) {
            p = Integer.parseInt(bundle.get(name).toString());
        }
        if (p == null) {
            return defaultValue;
        }
        return p.intValue();
    }

    /**
     * Get string property for activity.
     *
     * @param name
     * @param defaultValue
     * @return
     */
    public String getStringProperty(String name, String defaultValue) {
        Bundle bundle = null;// this.getIntent().getExtras();
        if (bundle == null) {
            return defaultValue;
        }
        String p = bundle.getString(name);
        if (p == null) {
            return defaultValue;
        }
        return p;
    }

    /**
     * Get double property for activity.
     *
     * @param name
     * @param defaultValue
     * @return
     */
    public double getDoubleProperty(String name, double defaultValue) {
        Bundle bundle = null;// this.getIntent().getExtras();
        if (bundle == null) {
            return defaultValue;
        }
        Double p;
        try {
            p = (Double) bundle.get(name);
        } catch (ClassCastException e) {
            p = Double.parseDouble(bundle.get(name).toString());
        }
        if (p == null) {
            return defaultValue;
        }
        return p.doubleValue();
    }

    /**
     * Set boolean property on activity.
     *
     * @param name
     * @param value
     */
    public void setBooleanProperty(String name, boolean value) {

    }

    /**
     * Set int property on activity.
     *
     * @param name
     * @param value
     */
    public void setIntegerProperty(String name, int value) {

    }

    /**
     * Set string property on activity.
     *
     * @param name
     * @param value
     */
    public void setStringProperty(String name, String value) {

    }

    /**
     * Set double property on activity.
     *
     * @param name
     * @param value
     */
    public void setDoubleProperty(String name, double value) {

    }

    // for inteface
    protected CordovaPlugin activityResultCallback = null;
    protected boolean activityResultKeepRunning = false;

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d("PGapView", "onActivityResult: ");

//		ValueCallback<Uri> mUploadMessage = this.appView.getWebChromeClient()
//				.getValueCallback();
//		if (requestCode == CordovaChromeClient.FILECHOOSER_RESULTCODE || requestCode == 17) {
//			if (null == mUploadMessage)
//				return;
//			Uri result = intent == null || resultCode != Activity.RESULT_OK ? null
//					: intent.getData();
//			mUploadMessage.onReceiveValue(result);
//			mUploadMessage = null;
//		}

        if (intent != null && intent.getData() != null) {
            Uri uri = intent.getData();
            String imagePath = CommonUtil.getPath(context, uri);

            intent.setData(Uri.parse(imagePath));
        }

        CordovaPlugin callback = this.activityResultCallback;
        if (callback == null) {
            if (initCallbackClass != null) {
                this.activityResultCallback = appView.pluginManager
                        .getPlugin(initCallbackClass);
                callback = activityResultCallback;
                callback.onActivityResult(requestCode, resultCode, intent);
            }
        } else {
            callback.onActivityResult(requestCode, resultCode, intent);
        }
    }

    private CordovaInterface cordovaCall = new CordovaInterface() {
        public Activity getActivity() {
            // TODO Auto-generated method stub
            return context;
        }

        @Override
        public ExecutorService getThreadPool() {
            // TODO Auto-generated method stub
            return threadPool;
        }

        @Override
        public Object onMessage(String id, Object data) {
            if ("spinner".equals(id)) {
                if ("stop".equals(data.toString())) {
                    PGapView.this.appView.setVisibility(View.VISIBLE);
                }
            }
            if (PGapView.this.context != null
                    && PGapView.this.context.getClass().equals(
                    PGapActivity.class)) {
                PGapActivity context = (PGapActivity) PGapView.this.context;
                context.onMessage(id, data);
            }
            return null;
        }

        @Override
        public void setActivityResultCallback(CordovaPlugin plugin) {
            // TODO Auto-generated method stub
            activityResultCallback = plugin;
        }

        @Override
        public void startActivityForResult(CordovaPlugin command,
                                           Intent intent, int requestCode) {
            // TODO Auto-generated method stub
            activityResultCallback = command;
            activityResultKeepRunning = keepRunning;

            // If multitasking turned on, then disable it for activities that
            // return results
            if (command != null) {
                keepRunning = false;
            }
            // Start activity
            if (context != null)
                context.startActivityForResult(intent, requestCode);
        }
    };

}
