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
import androidx.fragment.app.FragmentStatePagerAdapter;
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
        setOffscreenPageLimit(0);
        setAllowedSwipeDirection(SViewPager.SwipeDirection.left);
    }


    public void setAllowedSwipeDirection(SwipeDirection direction) {
        this.direction = direction;
    }


    public void StartPage(FragmentActivity activity, Fragment newPage, Object Param)
    {
        SPagerAdapter adapter = new SPagerAdapter(activity.getSupportFragmentManager());
        adapter.AddFragment(newPage);
        setAdapter(adapter);
    }

    public void NextPage(Fragment newPage, Object Param, boolean bAnimation)
    {
        SPagerAdapter apdater = (SPagerAdapter)this.getAdapter();
        apdater.AddFragment(newPage);
        if(apdater.getCount() - 1 == getCurrentItem())
        {
            Log.e("JavaSong","Can not change the page, because is in Last of Page...");
            return;
        }
        setCurrentItem(getCurrentItem() + 1,bAnimation);
    }

    //    public void NextPage(Object Param,boolean bAnimation)
//    {
//        PagerAdapter apdater = this.getAdapter();
//        if(apdater.getCount() - 1 == getCurrentItem())
//        {
//            Log.e("JavaSong","Can not change the page, because is in Last of Page...");
//            return;
//        }
//        setCurrentItem(getCurrentItem() + 1,bAnimation);
//    }
//
    public void BackPage(Object Param,boolean bAnimation)
    {
        final SPagerAdapter apdater = (SPagerAdapter)this.getAdapter();
        if(getCurrentItem() == 0)
        {
            Log.e("JavaSong","Can not change the page, because is in zero of Page...");
            return;
        }
        final int position = getCurrentItem();
        setCurrentItem(getCurrentItem() - 1,bAnimation);
        if(bAnimation)
        {
            new Handler().postDelayed(new SParamRunnable(position) {
                @Override
                public void run(Object Param) {
                    apdater.RemoveFragment((int)position);
                }
            }, 500);
        }
        else
            apdater.RemoveFragment(position);
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
