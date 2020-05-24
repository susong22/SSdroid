package co.kr.ssdroidsample.swebview;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import co.kr.ssdroidlib.webview.SWebView;
import co.kr.ssdroidsample.R;

public class SWebViewActivity extends AppCompatActivity {
    SWebView mWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swebview);
        setTitle("SWebView");

        mWebView = (SWebView)findViewById(R.id.webview);
        mWebView.SetWebSettings(this);

        mWebView.loadUrl("http://58.181.28.39:8080/SimpTLSvc/WSClient.html");
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


    @JavascriptInterface
    public void postMessage(String data) {

        try {
            JSONObject dic = new JSONObject(data);
            String type = dic.getString("type");
            if(type != null)
            {
                if(type.compareToIgnoreCase("syncdata") == 0)
                {
                    //NeedUpdateData();
                }
                else if(type.compareToIgnoreCase("reqauth") == 0)
                {
                    //PosReqAuth();
                }
            }

        }
        catch (JSONException e)
        {
            Log.e("JavaSongs","postMessage " + e.getLocalizedMessage());
        }
    }
}
