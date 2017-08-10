package floatingcube.pointofsale.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.List;

import floatingcube.pointofsale.R;
import floatingcube.pointofsale.adapters.QuickMenuAdapter;
import floatingcube.pointofsale.models.QuickMenuModel;

/**
 * Created by Steven Nguyen on 8/31/2016.
 */
public class QuickMenuFragment extends Fragment {
    private GridView quickKeyGrid;
    private QuickMenuAdapter quickKeyAdapter;
    List<QuickMenuModel> quickKeyList;
    private Activity activity;
    CountDownTimer countDownTimer;
    public QuickMenuFragment() {

    }

    public QuickMenuFragment(List<QuickMenuModel> quickKeyList, Activity activity) {
        this.quickKeyList = quickKeyList;
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pager_quick_keys, container, false);
        quickKeyGrid = (GridView) view.findViewById(R.id.gvQuickKeys);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(activity != null) {
            quickKeyAdapter = new QuickMenuAdapter(quickKeyList, activity);
            if(quickKeyGrid != null) {
                quickKeyGrid.setAdapter(quickKeyAdapter);
            }

            quickKeyGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if(quickKeyList.get(i).Product != null) {
                        long quickKeyId = quickKeyList.get(i).getId();
                        ((QuickMenuActivity) activity).removeQuickKey(quickKeyId);
                    }
                }
            });
        }
    }
}
