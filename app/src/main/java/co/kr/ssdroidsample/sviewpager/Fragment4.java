package co.kr.ssdroidsample.sviewpager;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import co.kr.ssdroidlib.page.SViewPager;
import co.kr.ssdroidsample.R;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;



public class Fragment4 extends Fragment {

    public Fragment4() {
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
        Log.d("JavaSong","4 - onCreateView");
        final SViewPager viewPager = (SViewPager)container;

        View view =  inflater.inflate(R.layout.fragment_4, container, false);
        Button button = view.findViewById(R.id.btnBackHome);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.BackPage("Main",null, true);
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        Log.d("JavaSong","Delete - Fragment4");
        super.onDestroyView();
    }
}
