package co.kr.ssdroidlib.page;

import android.util.Log;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import co.kr.ssdroidlib.comm.SUtils;

/**
 * Created By hhsong 2020.05.12
 */
public class SPagerAdapter extends FragmentStatePagerAdapter {


    public  class  SPagerAdapterData
    {
        public Fragment fragment;
        public String   id;
        public Object   param;
    }

    private ArrayList<SPagerAdapterData> mList = new ArrayList<SPagerAdapterData>();

    public SPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }
    public SPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mList.get(position).fragment;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    public void AddFragment(Fragment frg)
    {
        AddFragment(String.format("%d",SUtils.NewID()),frg,null);
        notifyDataSetChanged();
    }

    public void AddFragment(Fragment frg,Object Param)
    {
        AddFragment(String.format("%d",SUtils.NewID()), frg, Param);
        notifyDataSetChanged();
    }

    public SPagerAdapterData Find(String ID)
    {
        for (SPagerAdapterData frg:mList)
        {
            if(frg.id.compareTo(ID) == 0) return frg;
        }
        return null;
    }

    public int FindPostion(String ID)
    {
        for (int i = 0;  i < mList.size(); i++)
        {
            if(mList.get(i).id.compareTo(ID) == 0)
                return i;
        }
        return -1;
    }

    public SPagerAdapterData GetAt(int position)
    {
        return mList.get(position);
    }
    public void AddFragment(String ID,Fragment frg,Object Param)
    {
        SPagerAdapterData data = new SPagerAdapterData();
        data.id = ID;
        data.param = Param;
        data.fragment = frg;
        mList.add(data);
        notifyDataSetChanged();
    }

    public void RemoveFragment(int position)
    {
        mList.remove(position);
        notifyDataSetChanged();
    }

    public void RemoveFragmentFrom(int toposition)
    {
        for (int i = mList.size() - 1;  i > toposition; i--)
        {
            mList.remove(i);
        }
        notifyDataSetChanged();
    }

    // This is called when notifyDataSetChanged() is called (Remove 이 적용되려면 이런 문구가 되어야 한다.)
    @Override
    public int getItemPosition(Object object) {
        // refresh all fragments when data set changed
        return PagerAdapter.POSITION_NONE;
    }
}
