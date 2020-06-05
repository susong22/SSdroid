package co.kr.ssdroidlib.http;

public interface IHttpProgress {
    public void OnTitle(long id,String sTitle);
    public void OnTotal(long id,long nSize);
    public void OnPosition(long id,long nPos);
    public void OnEnd(long id);
}
