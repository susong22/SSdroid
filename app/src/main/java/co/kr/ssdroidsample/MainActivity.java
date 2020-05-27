package co.kr.ssdroidsample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import co.kr.ssdroidlib.http.SHttpRequest;
import co.kr.ssdroidlib.page.SViewPager;
import co.kr.ssdroidsample.R;
import co.kr.ssdroidsample.shttp.SHttpActivity;
import co.kr.ssdroidsample.sviewpager.SViewPagerActivity;
import co.kr.ssdroidsample.swebview.SWebViewActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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

    static final int ID_ViewPager = 1;
    static final int ID_WebView = 2;
    static final int ID_HttpRequest = 3;

    static  public class SampleData
    {
        public  SampleData(int id,String sT,String sD,Class<?> activity) { Title = sT;Desc = sD; ID = id;Activity = activity;}
        public int ID;
        public String Title;
        public String Desc;
        public Class<?> Activity;
    }

    Context mContext;
    ArrayList<SampleData> mLstSampleData = new ArrayList<SampleData>();

    public void AddData()
    {
        mLstSampleData.add(new SampleData(ID_ViewPager,"SViewPager","This is a sample to switch page.",SViewPagerActivity.class));
        mLstSampleData.add(new SampleData(ID_WebView,"SWebView","This is a sample of webview page.",SWebViewActivity.class));
        mLstSampleData.add(new SampleData(ID_HttpRequest,"SHttpRequest","This is a sample to request http.",SHttpActivity.class));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        checkPermission();
        AddData();
        SetListView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        try
        {
            switch (requestCode) {
                case MY_PERMISSION:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.d("JavaSongs", "onRequestPermissionsResult  GPS Permission");
                    }
                    else
                    {
                        Log.d("JavaSongs", "onRequestPermissionsResult GPS Permission always deny");
                    }
                    break;
            }
        }
        catch(Exception e)
        {
            Log.e("JavaSongs", "onRequestPermissionsResult " + e.getLocalizedMessage());
        }
    }

    void SetListView()
    {
        ListView view = findViewById(R.id.lstSample);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                SampleData data = mLstSampleData.get(position);
                Intent intent = new Intent(MainActivity.this,data.Activity);
                startActivity(intent);
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

    static final int MY_PERMISSION = 101;
    private boolean checkPermission() {
        try
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                //Location 정보를 추가한다.
                ActivityCompat.requestPermissions(this,new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
//								,Manifest.permission.BROADCAST_SMS
//								,Manifest.permission.BROADCAST_WAP_PUSH
                        },
                        MY_PERMISSION);

            }
            else return  true;
        }
        catch(Exception e){
            e.printStackTrace();
            Log.e("JavaSongs",e.getLocalizedMessage());
        }
        return false;
    }
}
