package co.kr.ssdroidsample;

import androidx.appcompat.app.AppCompatActivity;
import co.kr.ssdroidlib.page.SViewPager;
import co.kr.ssdroidsample.R;
import co.kr.ssdroidsample.sviewpager.SViewPagerActivity;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static int ID_ViewPager = 1;

    static  public class SampleData
    {
        public  SampleData(int id,String sT,String sD) { Title = sT;Desc = sD; ID = id;}
        public int ID;
        public String Title;
        public String Desc;
    }

    Context mContext;
    ArrayList<SampleData> mLstSampleData = new ArrayList<SampleData>();

    public void AddData()
    {
        mLstSampleData.add(new SampleData(ID_ViewPager,"SViewPager","This is a sample to switch page."));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        AddData();
        SetListView();
    }

    void SetListView()
    {
        ListView view = findViewById(R.id.lstSample);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SampleData data = mLstSampleData.get(i);
                if(data.ID == ID_ViewPager)
                {
                    Intent intent = new Intent(MainActivity.this, SViewPagerActivity.class);
                    startActivity(intent);
                }
            }
        });
        view.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() { return mLstSampleData.size(); }
            @Override
            public Object getItem(int i) { return mLstSampleData.get(i); }
            @Override
            public long getItemId(int i) { return 0; }
            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                if(view == null)  view = ((LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.row_samples,viewGroup,false);
                SampleData data = mLstSampleData.get(i);
                TextView title = view.findViewById(R.id.txtTitle);
                title.setText(data.Title);
                TextView desc = view.findViewById(R.id.txtDesc);
                desc.setText(data.Desc);
                return view;
            }
        });
    }
}
