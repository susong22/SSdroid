package co.kr.ssdroidlib.http;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class SHttpConnection {

    public final int Error_Connection = -1;
    public final int Error_Setup = -2;
    public final int Error_SendData = -3;
    //public final int Error_Response = -4;
    public final int Error_MalformedURL = -5;
    public final int Error_MalformedURLIO = -6;
    public final int Error_MalformedURLOther = -7;
    //public final int Error_Completed = -8;

    protected String msURL;
    protected int mnConnectionTimeOut;
    protected int mnRcvTimeOut;
    protected HttpURLConnection mConn;
    protected String mError;
    protected Thread mThread;
    protected IHttpConnection mRequestEvent;
    protected IHttpProgress mProgress;
    //Response
    protected int mContentSize;
    protected String  mDownFileName;

    public void SetError(String s) { mError = s;};
    public String GetError() { return mError;}

    public void SetHttpRequestEvent(IHttpConnection event) { mRequestEvent = event;}
    public void SetProgress(IHttpProgress Progress) { mProgress = Progress;}


    public SHttpConnection(String sURL) { msURL = sURL;}
    void Disconnection()
    {
        try
        {
            if(mConn != null) mConn.disconnect();
        }
        catch (Exception e)
        {
            SetError("Disconnection " + e.getLocalizedMessage());
            Log.e("JavaSong",GetError());
        }
    }

    boolean Connection()
    {
        URL url = null;
        try
        {
            url = new URL(msURL);
            if(mProgress != null) mProgress.OnTitle("Connecting...");
            if (url.getProtocol().toLowerCase().equals("https"))
            {
                System.setProperty("http.keepAlive", "false");
                SSLX509TrustManager.allowAllSSL();
                mConn = (HttpsURLConnection) url.openConnection();
            }
            else
            {
                mConn = (HttpURLConnection)url.openConnection();
            }
        }
        catch (Exception e)
        {
            SetError("Connection " + e.getLocalizedMessage());
            Log.e("JavaSong",GetError());
            if(mProgress != null) {mProgress.OnEnd();}
            if(mRequestEvent != null) mRequestEvent.OnConnected(Error_Connection,null,GetError());
            return false;
        }
        return true;
    }

    boolean HttpSetup(long PostSize, Map<String,String> Header)
    {
        try {

            if(mProgress != null) mProgress.OnTitle("Updating...");

            if (PostSize > 0) {
                mConn.setRequestMethod("POST");
                mConn.setRequestProperty("Content-Length", Long.toString(PostSize));
            } else
                mConn.setRequestMethod("GET");

            mConn.setDoOutput(true);
            mConn.setDoInput(true);
            mConn.setUseCaches(false);
            mConn.setDefaultUseCaches(false);
            //해더를 추가해주었다.
            if (Header != null && Header.size() > 0) {
                for (Map.Entry<String, String> entry : Header.entrySet()) {
                    mConn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            if (mnConnectionTimeOut != 0) mConn.setConnectTimeout(mnConnectionTimeOut);
            if (mnRcvTimeOut != 0) mConn.setReadTimeout(mnRcvTimeOut);
        }
        catch (Exception e)
        {
            SetError("HttpSetup " + e.getLocalizedMessage());
            Log.e("JavaSong",GetError());
            if(mProgress != null) {mProgress.OnEnd();}
            if(mRequestEvent != null) mRequestEvent.OnConnected(Error_Setup,null,GetError());
            return false;
        }
        return true;
    }

    boolean SendData(byte[] btData)
    {
        OutputStream outStream = null;
        try {
            //2> 포스트 데이터
            if (btData != null && btData.length > 0) {
                outStream = mConn.getOutputStream();
                if (outStream == null) {
                    SetError("can not send outStream");
                    Log.e("JavaSong",GetError());
                    if(mProgress != null) {mProgress.OnEnd();}
                    if(mRequestEvent != null) mRequestEvent.OnConnected(Error_SendData,null,GetError());
                    return false;
                }
                outStream.write(btData);
                outStream.flush();
                outStream.close();
                outStream = null;
            }
        }
        catch (Exception e)
        {
            SetError("SendData " + e.getLocalizedMessage());
            Log.e("JavaSong",GetError());
            if(mProgress != null) {mProgress.OnEnd();}
            if(mRequestEvent != null) mRequestEvent.OnConnected(Error_SendData,null,GetError());
            return false;
        }
        return true;
    }


    boolean SendFile(String sFilePath)
    {
        InputStream inputStream = null;
        OutputStream outStream = null;
        try
        {
            inputStream = new FileInputStream(sFilePath);
            outStream = mConn.getOutputStream();
            if (outStream == null) {
                SetError("can not send outStream");
                Log.e("JavaSong",GetError());
                if(mProgress != null) {mProgress.OnEnd();}
                if(mRequestEvent != null) mRequestEvent.OnConnected(Error_SendData,null,GetError());
                return false;
            }
            byte[] buf = new byte[4096];
            for (int nChunk = inputStream.read(buf); nChunk!=-1; nChunk = inputStream.read(buf))
            {
                outStream.write(buf, 0, nChunk);
            }
            outStream.flush();
            outStream.close();
            outStream = null;
        }
        catch (Exception e)
        {
            SetError("SendData " + e.getLocalizedMessage());
            Log.e("JavaSong",GetError());
            if(mProgress != null) {mProgress.OnEnd();}
            if(mRequestEvent != null) mRequestEvent.OnConnected(Error_SendData,null,GetError());
            return false;
        }
        return true;
    }

    int Response()
    {
        int nResponse = 0;
        try
        {
            //3>응답
            nResponse = mConn.getResponseCode();
            switch(nResponse)
            {
                case HttpURLConnection.HTTP_OK:
                    break;
                default:
                    SetError("Response " + mConn.getResponseMessage());
                    Log.e("JavaSong",GetError());
                    if(mProgress != null) {mProgress.OnEnd();}
                    if(mRequestEvent != null) mRequestEvent.OnConnected(nResponse,null,GetError());
                    return nResponse;
            }

            if(mProgress != null) mProgress.OnTitle("Downloading...");
            String sBufferSize = mConn.getHeaderField("Content-Length");
            if(sBufferSize != null) {
                mContentSize = Integer.parseInt(sBufferSize);
                if(mProgress != null) mProgress.OnTotal(mContentSize);
            }
            String sContentDispos =  mConn.getHeaderField("Content-Disposition");
            if(sContentDispos != null)
            {
                if(sContentDispos.contains("attatchment;"))
                {
                    int nIndex = sContentDispos.lastIndexOf('=');
                    if(nIndex != -1)
                    {
                        mDownFileName = sContentDispos.substring(nIndex + 1);
                        mDownFileName = mDownFileName.trim();
                    }
                    else
                        mDownFileName = sContentDispos.replaceFirst("(?i)^.*filename = \"([^\"]+)\".*$", "$1");
                }
            }
            if(mRequestEvent != null) mRequestEvent.OnConnected(HttpURLConnection.HTTP_OK,mConn.getHeaderFields(),GetError());
        }
        catch (MalformedURLException e)
        {
            SetError("SendData " + e.getLocalizedMessage());
            Log.e("JavaSong",GetError());
            if(mProgress != null) {mProgress.OnEnd();}
            if(mRequestEvent != null) mRequestEvent.OnConnected(Error_MalformedURL,null,GetError());
            return  Error_MalformedURL;
        }
        catch (IOException e)
        {
            SetError("SendData " + e.getLocalizedMessage());
            Log.e("JavaSong",GetError());
            if(mProgress != null) {mProgress.OnEnd();}
            if(mRequestEvent != null) mRequestEvent.OnConnected(Error_MalformedURLIO,null,GetError());
            return  Error_MalformedURLIO;
        }

        catch (Exception e)
        {
            SetError("SendData " + e.getLocalizedMessage());
            Log.e("JavaSong",GetError());
            if(mProgress != null) {mProgress.OnEnd();}
            if(mRequestEvent != null) mRequestEvent.OnConnected(Error_MalformedURLOther,null,GetError());
            return  Error_MalformedURLOther;
        }
        return nResponse;
    }


    public boolean Receive()
    {
        InputStream input = null;
        try {
            input = mConn.getInputStream();
            byte[] buf = new byte[4096];
            long lPos = 0;
            for (int nChunk = input.read(buf); nChunk!=-1; nChunk = input.read(buf))
            {
                byte[] data = new byte[nChunk];
                System.arraycopy(buf,0,data,0,nChunk);
                if(mRequestEvent != null) mRequestEvent.OnReceive(data);
                if(mProgress != null) {mProgress.OnPosition(lPos);lPos+=nChunk;}
            }
        }
        catch (Exception e)
        {
            SetError("ReceiveData " + e.getLocalizedMessage());
            Log.e("JavaSong",GetError());
            if(mProgress != null) {mProgress.OnEnd();}
            if(mRequestEvent != null) mRequestEvent.OnCompleted(GetError());
            return  false;
        }
        if(mProgress != null) {mProgress.OnEnd();}
        if(mRequestEvent != null) mRequestEvent.OnCompleted(null);
        return true;
    }

    public byte[] ReceiveData(InputStream input)
    {
        ByteArrayOutputStream ouput = new ByteArrayOutputStream();
        try {
            byte[] buf = new byte[4096];
            for (int nChunk = input.read(buf); nChunk!=-1; nChunk = input.read(buf))
            {
                ouput.write(buf, 0, nChunk);
                if(mRequestEvent != null) mRequestEvent.OnReceive(buf);
            }
        }
        catch (Exception e)
        {
            SetError("ReceiveData " + e.getLocalizedMessage());
            Log.e("JavaSong",GetError());
            if(mRequestEvent != null) mRequestEvent.OnCompleted(GetError());
            return  null;
        }

        if(mRequestEvent != null) mRequestEvent.OnCompleted(null);
        return ouput.toByteArray();
    }
}
