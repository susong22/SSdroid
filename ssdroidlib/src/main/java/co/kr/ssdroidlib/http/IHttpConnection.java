package co.kr.ssdroidlib.http;

import java.util.List;
import java.util.Map;

public interface IHttpConnection {
    void OnConnected(int Code, Map<String, List<String>> Header, String sError);
    void OnReceive(byte[] btData);
    void OnCompleted(String sError);
}
