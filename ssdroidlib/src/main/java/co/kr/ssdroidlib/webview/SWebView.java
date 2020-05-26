package co.kr.ssdroidlib.webview;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.DOWNLOAD_SERVICE;

public class SWebView extends WebView {


    protected ISWebView mInterface;
    protected Context mContext;
    protected ProgressBar mProgressBar;
    protected long mID;
    protected SMultiWebView mMultiWebView;
    // -------------------------------------------------------------------------------
    // 파일 업로드..
    // -------------------------------------------------------------------------------
    protected static final String TYPE_IMAGE = "image/*";
    protected static final String TYPE_ALL = "*/*";
    public final static int INPUT_FILE_REQUEST_CODE = 1991;
    public static ValueCallback<Uri> mUploadMessage;
    public static ValueCallback<Uri[]> mFilePathCallback;
    public static String mCameraPhotoPath;
    // -------------------------------------------------------------------------------

    public SWebView(Context context) {
        super(context);
        mContext = context;

    }

    public SWebView(Context context, AttributeSet attrs) {
        super(context,attrs);
        mContext = context;

    }
    public SWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context,attrs,defStyle);
        mContext = context;
    }

    public void SetMultiWebView(SMultiWebView v) { mMultiWebView = v;}

    public long GetID() { return mID;}
    public void SetID(long v) { mID = v;}
    public ISWebView GetInterface() {
        return mInterface;
    }
    public void SetInterface(ISWebView Inter) {
        mInterface = Inter;
    }
    public void SetProgressBar(ProgressBar bar)
    {
        mProgressBar = bar;
    }
    public void ExecuteJS(String sInJSFunction)
    {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            evaluateJavascript(sInJSFunction.replace("javascript::",""),null);
        } else {
            loadUrl(sInJSFunction);
        }
    }


    //File Upload 실행해주어야 한다.
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        //웹뷰에서 파일 업로드를 해준다.
        if (requestCode == INPUT_FILE_REQUEST_CODE && resultCode == RESULT_OK)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (mFilePathCallback == null) {
                    return;
                }
                Uri[] results = new Uri[]{getResultUri(data)};

                mFilePathCallback.onReceiveValue(results);
                mFilePathCallback = null;
            } else {
                if (mUploadMessage == null) {
                    return;
                }
                Uri result = getResultUri(data);
                Log.d(getClass().getName(), "openFileChooser : "+result);
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
        else if(requestCode == INPUT_FILE_REQUEST_CODE)
        {
            if (mFilePathCallback != null) mFilePathCallback.onReceiveValue(null);
            if (mUploadMessage != null) mUploadMessage.onReceiveValue(null);
            mFilePathCallback = null;
            mUploadMessage = null;
        }
        //-------------------------------------------------------------------------------
    }

    @SuppressLint("JavascriptInterface")
    public void SetWebSettings() {

        getSettings().setJavaScriptEnabled(true);

        // window.ssdroid.postMessage(xxx) 이렇게 자바스크립트를 콜하여 이벤트를 발생시켜준다.
        addJavascriptInterface(new JavascriptInterface(this),"ssdroid");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getSettings().setSafeBrowsingEnabled(false);
        }

        // 줌사용...
        getSettings().setSupportZoom(true);
        getSettings().setBuiltInZoomControls(true);
        getSettings().setDisplayZoomControls(false);

        //시스템설정의 폰트크기 에 따라 WebView 의 레이아웃이 변경되는 경우 (Lollipop 이슈)
        //http://1004lucifer.blogspot.com/2015/05/android-webview-lollipop.html
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            getSettings().setTextZoom(100);

        // webview.setWebViewClient(new WebViewClient()); //어떤 뷰로 실행할 것인지의 팝업이
        // 안뜨게 한다.

        //getSettings().setSupportMultipleWindows(true);
        getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        getSettings().setDomStorageEnabled(true);
        getSettings().setAllowFileAccess(true);
        getSettings().setAllowContentAccess(true);
        // webview.getSettings().setPluginsEnabled(true);
        getSettings().setPluginState(WebSettings.PluginState.ON);
        // webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

        // meta태그의 viewport사용 가능
        getSettings().setUseWideViewPort(true);
        getSettings().setLoadWithOverviewMode(true);

        getSettings().setGeolocationEnabled(true);
        getSettings().setGeolocationDatabasePath(((Activity)mContext).getFilesDir().getPath());

        getSettings().setDatabaseEnabled(true);
