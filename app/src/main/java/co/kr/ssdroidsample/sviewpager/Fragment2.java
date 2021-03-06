package co.kr.ssdroidsample.sviewpager;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import co.kr.ssdroidlib.page.SViewPager;
import co.kr.ssdroidsample.R;



public class Fragment2 extends Fragment {

    public Fragment2() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("JavaSong","2 - onCreateView");
        final SViewPager viewPager = (SViewPager)container;

        View view =  inflater.inflate(R.layout.fragment_2, container, false);
        Button button = view.findViewById(R.id.btnNext2);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.goPage(new Fragment3(),null,true);
            }
        });

        button = view.findViewById(R.id.btnBack2);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.BackPage("Fragment2에서 파라미터를 넣어줌",true);
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        Log.d("JavaSong","Delete - Fragment2");
        super.onDestroyView();
    }
}
