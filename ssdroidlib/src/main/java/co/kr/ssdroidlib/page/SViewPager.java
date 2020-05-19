package co.kr.ssdroidlib.page;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import co.kr.ssdroidlib.comm.SParamRunnable;

/**
 * Created By hhsong 2020.05.12
 */
public class SViewPager extends ViewPager {

    /**
     * Control SWIPE
     */
    static public enum SwipeDirection {
        all, left, right, none ;
    }
    private float initialXValue;
    private SwipeDirection direction;

    public SViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Settings();
    }

    public SViewPager(@NonNull Context context) {
        super(context);
        Settings();
    }

    public void Settings()
    {
        setOffscreenPageLimit(99999);
        setAllowedSwipeDirection(SViewPager.SwipeDirection.left);
    }


    public void setAllowedSwipeDirection(SwipeDirection direction) {
        this.direction = direction;
    }

    /**
     * You need to set the first fragment.
     * @param activity
     * @param newPage
     * @param Param
     */
    public void StartPage(FragmentActivity activity, Fragment newPage, Object Param)
    {
        StartPage(activity,null,newPage,Param);
    }

    public void StartPage(FragmentActivity activity,String ID, Fragment newPage, Object Param)
    {
        SPagerAdapter adapter = new SPagerAdapter(activity.getSupportFragmentManager());
        adapter.AddFragment(ID,newPage,Param);
        setAdapter(adapter);
    }

    /**
     * Get the current parameter.
     * @return
     */
    public Object GetCurrentParam()
    {
        SPagerAdapter apdater = (SPagerAdapter)this.getAdapter();
        return apdater.GetAt(getCurrentItem()).param;
    }

    /**
     * Move page without ID
     * @param newPage
     * @param Param
     * @param bAnimation
     */
    public void goPage(Fragment newPage, Object Param, boolean bAnimation)
    {
        SPagerAdapter apdater = (SPagerAdapter)this.getAdapter();
        apdater.AddFragment(newPage,Param);
        if(apdater.getCount() - 1 == getCurrentItem())
        {
            Log.e("JavaSong","Can not change the page, because is in Last of Page...");
            return;
        }
        setCurrentItem(getCurrentItem() + 1,bAnimation);
    }

    /**
     * Move page
     * @param ID
     * @param newPage
     * @param Param
     * @param bAnimation
     */
    public void goPage(String ID,Fragment newPage, Object Param, boolean bAnimation)
    {
        SPagerAdapter apdater = (SPagerAdapter)this.getAdapter();
        apdater.AddFragment(ID,newPage,Param);
        if(apdater.getCount() - 1 == getCurrentItem())
        {
            Log.e("JavaSong","Can not change the page, because is in Last of Page...");
            return;
        }
        setCurrentItem(getCurrentItem() + 1,bAnimation);
    }

    /**
     * Go to the previous page
     * @param Param
     * @param bAnimation
     */
    public void BackPage(Object Param,boolean bAnimation)
    {
        BackPage(null,Param,bAnimation);
    }

    public boolean HasBackPage() {
        SPagerAdapter apdater = (SPagerAdapter)this.getAdapter();
        if(apdater.getCount() > 1) return true;
        return false;
    }

    /**
     * Go to the ID page.
     * @param ID
     * @param Param
     * @param bAnimation
     */
    public void BackPage(String ID,Object Param,boolean bAnimation)
    {
        final SPagerAdapter apdater = (SPagerAdapter)this.getAdapter();
        if(getCurrentItem() == 0)
        {
            Log.e("JavaSong","Can not change the page, because is in zero of Page...");
            return;
        }

        final int position = getCurrentItem();
        if(ID == null) {
            int backposition = getCurrentItem() - 1;
            setCurrentItem(backposition, bAnimation);
            if(apdater.GetAt(backposition).fragment instanceof ISFragment)
                ((ISFragment)apdater.GetAt(backposition).fragment).OnBackActive(Param);
            if (bAnimation) {
                new Handler().postDelayed(new SParamRunnable(position) {
                    @Override
                    public void run(Object Param) {
                        apdater.RemoveFragment((int) Param);
                    }
                }, 500);
            } else
                apdater.RemoveFragment(position);
        }
        else
        {
            int backposition = apdater.FindPostion(ID);
            if(backposition == -1)
            {
                Log.e("JavaSong","Can not change the page, because is not found id..."+ID);
                return;
            }
            setCurrentItem(backposition, bAnimation);
            if(apdater.GetAt(backposition).fragment instanceof ISFragment)
                ((ISFragment)apdater.GetAt(backposition).fragment).OnBackActive(Param);
            if (bAnimation) {
                new Handler().postDelayed(new SParamRunnable(backposition) {
                    @Override
                    public void run(Object Param) {
                        apdater.RemoveFragmentFrom((int) Param);
                    }
                }, 500);
            } else
                apdater.RemoveFragmentFrom(backposition);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.IsSwipeAllowed(event)) {
            return super.onTouchEvent(event);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.IsSwipeAllowed(event)) {
            return super.onInterceptTouchEvent(event);
        }
        return false;
    }

    private boolean IsSwipeAllowed(MotionEvent event) {
        if(this.direction == SwipeDirection.all) return true;

        if(direction == SwipeDirection.none )//disable any swipe
            return false;

        if(event.getAction()== MotionEvent.ACTION_DOWN) {
            initialXValue = event.getX();
            return true;
        }

        if(event.getAction()==MotionEvent.ACTION_MOVE) {
            try {
                float diffX = event.getX() - initialXValue;
                if (diffX > 0 && direction == SwipeDirection.right ) {
                    return false;
                }else if (diffX < 0 && direction == SwipeDirection.left ) {
                    // swipe from right to left detected
                    return false;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return true;
    }
}
