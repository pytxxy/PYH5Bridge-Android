[ ![Download](https://api.bintray.com/packages/tianxiaxinyong/maven/PYH5Bridge/images/download.svg?version=1.2.1) ](https://bintray.com/tianxiaxinyong/maven/PYH5Bridge/1.2.1/link)

**H5Bridge android集成与使用流程**

**注意：接入本sdk后的应用不可使用热修复方式发版，因为sdk中有在AndroidManifest.xml中新增Activity与ContentProvider**



一、**集成SDK**
工程根目录下的build.gradle中加上如下代码

```  
allprojects {
    repositories {
        jcenter()
        maven {
             url "https://jitpack.io" //引用PhotoView用到
        }
    }
}

```

具体使用此SDK的模块下的build.gradle中加上如下依赖

```
dependencies {
    compile 'com.pycredit.h5sdk:h5bridge:x.x.x' //x.x.x为SDK版本号,如:1.2.1
}
```

>SDK中引用的第三方库说明  

[PhotoView](https://github.com/chrisbanes/PhotoView)预览控件，预览照片时用到  

[universal-image-loader](https://github.com/nostra13/Android-Universal-Image-Loader)图片缓存，预览照片时用到  

[okhttp3](https://github.com/square/okhttp)网络请求用到    

>混淆  

```  
-keep class com.pycredit.h5sdk.** { *; }
-keep class com.google.android.cameraview.** { *; }
```

二、**如何使用**  
#####  A.最简用法（可参考：[DefaultDemoActivity](app/src/main/java/com/pycredit/h5bridge/DefaultDemoActivity.java)）  

如果不需要对WebView有任何特殊处理，只用一个页面展示报告，则可以使用[DefaultWebFragment](h5bridge/src/main/java/com/pycredit/h5sdk/ui/DefaultWebFragment.java)  

#####  B.自定义用法（可参考：[DemoActivity](app/src/main/java/com/pycredit/h5bridge/DemoActivity.java))  
   
1、实例化H5SDKHelper  

```  
h5SDKHelper = new H5SDKHelper(this, mWebView);
```

> 注意：WebView所在页面Activity需要配置成竖屏  

```  
android:screenOrientation="portrait"
```


2、WebView所在的Activity/Fragment生命周期中调用H5SDKHelper对应的生命周期方法  

```  
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
```
3、处理返回按钮逻辑，H5SDKHelper优先处理返回，用于实现页面回退  

```  
@Override
public void onBackPressed() {
    if (h5SDKHelper.onBackPressed()) {
        return;
    }
    super.onBackPressed();
}
```

> 注意：有可能会遇到按返回一直在重复加载第一个页面这种情况，建议在页面合适的地方加一个直接关闭页面的按钮。（比如:在页面右上角加个关闭menu）  


4、处理网页面重定向,主要是为了处理点击网页中的电话号码调起拔打电话页面，还有调起微信、支付宝处理  
在WebViewClient的shouldOverrideUrlLoading方法中加上下面代码：  

```  
@Override
public boolean shouldOverrideUrlLoading(WebView view, String url) {
    if (h5SDKHelper.shouldOverrideUrlLoading(view, url)) {//注意这部份SDK处理放在本方法最前面
        return true;
    }
    return super.shouldOverrideUrlLoading(view, url);
}

```

5、处理上传文件功能（重要）  
重写WebChromeClient的onShowFileChooser、openFileChooser方法  

```  
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
```

6、处理地理位置请求  
重写WebChromeClient的onGeolocationPermissionsShowPrompt  

```  
@Override
public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
    h5SDKHelper.onGeolocationPermissionsShowPrompt(origin, callback);
}
```

7、处理5.0及5.0以上WebView录视频、录音权限请求
重写WebChromeClient的onPermissionRequest  

```
@Override
public void onPermissionRequest(PermissionRequest request) {
    h5SDKHelper.onPermissionRequest(request);
}
```

> 如果你的WebView没有别的特殊处理，上面的4、5、6、7步可以直接使用h5SDKHelper.initDefaultSettings(),或者分别使用DefaultWebViewClient,DefaultWebChromeClient，详情参考[DemoActivity](app/src/main/java/com/pycredit/h5bridge/DemoActivity.java)  


8、添加广告（可选）  

```  
//如果需要在H5页面插入广告，使用如下方式，传入广告图片地址及回调
//请使用 https:// 协议的网址，否则可能导致协议错误无法加载
h5SDKHelper.setBanner("https://apk-txxy.oss-cn-shenzhen.aliyuncs.com/test_ad.png", new BannerCallback() {
    @Override
    public void onBannerClick() {
        Toast.makeText(getApplicationContext(), "广告被点击了", Toast.LENGTH_SHORT).show();
    }
});
```

**当前支持android系统版本**
1. 当前支持android 4.1(API 16)及以上系统版本；
2. android 4.4.4系统不支持调起微信支付



[更新日志](change_log.md)  

