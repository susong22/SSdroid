package co.kr.ssdroidsample.sviewpager;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import co.kr.ssdroidlib.page.ISFragment;
import co.kr.ssdroidlib.page.SViewPager;
import co.kr.ssdroidsample.R;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class Fragment1 extends Fragment implements ISFragment {

    TextView mtxtView;
    public Fragment1() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("JavaSong","1 - onCreateView");
        final  SViewPager viewPager = (SViewPager)container;


        View view =  inflater.inflate(R.layout.fragment_1, container, false);
        mtxtView = view.findViewById(R.id.txtView);


        //파라미터를 받는 샘플이다...
        Object Param = viewPager.GetCurrentParam();
        mtxtView.setText((String)Param);

        Button button = view.findViewById(R.id.btnNext);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.goPage(new Fragment2(),null,true);
            }
        });

        final Activity Main = this.getActivity();
        button = view.findViewById(R.id.btnBack1);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Main.finish();;
            }
        });
        return  view;
    }


    @Override
    public void OnBackActive(Object Param) {
        if(Param != null) mtxtView.setText((String)Param);
    }


    @Override
    public void onDestroyView() {
        Log.d("JavaSong","Delete - Fragment1");
        super.onDestroyView();
    }
}
