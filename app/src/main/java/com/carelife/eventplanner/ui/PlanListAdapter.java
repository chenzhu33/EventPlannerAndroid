package com.carelife.eventplanner.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.carelife.eventplanner.R;
import com.carelife.eventplanner.dom.Plan;
import com.carelife.eventplanner.utils.TimeUtil;

import java.util.List;

/**
 * Created by carelifead on 2016/4/7.
 */
public class PlanListAdapter extends BaseAdapter {
    private final LayoutInflater layoutInflater;
    private List<Plan> planList;
    private Context mContext;
    private ViewHolder viewHolder = null;

    public PlanListAdapter(Context context, List<Plan> list) {
        planList = list;
        mContext = context;
        layoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return planList.size();
    }

    @Override
    public Object getItem(int position) {
        return planList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.fragment_item, null);
            viewHolder.title = (TextView) convertView
                    .findViewById(R.id.tv_title);
            viewHolder.venue = (TextView) convertView
                    .findViewById(R.id.tv_location);
            viewHolder.time = (TextView) convertView
                    .findViewById(R.id.tv_time);
            viewHolder.number = (TextView) convertView
                    .findViewById(R.id.tv_number);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        planList.get(position).convertToList();
        viewHolder.title.setText(planList.get(position).title);
        viewHolder.venue.setText(planList.get(position).venue);
        viewHolder.number.setText(""+planList.get(position).attendees.size());
        viewHolder.time.setText(TimeUtil.toDate(planList.get(position).startDate)+" - "+ TimeUtil.toDate(planList.get(position).endDate));
        return convertView;
    }

    public class ViewHolder {
        public TextView title;
        public TextView venue;
        public TextView time;
        public TextView number;

    }
}
