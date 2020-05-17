package co.kr.ssdroidsample;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import co.kr.ssdroidlib.page.SViewPager;
import co.kr.ssdroidsample.R;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment4#newInstance} factory method to
 * create an instance of this fragment.
 */
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
        Log.d("JavaSong","4 - onCreateView");

        final SViewPager viewPager = (SViewPager)container;

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_4, container, false);
        Button btn = view.findViewById(R.id.btnBack);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.BackPage(null,true);
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
