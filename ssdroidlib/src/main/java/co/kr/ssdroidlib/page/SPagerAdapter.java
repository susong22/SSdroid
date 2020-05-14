package co.kr.ssdroidlib.page;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

/**
 * Created By hhsong 2020.05.12
 */
public class SPagerAdapter extends FragmentStatePagerAdapter {
    public ArrayList<Fragment> mList = new ArrayList<Fragment>();

    public SPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }
    public SPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    public void AddFragment(Fragment frg)
    {
        mList.add(frg);
        notifyDataSetChanged();
    }

    public void RemoveFragment(int position)
    {
        mList.remove(position);
        notifyDataSetChanged();
    }

    // This is called when notifyDataSetChanged() is called (Remove 이 적용되려면 이런 문구가 되어야 한다.)
    @Override
    public int getItemPosition(Object object) {
        // refresh all fragments when data set changed
        return PagerAdapter.POSITION_NONE;
    }
}