//		getSettings().setDomStorageEnabled(true);
        getSettings().setAppCacheEnabled(true);


        //window.open
        getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        getSettings().setSupportMultipleWindows(true);


        //파일 다운로드 관련한 설정을 추가하였다. (파일다운로드 지원)
        setDownloadListener(new DownloadListenerEx());
        // Alert 창을 보여주는 로직이다.
        setWebChromeClient(new WebChromeClientEx() );
        setWebViewClient(new WebViewClientEx());
    }


    //웹뷰에서 파일 업로드를 해준다.
    private Uri getResultUri(Intent data) {
        Uri result = null;
        if(data == null || TextUtils.isEmpty(data.getDataString())) {
            // If there is not data, then we may have taken a photo
            if(mCameraPhotoPath != null) {
                result = Uri.parse(mCameraPhotoPath);
            }
        } else {
            String filePath = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                filePath = data.getDataString();
            } else {
                filePath = "file:" + RealPathUtil.getRealPath(((Activity)mContext), data.getData());
            }
            result = Uri.parse(filePath);
        }
        return result;
    }

    class WebViewClientEx extends  WebViewClient
    {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String Url) {
            if(mInterface != null) return mInterface.shouldOverrideUrlLoading(view,Url);
            return false;
        }

        // 웹페이지 호출시 오류 발생에 대한 처리
        @Override
        public void onReceivedError(WebView view, int errorcode, String description, String fallingUrl) {
            if(mInterface != null) mInterface.onReceivedError(view,errorcode,description,fallingUrl);
        }

        // 페이지 로딩 시작시 호출
        @Override
        public void onPageStarted(WebView view, String Url, Bitmap favicon) {
            if(mProgressBar != null) mProgressBar.setVisibility(View.VISIBLE);
            if(mInterface != null) mInterface.onPageStarted(view,Url,favicon);
        }

        // 페이지 로딩 종료시 호출
        public void onPageFinished(WebView view, String Url) {
            if(mProgressBar != null) mProgressBar.setVisibility(View.INVISIBLE);
            if(mInterface != null) mInterface.onPageFinished(view,Url);

            SWebView sWebView = (SWebView)view;
            if(mMultiWebView != null) {
                if(view.getTitle().length() > 0)
                    mMultiWebView.mTabBar.SetTitle(sWebView.GetID(), view.getTitle());
            }
        }

    }

    class WebChromeClientEx extends WebChromeClient
    {

        public void onGeolocationPermissionsShowPrompt(String origin, android.webkit.GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
        }

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            if(mMultiWebView != null) {
                WebView newWebView = mMultiWebView.AddWebView("NoTitle", true);
                ((WebView.WebViewTransport) resultMsg.obj).setWebView(newWebView);
                resultMsg.sendToTarget();
            }
            return true;
        }

        @Override public void onCloseWindow(WebView webView)
        {
            if(mMultiWebView != null)
                mMultiWebView.RemoveWebView(mMultiWebView.FindViewID(webView));
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message,
                                 final android.webkit.JsResult result) {
            return super.onJsAlert(view, url, message, result);
        }

        // -------------------------------------------------------------------------------
        // 파일 업로드...
        // -------------------------------------------------------------------------------
        // For Android Version < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            // System.out.println("WebViewActivity OS Version : " +
            // Build.VERSION.SDK_INT + "\t openFC(VCU), n=1");
            mUploadMessage = uploadMsg;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            //intent.setType(TYPE_IMAGE);
            intent.setType(TYPE_ALL);
            ((Activity)mContext).startActivityForResult(intent,
                    INPUT_FILE_REQUEST_CODE);
        }

        // For 3.0 <= Android Version < 4.1
        public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                    String acceptType) {
            // System.out.println("WebViewActivity 3<A<4.1, OS Version : " +
            // Build.VERSION.SDK_INT + "\t openFC(VCU,aT), n=2");
            openFileChooser(uploadMsg, acceptType, "");
        }

        // For 4.1 <= Android Version < 5.0
        public void openFileChooser(ValueCallback<Uri> uploadFile,
                                    String acceptType, String capture) {
            Log.d(getClass().getName(), "openFileChooser : " + acceptType
                    + "/" + capture);
            mUploadMessage = uploadFile;
            imageChooser();
        }

        // For Android Version 5.0+
        // Ref:
        // https://github.com/GoogleChrome/chromium-webview-samples/blob/master/input-file-example/app/src/main/java/inputfilesample/android/chrome/google/com/inputfilesample/MainFragment.java
        public boolean onShowFileChooser(WebView webView,
                                         ValueCallback<Uri[]> filePathCallback,
                                         FileChooserParams fileChooserParams) {
            System.out.println("WebViewActivity A>5, OS Version : "
                    + Build.VERSION.SDK_INT + "\t onSFC(WV,VCUB,FCP), n=3");
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePathCallback;
            imageChooser();
            return true;
        }

        private void imageChooser() {

            Intent takePictureIntent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(((Activity)mContext).getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    takePictureIntent.putExtra("PhotoPath",
                            mCameraPhotoPath);
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Log.e(getClass().getName(),
                            "Unable to create Image File", ex);
                }

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    mCameraPhotoPath = "file:"
                            + photoFile.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                } else {
                    takePictureIntent = null;
                }
            }

            Intent contentSelectionIntent = new Intent(
                    Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            //contentSelectionIntent.setType(TYPE_IMAGE);
            contentSelectionIntent.setType(TYPE_ALL);

            Intent[] intentArray;
            if (takePictureIntent != null) {
                intentArray = new Intent[] { takePictureIntent };
            } else {
                intentArray = new Intent[0];
            }

            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_INTENT,
                    contentSelectionIntent);
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                    intentArray);

            ((Activity)mContext).startActivityForResult(
                    chooserIntent, INPUT_FILE_REQUEST_CODE);
        }

        /**
         * 파일 시스템을 오픈할 수가 있다.
         * @param minmeType
         */
        public void openFile(String minmeType)
        {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType(minmeType);
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            // special intent for Samsung file manager
            Intent sIntent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
            // if you want any file type, you can skip next line
            sIntent.putExtra("CONTENT_TYPE", minmeType);
            sIntent.addCategory(Intent.CATEGORY_DEFAULT);

            Intent chooserIntent;

            if (((Activity)mContext).getPackageManager().resolveActivity(sIntent, 0) != null){
                // it is device with samsung file manager
                chooserIntent = Intent.createChooser(sIntent, "Open file");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { intent});
            }
            else {
                chooserIntent = Intent.createChooser(intent, "Open file");
            }

            try {
                ((Activity)mContext).startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
            } catch (android.content.ActivityNotFoundException ex) {

            }
        }

        private File createImageFile() throws IOException {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                    .format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File imageFile = File.createTempFile(imageFileName, /* prefix */
                    ".jpg", /* suffix */
                    storageDir /* directory */
            );
            return imageFile;
        }
        // 파일 업로드...
        // -------------------------------------------------------------------------------
    }


    class DownloadListenerEx implements DownloadListener
    {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {

            try {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setMimeType(mimeType);
                request.addRequestHeader("User-Agent", userAgent);
                request.setDescription("Downloading file");
                String fileName = contentDisposition.replace("inline; filename=", "");
                fileName = fileName.replace("attachment; filename=", "");

                fileName = fileName.replaceAll("\"", "");
                request.setTitle(fileName);
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                DownloadManager dm = (DownloadManager) ((Activity)mContext).getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(((Activity)mContext).getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();
            } catch (Exception e) {

                if (ContextCompat.checkSelfPermission(((Activity)mContext),
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(((Activity)mContext),
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        Toast.makeText(((Activity)mContext).getBaseContext(), "첨부파일 다운로드를 위해\n동의가 필요합니다.", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(((Activity)mContext), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                110);
                    } else {
                        Toast.makeText(((Activity)mContext).getBaseContext(), "첨부파일 다운로드를 위해\n동의가 필요합니다.", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(((Activity)mContext), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                110);
                    }
                }
            }
        }
    }

    class JavascriptInterface {
        SWebView mWebView;
        public JavascriptInterface(SWebView WebView) {
            mWebView = WebView;
        }
        //window.ssdroid.toastLong( "JavscriptInterface Test" );
        @android.webkit.JavascriptInterface
        public void toastLong(String data) { if(mWebView.mInterface != null) mWebView.mInterface.toastLong(mWebView,data); }
        @android.webkit.JavascriptInterface
        public void toastShort(String data) { if(mWebView.mInterface != null) mWebView.mInterface.toastShort(mWebView,data); }
        @android.webkit.JavascriptInterface
        public void postMessage(String data) { if(mWebView.mInterface != null) mWebView.mInterface.postMessage(mWebView,data); }

    }

}
