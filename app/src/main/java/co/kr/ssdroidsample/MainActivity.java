package co.kr.ssdroidsample;

import androidx.appcompat.app.AppCompatActivity;
import co.kr.ssdroidlib.page.SViewPager;
import co.kr.ssdroidsample.R;
import android.os.Bundle;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SViewPager viewpager =  findViewById(R.id.vwPage);
        viewpager.StartPage(this,"Main",new Fragment1(),null);
    }
}
