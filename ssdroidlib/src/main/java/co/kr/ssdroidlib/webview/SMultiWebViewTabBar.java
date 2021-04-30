package co.kr.ssdroidlib.webview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Hashtable;
import java.util.Map;

import co.kr.ssdroidlib.comm.SUtils;

public class SMultiWebViewTabBar extends HorizontalScrollView {

    public interface SMultiWebViewTabBarEvent{
        void OnClicked(long id);
        void OnRemoved(long id);
    }
    class TabData {
        long ID;
        LinearLayout TabButtonCon;
        TextView Title;
        LinearLayout CloseButtonCon;
        boolean Focus;
    }

    public LinearLayout          mTabBarContent;
    public Context mContext;
    public SMultiWebViewTabBarEvent mEvent;
    public Map<Long,TabData> mapTab = new Hashtable<Long,TabData>();


    public SMultiWebViewTabBar(Context context) {
        super(context);
        Init(context);
    }

    public SMultiWebViewTabBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context);
    }

    public void SetEvent(SMultiWebViewTabBarEvent event)
    {
        mEvent = event;
    }

    public void Init(Context context)
    {
        mContext = context;
        HorizontalScrollView.LayoutParams TabBarParam = new HorizontalScrollView.LayoutParams(HorizontalScrollView.LayoutParams.MATCH_PARENT, SUtils.ToDiplay(mContext,30));
        setLayoutParams(TabBarParam);

        mTabBarContent = new LinearLayout(context);
        mTabBarContent.setLayoutParams(new LinearLayout.LayoutParams(SUtils.ToDiplay(mContext,0), LinearLayout.LayoutParams.MATCH_PARENT));
        mTabBarContent.setOrientation(LinearLayout.HORIZONTAL);
        addView(mTabBarContent);
    }

    public void SetFocus(View TabCon, TextView Title,boolean bFocus)
    {
        if(bFocus) {
            TabCon.setBackgroundColor(Color.LTGRAY);
            Title.setTextColor(Color.BLACK);
            Title.setTypeface(null, Typeface.BOLD);
            //Title.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
        }
        else {
            TabCon.setBackgroundColor(Color.GRAY);
            Title.setTextColor(Color.WHITE);
            Title.setTypeface(null, Typeface.NORMAL);
            //Title.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
        }
    }

    public long GetTabSize()
    {
        return mapTab.size();
    }
    public boolean IsFirstTab(long ID)
    {
        View v = mTabBarContent.getChildAt(0);
        TabData tab = mapTab.get(ID);
        if(tab != null && tab.TabButtonCon == v) return true;
        return false;
    }

    public long GetFocus()
    {
        for(Map.Entry<Long,TabData> entry : mapTab.entrySet()) {
            TabData value = entry.getValue();
            if(value.Focus) return entry.getKey();
        }
        return 0;
    }

    public void SetFocus(long ID)
    {
        for(Map.Entry<Long,TabData> entry : mapTab.entrySet()) {
            TabData value = entry.getValue();
            if(entry.getKey() == ID) {
                SetFocus(value.TabButtonCon, value.Title, true);
                value.Focus = true;

            }
            else {
                SetFocus(value.TabButtonCon, value.Title, false);
                value.Focus = false;
            }
        }
    }

    public void SetTitle(long ID,String sTitle)
    {
        TabData tabData = mapTab.get(ID);
        if(tabData != null)
        {
            tabData.Title.setText(sTitle);
        }
    }

    public void RemoveTab(long ID)
    {
        long FocusID = -1;

        TabData tabData = mapTab.get(ID);
        if(tabData != null)
        {
            if(tabData.Focus) {
                for (Map.Entry<Long, TabData> entry : mapTab.entrySet()) {
                    TabData value = entry.getValue();
                    if (entry.getKey() != ID) {
                        FocusID = entry.getKey();
                        break;
                    }
                }
            }
            mapTab.remove(ID);
            mTabBarContent.removeView(tabData.TabButtonCon);
            if(FocusID != -1)
                mEvent.OnClicked(FocusID);
        }
    }

    public void AddTab(long ID,String sTitle,boolean bCloseable,boolean bFocus)
    {

        //------------------- TabButtonCon
        //|X|   타이틀
        //-------------------
        LinearLayout TabButtonCon = new LinearLayout(mContext);
        TabButtonCon.setOrientation(LinearLayout.HORIZONTAL);
        TabButtonCon.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));


        //Close Button
        LinearLayout CloseButtonCon = new LinearLayout(mContext);
        CloseButtonCon.setOrientation(LinearLayout.HORIZONTAL);
        CloseButtonCon.setGravity(Gravity.CENTER);
        CloseButtonCon.setLayoutParams(new LinearLayout.LayoutParams(SUtils.ToDiplay(mContext,25),ViewGroup.LayoutParams.MATCH_PARENT));
        if(!bCloseable) CloseButtonCon.setVisibility(View.GONE);

        View CloseButton = new View(mContext);
        CloseButton.setBackgroundResource(android.R.drawable.ic_menu_close_clear_cancel);
        CloseButton.setLayoutParams(new LinearLayout.LayoutParams(SUtils.ToDiplay(mContext,14),SUtils.ToDiplay(mContext,14)));
        CloseButtonCon.addView(CloseButton);

        TabButtonCon.addView(CloseButtonCon);
        //Close End

        //Title
        TextView Title = new TextView(mContext);
        Title.setText(sTitle);
        Title.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        Title.setSingleLine();
        Title.setPadding(SUtils.ToDiplay(mContext,1),0,SUtils.ToDiplay(mContext,1),0);

        Title.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
        Title.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams TxtParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT);
        TxtParams.setMargins(SUtils.ToDiplay(mContext,1),0,SUtils.ToDiplay(mContext,10),0);
        Title.setLayoutParams(TxtParams);
        TabButtonCon.addView(Title);
        //Title End
        mTabBarContent.addView(TabButtonCon);


        TabData newTab = new TabData();
        newTab.ID = ID;
        newTab.CloseButtonCon = CloseButtonCon;
        newTab.Title = Title;
        newTab.TabButtonCon = TabButtonCon;
        mapTab.put(ID,newTab);

        SetFocus(ID);

        CloseButtonCon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                for(Map.Entry<Long,TabData> entry : mapTab.entrySet()) {
                    TabData value = entry.getValue();
                    if(value.CloseButtonCon == view)
                    {
                        RemoveTab(entry.getKey());
                        if(mEvent != null) mEvent.OnRemoved(entry.getKey());
                        break;
                    }
                }
            }
        });

        TabButtonCon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                for(Map.Entry<Long,TabData> entry : mapTab.entrySet()) {
                    TabData value = entry.getValue();
                    if(value.TabButtonCon == view)
                    {
                        SetFocus(entry.getKey());
                        if(mEvent != null) mEvent.OnClicked(entry.getKey());
                        break;
                    }
                }
            }
        });
    }
}
