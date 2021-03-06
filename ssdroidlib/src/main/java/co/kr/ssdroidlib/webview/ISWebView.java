package co.kr.ssdroidlib.webview;

import android.graphics.Bitmap;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

public interface ISWebView {
    public WebResourceResponse shouldInterceptRequest(WebView view, String url);
    public boolean shouldOverrideUrlLoading(WebView view, String url);
    public void onReceivedError(WebView view, int errorcode, String description, String fallingUrl);
    public void onPageStarted(WebView view, String url, Bitmap favicon);
    public void onPageFinished(WebView view, String Url);

    //Javascript Handler
    public void postMessage(WebView view, String data);
    public void toastLong (WebView view, String message);
    public void toastShort (WebView view, String message);
}
