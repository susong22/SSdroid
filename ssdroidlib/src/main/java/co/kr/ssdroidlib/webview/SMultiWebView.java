package co.kr.ssdroidlib.webview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import org.w3c.dom.Text;
import co.kr.sdroidlib.R;

public class SMultiWebView extends LinearLayout {
    Context mContext;
    HorizontalScrollView  mTabBar;
    LinearLayout          mTabBarContent;

    public SMultiWebView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Init(context);
    }

    void Init(Context context)
    {
        mContext = context;
        this.setOrientation(LinearLayout.VERTICAL);

        mTabBar = new HorizontalScrollView(context);
        HorizontalScrollView.LayoutParams TabBarParam = new HorizontalScrollView.LayoutParams(HorizontalScrollView.LayoutParams.MATCH_PARENT, ToDiplay(30));
        mTabBar.setLayoutParams(TabBarParam);

        mTabBarContent = new LinearLayout(context);
        LinearLayout.LayoutParams pl = new LinearLayout.LayoutParams(ToDiplay(0), LayoutParams.MATCH_PARENT);
        mTabBarContent.setLayoutParams(pl);
        mTabBarContent.setOrientation(LinearLayout.HORIZONTAL);
        mTabBarContent.setBackgroundColor(Color.WHITE);
        mTabBar.addView(mTabBarContent);

        addView(mTabBar);

        //mTabBar.setBackgroundColor(Color.RED);
        AddTab("울랄라");
        AddTab("울랄라 s asadkfasd");
    }

    void AddTab(String sTitle)
    {

        TextView textView = new TextView(mContext);
        textView.setText(sTitle);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setBackgroundColor(Color.DKGRAY);
        textView.setTextColor(Color.WHITE);
        //textView.setBackgroundResource(android.R.drawable.ic_menu_close_clear_cancel);

        textView.setSingleLine();
        textView.setPadding(ToDiplay(4),0,ToDiplay(4),0);

        textView.setTextSize(18);
        textView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams TxtParams = new LinearLayout.LayoutParams(ToDiplay(100),ViewGroup.LayoutParams.MATCH_PARENT);
        TxtParams.setMargins(ToDiplay(2),0,0,0);
        textView.setLayoutParams(TxtParams);
        mTabBarContent.addView(textView);
    }

    int ToDiplay(int n) { return (int)(n* mContext.getResources().getDisplayMetrics().density);}
}
