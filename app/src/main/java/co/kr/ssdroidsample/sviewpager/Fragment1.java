package co.kr.ssdroidsample.sviewpager;

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
        Object Param = viewPager.GetCurrentParam();

        View view =  inflater.inflate(R.layout.fragment_1, container, false);
        final TextView bb = view.findViewById(R.id.txtView);

        Button button = view.findViewById(R.id.btnNext);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                bb.setText("dkdkdkd");
                viewPager.goPage(new Fragment2(),null,true);
            }
        });
        return  view;
    }


    @Override
    public void OnBackActive(Object Param) {

    }


    @Override
    public void onDestroyView() {
        Log.d("JavaSong","Delete - Fragment1");
        super.onDestroyView();
    }
}
