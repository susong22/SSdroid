package co.kr.ssdroidlib.page;

public interface IViewPagerEvent {
    public void OnActiveView(boolean bForward,Object Param);
    public void OnDeactiveView();
}
