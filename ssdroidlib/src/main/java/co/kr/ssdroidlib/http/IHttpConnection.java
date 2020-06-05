package co.kr.ssdroidlib.http;

import java.util.List;
import java.util.Map;

public interface IHttpConnection {
    void OnConnected(long ID,int Code, Map<String, List<String>> Header, String sError);
    void OnReceive(long ID,byte[] btData);
    void OnCompleted(long ID,String sError);
}
