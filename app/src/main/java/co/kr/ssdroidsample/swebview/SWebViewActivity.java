package co.kr.ssdroidsample.swebview;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

import co.kr.ssdroidlib.webview.ISWebView;
import co.kr.ssdroidlib.webview.SMultiWebView;
import co.kr.ssdroidlib.webview.SWebView;
import co.kr.ssdroidsample.R;

public class SWebViewActivity extends AppCompatActivity implements ISWebView {
    SMultiWebView mWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swebview);
        setTitle("SWebView");

        mWebView = (SMultiWebView)findViewById(R.id.webview);
        mWebView.SetInterface(this);
        //mWebView.loadUrl("http://58.181.28.39:8080/SimpTLSvc/WSClient.html");
        mWebView.loadUrl("https://snu.ac.kr");
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mWebView.onActivityResult(requestCode,resultCode,data); //파일 업로드시에 사용한다.
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK: {
                    if (mWebView.canGoBack()) mWebView.goBack();
                    else return super.onKeyDown(keyCode, event);
                    return false;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    //-------------------------------------------
    //Start ISWebView
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.d("JavaSong","shouldOverrideUrlLoading " + url);
        return false;
    }

    @Override
    public void onReceivedError(WebView view, int errorcode, String description, String fallingUrl) {

    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {

    }

    @Override
    public void onPageFinished(WebView view, String Url) {

    }

    /**
     * window.ssdroid.postMessage(xxx)
     * @param view
     * @param data
     */
    @Override
    public void postMessage(WebView view, String data) {
        try {
            JSONObject dic = new JSONObject(data);
            Log.d("JavaSong","postMessage  " + data);
        }
        catch (JSONException e)
        {
            Log.e("JavaSongs","postMessage " + e.getLocalizedMessage());
        }
    }

    @Override
    public void toastLong(WebView view, String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void toastShort(WebView view, String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    //End ISWebView
    //-------------------------------------------
}
