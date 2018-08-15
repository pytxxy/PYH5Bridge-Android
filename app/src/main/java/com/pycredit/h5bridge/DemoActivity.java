package com.pycredit.h5bridge;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.pycredit.h5sdk.H5SDKHelper;
import com.pycredit.h5sdk.impl.BannerCallback;

//使用AppCompatActivity要求依赖appcompat-v7包且版本至少为25.1.0 否则请使用Activity
public class DemoActivity extends AppCompatActivity {

    private WebView mWebView;
    private TextView tvClose;
    private H5SDKHelper h5SDKHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        mWebView = (WebView) findViewById(R.id.wv_content);
        tvClose = (TextView) findViewById(R.id.tv_close);
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        h5SDKHelper = new H5SDKHelper(this, mWebView);
//        1、如果你的WebView没有别的特殊处理,只需要调用initDefaultSettings就可以了
//        ==========方法1开始==========
//        h5SDKHelper.initDefaultSettings();
//        ==========方法1结束==========



//        2、如果你的WebView暂时没有自定义了WebViewClient与WebChromeClient，同时你又有一些特处理，可以使用DefaultWebViewClient与DefaultWebChromeClient的子类
//        ==========方法2开始==========
//        mWebView.setWebViewClient(new DefaultWebViewClient(h5SDKHelper) {
//            //实现你的处理(注意不要覆盖了DefaultWebViewClient中的方法)
//        });
//        mWebView.setWebChromeClient(new DefaultWebChromeClient(h5SDKHelper) {
//            //实现你的处理(注意不要覆盖了DefaultWebChromeClient中的方法)
//        });
//        ==========方法2结束==========



//        3、如果你的WebView已经自定义了WebViewClient，与WebChromeClient，则在以下相应方法中调用h5SDKHelper的相应方法
//        ==========方法3开始==========
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (h5SDKHelper.shouldOverrideUrlLoading(view, url)) {
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            //Android 5.0 以下 必须重写此方法
            public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
                h5SDKHelper.openFileChooser(uploadFile, acceptType, capture);
            }

            //Android 5.0及5.0以上 必须重写此方法
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (h5SDKHelper.onShowFileChooser(webView, filePathCallback, fileChooserParams)) {
                    return true;
                }
                return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                h5SDKHelper.onGeolocationPermissionsShowPrompt(origin, callback);
            }

            @Override
            public void onPermissionRequest(PermissionRequest request) {
                h5SDKHelper.onPermissionRequest(request);
            }
        });
//      ==========方法3结束==========


        //如果需要在H5页面插入广告，使用如下方式，传入广告图片地址及回调
        //请使用 https:// 协议的网址，否则可能导致协议错误无法加载
        h5SDKHelper.setBanner("https://apk-txxy.oss-cn-shenzhen.aliyuncs.com/test_ad.png", new BannerCallback() {
            @Override
            public void onBannerClick() {
                Toast.makeText(getApplicationContext(), "广告被点击了", Toast.LENGTH_SHORT).show();
            }
        });
        mWebView.loadUrl("**使用鹏元提供的渠道**");
    }

    @Override
    protected void onResume() {
        super.onResume();
        h5SDKHelper.onResume();
    }

    @Override
    protected void onPause() {
        h5SDKHelper.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        h5SDKHelper.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (h5SDKHelper.onBackPressed()) {
            tvClose.setVisibility(View.VISIBLE);
            return;
        }
        super.onBackPressed();
    }
}
