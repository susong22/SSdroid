package co.kr.ssdroidlib.http;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;


public class SHttpRequest {

    public interface IDataListener {
        void OnCompleted(long ID,long Code,byte[] Data,Map<String, List<String>> Header,String sError);
    }

    IDataListener mDataListener;
    public void SetDataListener(IDataListener DataListener) { mDataListener = DataListener;}
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            long ID = msg.what;
            long code = data.getLong("code");
            byte[] rcvdata = data.getByteArray("rcvdata");
            String filepath = data.getString("filepath");
            String error = (String)data.get("error");
            Map<String, List<String>> header = (Map<String, List<String>>)msg.obj;
            if(mDataListener != null)
            {
                byte[] sendData = filepath != null?filepath.getBytes():rcvdata;
                mDataListener.OnCompleted(ID,code,sendData,header,error);
            }
        }
    };

    public void RequestHttp(int ID,String sURL, byte[] btPost, Map<String,String> Header) {
        RequestHttp(ID,sURL, btPost,null, Header,null);
    }

    public void DownloadFile(int ID,String sURL, String sDownloadDir, Map<String,String> Header,IHttpProgress Progress) {
        RequestHttp(ID,sURL, null,sDownloadDir, Header,Progress);
    }

    public void UploadFile(int ID,String sURL, String sFilePath, Map<String,String> Header,IHttpProgress Progress) {
        SHttpConnectionMan.RequestHttp(sURL, sFilePath, Header, new SIHttpConnection(ID,null),Progress);
    }

    private void RequestHttp(int ID,String sURL, byte[] btPost, String sDownloadDir, Map<String,String> Header,IHttpProgress Progress)
    {
        SHttpConnectionMan.RequestHttp(sURL, btPost, Header, new SIHttpConnection(ID,sDownloadDir),Progress);
    }

    class SIHttpConnection implements IHttpConnection
    {
        protected  int   mID;
        protected  String   mDownloadDir;

        ByteArrayOutputStream mBuffer;
        OutputStream mFile;
        Map<String, List<String>> mHeader;
        int mCode;
        String mFilePath = null;

        public SIHttpConnection(int id,String DownloadDir)
        {
            mID = id;
            mDownloadDir = DownloadDir;
        }
        @Override
        public void OnConnected(int Code, Map<String, List<String>> Header, String sError) {
            mHeader = Header;
            mCode = Code;

            if(sError != null) {
                Message message = mHandler.obtainMessage();
                message.what = mID;
                Bundle b = new Bundle();
                b.putLong("code", Code);
                b.putString("error", sError);
                message.setData(b);
                mHandler.sendMessage(message);
            }
        }

        @Override
        public void OnReceive(byte[] btData) {
            try {
                if(mDownloadDir != null)
                {
                    if(mFile == null)
                    {
                        String sFileName = null;
                        for (Map.Entry<String, List<String>> entries : mHeader.entrySet()) {
                            if (entries.getKey().compareToIgnoreCase("Content-Disposition") == 0) {
                                String sContentDispos = "";
                                for (String value : entries.getValue()) {
                                    sContentDispos += value + ",";
                                }

                                if (sContentDispos.contains("attatchment;")) {
                                    int nIndex = sContentDispos.lastIndexOf('=');
                                    if (nIndex != -1) {
                                        sFileName = sContentDispos.substring(nIndex + 1);
                                        sFileName = sFileName.trim();
                                    } else
                                        sFileName = sContentDispos.replaceFirst("(?i)^.*filename = \"([^\"]+)\".*$", "$1");
                                }
                            }
                            if (sFileName == null) {
                                sFileName = Integer.toString(mID);
                                Log.d("JavaSong", "Not Found Filename");
                            }
                            mFile = new FileOutputStream(mDownloadDir + "/" + sFileName);
                        }
                        mFile.write(btData);
                    }
                }
                else {
                    if (mBuffer == null)
                        mBuffer = new ByteArrayOutputStream();
                    mBuffer.write(btData);
                }
            }
            catch (Exception e)
            {
                Log.e("JavaSong","OnReceive "+e.getLocalizedMessage());
                if(mFile != null) {
                    try {
                        mFile.close();
                    } catch (IOException ex) {
                        Log.e("JavaSong","OnReceive Close"+e.getLocalizedMessage());
                    }
                }
            }
        }

        @Override
        public void OnCompleted(String sError) {
            if(mFile != null) {
                try {
                    mFile.close();
                } catch (IOException ex) {
                    Log.e("JavaSong","OnReceive Close"+ex.getLocalizedMessage());
                }
            }

            Message message = mHandler.obtainMessage();
            message.what = mID;
            message.obj = mHeader;
            Bundle b = new Bundle();
            b.putLong("code", mCode);
            if(mCode != 200)
            {
                b.putString("error", sError);
            }
            else
            {
                if(mDownloadDir != null)
                    b.putString("filepath",mFilePath);
                else
                    b.putByteArray("rcvdata",mBuffer.toByteArray());
            }
            message.setData(b);
            mHandler.sendMessage(message);
        }
    }

}
