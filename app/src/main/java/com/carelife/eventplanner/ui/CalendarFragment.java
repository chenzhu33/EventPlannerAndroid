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
import android.widget.ImageButton;
import android.widget.TextView;

import com.carelife.eventplanner.R;
import com.carelife.eventplanner.dom.Plan;
import com.carelife.eventplanner.utils.TimeUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class CalendarFragment extends Fragment {

    private PlanListAdapter mAdapter;
    private List<Plan> planList = new ArrayList<>();

    private boolean sortByTime = false;
    private AlertDialog alertDialog;
    private CalendarView calendar;
    private TextView calendarCenter;

    public CalendarFragment() {
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
        View view = inflater.inflate(R.layout.fragment_item_grid, container, false);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sortByTime = sp.getBoolean("sort_method",false);

        calendar = (CalendarView)view.findViewById(R.id.calendar);
        calendar = (CalendarView)view.findViewById(R.id.calendar);

        ImageButton calendarLeft = (ImageButton) view.findViewById(R.id.calendarLeft);
        calendarCenter = (TextView)view.findViewById(R.id.calendarCenter);
        ImageButton calendarRight = (ImageButton) view.findViewById(R.id.calendarRight);

        calendar.setCalendarData(new Date(System.currentTimeMillis()));


        calendarCenter.setText(calendar.getYearAndmonth());
        calendarLeft.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                calendarCenter.setText(calendar.clickLeftMonth());
            }
        });

        calendarRight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                calendarCenter.setText(calendar.clickRightMonth());
            }
        });

        calendar.setOnItemClickListener(new CalendarView.OnItemClickListener() {

            @Override
            public void OnItemClick(Date downDate) {
                Calendar c = Calendar.getInstance();
                c.setTime(downDate);
                int downYear = c.get(Calendar.YEAR);
                int downMonth = c.get(Calendar.MONTH)+1;
                int downDay = c.get(Calendar.DAY_OF_MONTH);

                for(Plan plan : planList) {
                    long startTime = plan.getStartDate();
                    String[] dateString = TimeUtil.toSimpleDate(startTime).split("-");
                    if(Integer.parseInt(dateString[0])==downYear && Integer.parseInt(dateString[1]) == downMonth && Integer.parseInt(dateString[2])==downDay) {
                        showDialog(plan);
                        break;
                    }
                }
            }
        });
        mAdapter = new PlanListAdapter(this.getActivity(), planList);
        calendar.setAdapter(mAdapter);
        return view;
    }

    private void showDialog(final Plan plan) {
        alertDialog = new AlertDialog.Builder(getActivity()).
                setTitle(plan.title).
                setMessage(plan.venue+"\n"+plan.location+"\n"+TimeUtil.toDate(plan.startDate)+" -- "+TimeUtil.toDate(plan.endDate)).
                setIcon(R.drawable.ic_launcher).
                setPositiveButton(getResources().getString(R.string.tips_confirm_info_detail), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(getActivity(),PlanDetailActivity.class);
                        i.putExtra("PlanId",plan.getId());
                        startActivity(i);
                        alertDialog.dismiss();
                    }
                }).
                setNegativeButton(getResources().getString(R.string.tips_confirm_info_delete), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        plan.delete();
                        planList.remove(plan);
                        mAdapter.notifyDataSetChanged();
                        alertDialog.dismiss();
                    }
                }).
                create();
        alertDialog.show();
    }

    public void notifyChange() {
        mAdapter.notifyDataSetChanged();
    }
}
