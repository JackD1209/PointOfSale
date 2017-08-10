package floatingcube.pointofsale.activities;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import floatingcube.pointofsale.R;

/**
 * Created by Son on 8/6/2016.
 */
public class Fragment_2 extends Fragment{
    @Nullable
    boolean check1, check2, check3;
    Switch switch1;
    Switch switch2;
    Switch switch3;
    SharedPreferences.Editor editor;
    SharedPreferences preferences;
    SharedPreferences get_reference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_2,container,false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = preferences.edit();

        switch1 = (Switch)getActivity().findViewById(R.id.sw_switch1);
        switch2 = (Switch)getActivity().findViewById(R.id.sw_switch2);
        switch3 = (Switch)getActivity().findViewById(R.id.sw_switch3);

        switch1.setOnCheckedChangeListener(check_change);
        switch2.setOnCheckedChangeListener(check_change);
        switch3.setOnCheckedChangeListener(check_change);
        get_reference = PreferenceManager.getDefaultSharedPreferences(getActivity());
        check1=get_reference.getBoolean("checked_unlocked",false);
        check2=get_reference.getBoolean("checked_return_10",false);
        check3=get_reference.getBoolean("checked_return_sale",false);
        switch1.setChecked(check1);
        switch2.setChecked(check2);
        switch3.setChecked(check3);

    }

    private CompoundButton.OnCheckedChangeListener check_change  = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            switch (compoundButton.getId()){
                case R.id.sw_switch1:
                    if(isChecked){
                        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        editor.putBoolean("checked_unlocked",true);
                        editor.apply();
                    }
                    else {
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        editor.putBoolean("checked_unlocked",false);
                        editor.apply();
                    }break;
                case R.id.sw_switch2:
                    if(isChecked){
                        editor.putBoolean("checked_return_10",true);
                        editor.apply();
                    }
                    else {
                        editor.putBoolean("checked_return_10",false);
                        editor.apply();
                    }break;
                case R.id.sw_switch3:
                    if(isChecked){
                        editor.putBoolean("checked_return_sale",true);
                        editor.apply();

                    }
                    else {
                        editor.putBoolean("checked_return_sale",false);
                        editor.apply();
                    }break;
            }
        }
    };


}
