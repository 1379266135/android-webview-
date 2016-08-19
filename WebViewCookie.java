package com.wangjiumobile;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by elaine on 16/8/19.
 * Android WebView loadUrl的时候经常遇到跳转的是一个中转页面，通过这个中转页面跳转到最终目的页面。
 这里的多个连接地址， 可能出现多个域名，多个协议 等等。

 如果你的项目中有这样的需求，如Native App已经登录了，需要将登录的信息同步到Html中，  我们的操作是将 同步信息写入cookie中。方便后端、m站和pc端获取相应的数据。

 在这种情况下，就极有可能出现，由于跨域而引起的cookie丢失问题。

 核心方法：

 */
public class WebViewCookie extends WebView{
    public WebViewCookie(Context context) {
        super(context);
        init();
    }

    public WebViewCookie(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        String ua = getSettings().getUserAgentString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(this,true);
        }
        this.getSettings().setAllowFileAccess(true);
        //如果访问的页面中有Javascript，则webview必须设置支持Javascript
        this.getSettings().setJavaScriptEnabled(true);
        this.getSettings().setAllowFileAccess(true);
        this.getSettings().setAppCacheEnabled(true);
        this.getSettings().setDomStorageEnabled(true);
        this.getSettings().setDatabaseEnabled(true);
        this.getSettings().setUserAgentString(ua + "; from:android ; from/android" + "; appversion:" + "版本号");
        setWebViewClient(new ElaineWebViewCline());
    }

    private class ElaineWebViewCline extends WebViewClient{

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            synCookies(view.getContext(), url, getHostString(url));
            view.loadUrl(url);
            return true;
        }
    }

    private String getHostString(String url){
        String host = null;
        Uri uri = Uri.parse(url);
        if (uri != null){
            host = uri.getHost();
        }

        return host;
    }

    /**
     * 这里是将信息写入cookie；
     * @param context
     * @param url
     * @param host 域名字符串或地址
     */
    public  void synCookies(Context context, String url, String host) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

        cookieManager.removeSessionCookie();
        cookieManager.removeAllCookie();

        StringBuilder sbCookie = new StringBuilder();
        sbCookie.append(String.format("JSESSIONID=%s", "test  id "));
        sbCookie.append(String.format(";domain=%s", host));
        sbCookie.append(String.format(";path=%s", "/;"));
        String left = sbCookie.toString();
        cookieManager.setCookie(url, left);

        StringBuilder sbCookie2 = new StringBuilder();
        sbCookie2.append(String.format("COOKIE_SESSION_ID=%s","你的sessionId"));
        sbCookie2.append(String.format(";domain=%s", host));
        sbCookie2.append(String.format(";path=%s", "/;"));

        cookieManager.setCookie(url,sbCookie2.toString());

        StringBuilder sbCookie3 = new StringBuilder();
        sbCookie3.append(String.format("COOKIE_USER_ID=%s","你的userId"));
        sbCookie3.append(String.format(";domain=%s", host));
        sbCookie3.append(String.format(";path=%s", "/;"));

        cookieManager.setCookie(url, sbCookie3.toString());

        StringBuilder sbCookie4 = new StringBuilder();
        sbCookie4.append(String.format("COOKIE_TOKEN_ID=%s","你的tokenId"));
        sbCookie4.append(String.format(";domain=%s", host));
        sbCookie4.append(String.format(";path=%s", "/;"));

        cookieManager.setCookie(url,sbCookie4.toString());

        StringBuilder sbCookie6 = new StringBuilder();
        sbCookie6.append(String.format("COOKIE_TOKEN_DATE=%s","你的token Date"));
        sbCookie6.append(String.format(";domain=%s", host));
        sbCookie6.append(String.format(";path=%s", "/;"));

        cookieManager.setCookie(url,sbCookie6.toString());

        /* ...

        */

        CookieSyncManager.getInstance().sync();
        String s2 = cookieManager.getCookie(url);
    }
}
