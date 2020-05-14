package co.kr.ssdroidlib.comm;

/**
 * Created By hhsong
 */
public class SParamRunnable implements Runnable {
    Object mParam;
    public SParamRunnable(Object Param)
    {
        mParam = Param;
    }

    @Override
    public void run() {
        run(mParam);
    }

    public void run(Object Param)
    {
    }
}
