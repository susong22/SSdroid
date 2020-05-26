package co.kr.ssdroidsample.sviewpager;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import co.kr.ssdroidlib.page.SViewPager;
import co.kr.ssdroidsample.R;

public class SViewPagerActivity extends AppCompatActivity {
    SViewPager mViewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sviewpager);
        setTitle("SViewPager");
        mViewpager =  findViewById(R.id.vwPage);

        //This is set when using the first fragment at the start of the page.
        mViewpager.StartPage(this,"Main",new Fragment1(),"The parameters were set at startup.");
    }

    @Override
    public void onBackPressed() {
        //뷰페이져에 백할 페이지가 존재하면 백을 해준다...
        if(mViewpager.HasBackPage()) {
            mViewpager.BackPage(null,true);
            return;
        }
        super.onBackPressed();
    }

}
