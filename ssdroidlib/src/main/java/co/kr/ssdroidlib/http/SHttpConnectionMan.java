package co.kr.ssdroidlib.http;

import android.util.Log;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import co.kr.ssdroidlib.comm.SUtils;

public class SHttpConnectionMan {

    static class HttpResData {
        Thread Thread;
        SHttpConnection Request;
        long ID;
        byte[] Post;
        Map<String,String> Header;
        String UploadFilePath;
    }

    static public long RequestHttp(String sURL, byte[] btPost, Map<String,String> Header, IHttpConnection RequestEvent)
    {
        return RequestHttp(sURL,btPost,Header,RequestEvent);
    }

    static public long RequestHttp(String sURL, byte[] btPost, Map<String,String> Header, IHttpConnection RequestEvent, IHttpProgress Progress)
    {
        long lID = SUtils.NewID();
        HttpResData newHttp = new HttpResData();
        newHttp.ID = lID;
        newHttp.Post = btPost;
        newHttp.Header = Header;
        newHttp.Request = new SHttpConnection(sURL);
        newHttp.Request.SetHttpRequestEvent(RequestEvent);
        newHttp.Request.SetProgress(Progress);
        Put(lID,newHttp);

        newHttp.Thread = new ThreadParam(newHttp){
            @Override
            public void run() {
                HttpResData newHttp = (HttpResData)mObject;
                try {
                    if(newHttp.Request.Connection() == false) { Remove(newHttp.ID);return;}
                    if(newHttp.Request.HttpSetup(newHttp.Post!=null?newHttp.Post.length:0,newHttp.Header) == false) { Remove(newHttp.ID);return;}
                    if(newHttp.Request.SendData(newHttp.Post) == false) { Remove(newHttp.ID);return;}
                    if(newHttp.Request.Response() != 200) { Remove(newHttp.ID);return;}
                    if(newHttp.Request.Receive() == false) { Remove(newHttp.ID);return;}
                    Remove(newHttp.ID);
                }
                catch (Exception e)
                {
                    Remove(newHttp.ID);
                    Log.e("JavaSong","RequestHttp " + e.getLocalizedMessage());
                }
            }
        };
        newHttp.Thread.start();
        return lID;
    }


    static public long RequestHttp(String sURL, String sUploadFilePath, Map<String,String> Header,  IHttpConnection RequestEvent, IHttpProgress Progress)
    {
        long lID = SUtils.NewID();
        HttpResData newHttp = new HttpResData();
        newHttp.ID = lID;
        newHttp.Header = Header;
        newHttp.UploadFilePath = sUploadFilePath;
        newHttp.Request = new SHttpConnection(sURL);
        newHttp.Request.SetHttpRequestEvent(RequestEvent);
        newHttp.Request.SetProgress(Progress);
        Put(lID,newHttp);

        newHttp.Thread = new ThreadParam(newHttp){
            public void run() {
                HttpResData newHttp = (HttpResData)mObject;
                try {
                    File myFile = new File(newHttp.UploadFilePath);
                    long lSize = myFile.length();
                    if(newHttp.Request.Connection() == false) return;
                    if(newHttp.Request.HttpSetup(lSize,newHttp.Header) == false) return;
                    if(newHttp.Request.SendFile(newHttp.UploadFilePath)) return;
                    if(newHttp.Request.Response() != 200) return;
                    if(newHttp.Request.Receive()) return;
                    Remove(newHttp.ID);
                }
                catch (Exception e)
                {
                    Remove(newHttp.ID);
                    Log.e("JavaSong","RequestHttp " + e.getLocalizedMessage());
                }
            }
        };
        newHttp.Thread.start();
        return lID;
    }



    static void CancelRequestHttp(long id)
    {
        HttpResData http = Get(id);
        http.Request.Disconnection();
        Remove(id);;
    }

    static Map<Long,HttpResData> mapData = new HashMap<Long,HttpResData>();
    static void Put(long id,HttpResData res) {
        synchronized (mapData)
        {
            mapData.put(id,res);
        }
    }
    static HttpResData Get(long id) {
        HttpResData res;
        synchronized (mapData)
        {
            res = mapData.get(id);
        }
        return res;
    }

    static void Remove(long id) {

        synchronized (mapData)
        {
            mapData.remove(id);
        }
    }

    static class ThreadParam extends Thread
    {
        Object mObject;
        public ThreadParam(Object object)
        {
            mObject = object;
        }
    }
}
