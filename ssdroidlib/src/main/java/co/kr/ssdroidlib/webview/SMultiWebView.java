package co.kr.ssdroidlib.webview;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import co.kr.ssdroidlib.comm.SUtils;

public class SMultiWebView extends LinearLayout implements SMultiWebViewTabBar.SMultiWebViewTabBarEvent {
    Context mContext;
    SMultiWebViewTabBar  mTabBar;
    LinearLayout          mTabBarContent;
    ISWebView mInterface;
    Map<Long,SWebView> mapData = new HashMap<Long,SWebView>();

    public SMultiWebView(Context context) {
        super(context);
        Init(context);
    }

    public SMultiWebView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Init(context);
    }

    void Init(Context context)
    {
        mContext = context;
        setOrientation(LinearLayout.VERTICAL);

        mTabBar = new SMultiWebViewTabBar(context);
        mTabBar.setVisibility(View.GONE);
        mTabBar.SetEvent(this);
        addView(mTabBar);
        AddWebView("Main",false);
    }

    public void SetInterface(ISWebView Inter) {
        mInterface = Inter;
        for(Map.Entry<Long, SWebView> entry : mapData.entrySet()) {
            SWebView value = entry.getValue();
            if(value.GetInterface() != mInterface)
                value.SetInterface(Inter);
        }
    }

    public SWebView FindViewWebView(long id) {
        return mapData.get(id);
    }

    public long FindViewID(View view) {
        for(Map.Entry<Long, SWebView> entry : mapData.entrySet()) {
            SWebView value = entry.getValue();
            if(value == view)
                return entry.getKey();
        }
        return 0;
    }

    public SWebView GetFocusWebView()
    {
        long id = GetFocus();
        return mapData.get(id);
    }

    public void SetFocus(long ID)
    {
        for(Map.Entry<Long, SWebView> entry : mapData.entrySet()) {
            SWebView value = entry.getValue();
            if(entry.getKey() == ID) {
                mTabBar.SetFocus(ID);
                value.setVisibility(View.VISIBLE);
            }
            else {
                if(value.getVisibility() != View.GONE)
                    value.setVisibility(View.GONE);
            }
        }
    }

    public long GetFocus()
    {
        return mTabBar.GetFocus();
    }

    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        long id = GetFocus();
        SWebView webview = mapData.get(id);
        if(webview != null)
            webview.loadUrl(url,additionalHttpHeaders);
    }

    public void loadUrl(String url) {
        long id = GetFocus();
        SWebView webview = mapData.get(id);
        if(webview != null) webview.loadUrl(url);
    }

    public void postUrl(String url, byte[] postData) {
        long id = GetFocus();
        SWebView webview = mapData.get(id);
        if(webview != null)
            webview.postUrl(url,postData);
    }


    public void loadUrl(long id,String url, Map<String, String> additionalHttpHeaders) {
        SWebView webview = mapData.get(id);
        if(webview != null)
            webview.loadUrl(url,additionalHttpHeaders);
    }

    public void loadUrl(long id,String url) {
        SWebView webview = mapData.get(id);
        if(webview != null)
            webview.loadUrl(url);

    }

    public void postUrl(long id,String url, byte[] postData) {
        SWebView webview = mapData.get(id);
        if(webview != null)
            webview.postUrl(url,postData);
    }

    public SWebView AddWebView(String sTitle,boolean bClosable)
    {
        long ID = SUtils.NewID();
        mTabBar.AddTab(ID,sTitle,bClosable,true);
        SWebView webView = new SWebView(mContext);
        webView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(webView);
        webView.SetID(ID);
        webView.SetMultiWebView(this);
        webView.SetWebSettings();
        webView.SetInterface(mInterface);
        mapData.put(ID,webView);
        SetFocus(ID);

        if(mapData.size() > 1) mTabBar.setVisibility(VISIBLE);
        else mTabBar.setVisibility(GONE);
        return webView;
    }


    public void RemoveWebView(long ID)
    {
        SWebView webView = mapData.get(ID);
        if(webView != null)
        {
            removeView(webView);
            mapData.remove(ID);
        }
        mTabBar.RemoveTab(ID);

        if(mapData.size() > 1) mTabBar.setVisibility(VISIBLE);
        else mTabBar.setVisibility(GONE);
    }
    public boolean canGoBack()
    {
        long ID = GetFocus();
        SWebView webView = mapData.get(ID);
        if(webView != null)
        {
            return webView.canGoBack();
        }
        return false;
    }

    public boolean canGoForward()
    {
        long ID = GetFocus();
        SWebView webView = mapData.get(ID);
        if(webView != null)
        {
            return webView.canGoForward();
        }
        return false;
    }

    public void goBack()
    {
        long ID = GetFocus();
        SWebView webView = mapData.get(ID);
        if(webView != null)
        {
            webView.goBack();
        }
    }

    public void goForward()
    {
        long ID = GetFocus();
        SWebView webView = mapData.get(ID);
        if(webView != null)
        {
            webView.goForward();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        long ID = GetFocus();
        SWebView webView = mapData.get(ID);
        if(webView != null)
        {
            webView.onActivityResult(requestCode,resultCode,data);
        }
    }

    //SMultiWebViewTabBar.SMultiWebViewTabBarEvent

    @Override
    public void OnClicked(long id) {
        SetFocus(id);
    }

    @Override
    public void OnRemoved(long id) {
        //RemoveWebView(id);
        SWebView webView = FindViewWebView(id);
        if(webView != null)
            webView.loadUrl("javascript:window.close();");
    }

    //End SMultiWebViewTabBar.SMultiWebViewTabBarEvent
}
