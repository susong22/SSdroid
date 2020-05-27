package co.kr.ssdroidlib.http;

public interface IHttpProgress {
    public void OnTitle(String sTitle);
    public void OnTotal(long nSize);
    public void OnPosition(long nPos);
    public void OnEnd();
}
