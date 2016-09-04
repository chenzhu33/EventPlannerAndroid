package com.carelife.eventplanner.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.carelife.eventplanner.R;
import com.carelife.eventplanner.dom.Plan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ItemFragment extends Fragment implements AbsListView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private ListView mListView;

    private PlanListAdapter mAdapter;
    private List<Plan> planList = new ArrayList<>();
    private boolean sortByTime = false;
    private AlertDialog alertDialog;

    public ItemFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        planList.clear();
        planList.addAll(Plan.listAll(Plan.class));

        if(sortByTime) {
            Collections.sort(planList, new Comparator<Plan>() {
                @Override
                public int compare(Plan lhs, Plan rhs) {
                    return (int)((lhs.startDate-rhs.startDate)/1000);
                }
            });
        }
        if(mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sortByTime = sp.getBoolean("sort_method",false);

        mAdapter = new PlanListAdapter(this.getActivity(), planList);
        mListView = (ListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent(this.getActivity(),PlanDetailActivity.class);
        i.putExtra("PlanId",planList.get(position).getId());
        startActivity(i);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        alertDialog = new AlertDialog.Builder(getActivity()).
                setTitle(getResources().getString(R.string.tips_confirm_delete_title)).
                setMessage(getResources().getString(R.string.tips_confirm_delete_plan)).
                setIcon(R.drawable.ic_launcher).
                setPositiveButton(getResources().getString(R.string.tips_confirm_ok), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        planList.get(position).delete();
                        planList.remove(position);
                        mAdapter.notifyDataSetChanged();
                        alertDialog.dismiss();
                    }
                }).
                setNegativeButton(getResources().getString(R.string.tips_confirm_cancel), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                }).
                create();
        alertDialog.show();
        return true;
    }

    public void notifyChange() {
        mAdapter.notifyDataSetChanged();
    }
    
    public void sort(boolean ascend) {
        if(ascend) {
            Collections.sort(planList, new Comparator<Plan>() {
                @Override
                public int compare(Plan lhs, Plan rhs) {
                    return (int)((lhs.startDate-rhs.startDate)/1000);
                }
            });
        } else {
            Collections.sort(planList, new Comparator<Plan>() {
                @Override
                public int compare(Plan lhs, Plan rhs) {
                    return (int)((rhs.startDate-lhs.startDate)/1000);
                }
            });
        }
        mAdapter.notifyDataSetChanged();
    }
}
