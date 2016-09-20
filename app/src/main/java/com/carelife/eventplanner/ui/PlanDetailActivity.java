package com.carelife.eventplanner.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.carelife.eventplanner.R;
import com.carelife.eventplanner.controller.VoiceController;
import com.carelife.eventplanner.dom.Contact;
import com.carelife.eventplanner.dom.Plan;
import com.carelife.eventplanner.utils.TimeUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 修改: location选择改为从地图上选择点,这个功能是额外功能,作业里没要求
 */
public class PlanDetailActivity extends ActionBarActivity {

    private Plan plan;
    private EditText editTitle;
    private EditText editVenue;
    private TextView editLocation;
    private TextView editStartTime;
    private TextView editEndTime;
    private EditText editNote;

    private Button btnPlay;
    private Button btnRecord;

    private ListView attendeeListView;
    private AttendeeAdapter attendeeAdapter;
    private AlertDialog attendeeAlertDialog;

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    private VoiceController voiceController;
    private boolean isPlay = false;
    private boolean isRecord = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_content_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTitle = (EditText) findViewById(R.id.plan_edit1);
        editVenue = (EditText) findViewById(R.id.plan_edit2);
        editLocation = (TextView) findViewById(R.id.plan_edit3);
        editStartTime = (TextView) findViewById(R.id.plan_edit4);
        editEndTime = (TextView) findViewById(R.id.plan_edit5);
        editNote = (EditText) findViewById(R.id.plan_edit6);

        attendeeListView = (ListView) findViewById(R.id.list_attendee);
        attendeeListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                attendeeAlertDialog = new AlertDialog.Builder(PlanDetailActivity.this).
                        setTitle(getResources().getString(R.string.tips_confirm_delete_title)).
                        setMessage(getResources().getString(R.string.tips_confirm_delete_attendee)).
                        setIcon(R.drawable.ic_launcher).
                        setPositiveButton(getResources().getString(R.string.tips_confirm_ok), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                plan.attendees.remove(position);
                                plan.convertToString();
                                plan.save();
                                attendeeAdapter.notifyDataSetChanged();
                                attendeeAlertDialog.dismiss();
                            }
                        }).
                        setNegativeButton(getResources().getString(R.string.tips_confirm_cancel), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                attendeeAlertDialog.dismiss();
                            }
                        }).
                        create();
                attendeeAlertDialog.show();
                return true;
            }
        });

        editStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                year = Calendar.getInstance().get(Calendar.YEAR);
                month = Calendar.getInstance().get(Calendar.MONTH);
                day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                hour = Calendar.getInstance().get(Calendar.HOUR);
                minute = Calendar.getInstance().get(Calendar.MINUTE);

                DatePickerDialog dpd = new DatePickerDialog(PlanDetailActivity.this, dateStartlistener, year, month, day);
                dpd.show();
            }
        });

        editEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                year = Calendar.getInstance().get(Calendar.YEAR);
                month = Calendar.getInstance().get(Calendar.MONTH);
                day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                hour = Calendar.getInstance().get(Calendar.HOUR);
                minute = Calendar.getInstance().get(Calendar.MINUTE);

                DatePickerDialog dpd = new DatePickerDialog(PlanDetailActivity.this, dateEndlistener, year, month, day);
                dpd.show();
            }
        });
        editLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlanDetailActivity.this, MapsActivity.class);
                String[] coordinates = editLocation.getText().toString().split(",");
                if(coordinates.length == 0) {
                    return;
                }
                intent.putExtra("lat",31.05);
                intent.putExtra("lon",121.76);
                //intent.putExtra("lat",coordinates[0]);
                //intent.putExtra("lon",coordinates[1]);
                startActivityForResult(intent, 2);
            }
        });

        Button addAttendee = (Button) findViewById(R.id.btn_add_attendee);
        addAttendee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlanDetailActivity.this, ContactsActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        voiceController = VoiceController.getInstance();

        btnRecord = (Button) findViewById(R.id.plan_btn_record);
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecord) {
                    voiceController.stopRecord();
                    btnRecord.setText(getResources().getString(R.string.plan_voice_record));
                    btnPlay.setClickable(true);
                } else {
                    String path = voiceController.startRecord(PlanDetailActivity.this);
                    plan.setRecordPath(path);
                    btnRecord.setText(getResources().getString(R.string.plan_voice_recording));
                    btnPlay.setClickable(false);
                }
                isRecord = !isRecord;
            }
        });

        btnPlay = (Button) findViewById(R.id.plan_btn_play);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlay) {
                    voiceController.stopPlay();
                    btnPlay.setText(getResources().getString(R.string.plan_voice_play));
                    btnRecord.setClickable(true);
                } else {
                    voiceController.startPlay(plan.getRecordPath(), new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            btnPlay.setText(getResources().getString(R.string.plan_voice_play));
                            btnRecord.setClickable(true);
                        }
                    });
                    btnPlay.setText(getResources().getString(R.string.plan_voice_playing));
                    btnRecord.setClickable(false);
                }
                isPlay = !isPlay;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        long id = -2L;
        if (getIntent() != null) {
            id = getIntent().getLongExtra("PlanId", -2L);
        }
        if (id >= 0) {
            plan = Plan.findById(Plan.class, id);
            plan.convertToList();
            editTitle.setText(plan.title);
            editVenue.setText(plan.venue);
            editLocation.setText(plan.location);
            editStartTime.setText(TimeUtil.toDate(plan.startDate));
            editEndTime.setText(TimeUtil.toDate(plan.endDate));
            editNote.setText(plan.note);
            if (plan.getRecordPath() == null || plan.getRecordPath().isEmpty()) {
                btnPlay.setClickable(false);
            }
        } else if (id == -1) {
            plan = new Plan();
            plan.attendees = new ArrayList<>();
        } else {
            finish();
            return;
        }

        attendeeAdapter = new AttendeeAdapter(this, plan.attendees);

        attendeeListView.setAdapter(attendeeAdapter);
        setListViewHeightBasedOnChildren(attendeeListView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_plan_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        if (id == R.id.action_save) {
            if(editStartTime.getText().toString().isEmpty() || editEndTime.getText().toString().isEmpty()) {
                Toast.makeText(this, getResources().getString(R.string.error_empty_time), Toast.LENGTH_LONG).show();
                return true;
            }
            long startTimeStamp = TimeUtil.toTimeStamp(editStartTime.getText().toString());
            long endTimeStamp = TimeUtil.toTimeStamp(editEndTime.getText().toString());
            if(endTimeStamp <= startTimeStamp) {
                Toast.makeText(this, getResources().getString(R.string.error_wrong_time), Toast.LENGTH_LONG).show();
                return true;
            }
            if(editTitle.getText().toString().isEmpty()) {
                Toast.makeText(this, getResources().getString(R.string.error_empty_title), Toast.LENGTH_LONG).show();
                return true;
            }

            plan.title = editTitle.getText().toString();
            plan.venue = editVenue.getText().toString();
            plan.location = editLocation.getText().toString();
            plan.note = editNote.getText().toString();
            plan.startDate = startTimeStamp;
            plan.endDate = endTimeStamp;
            plan.save();
            finish();
            if(plan.attendees.isEmpty()) {
                return true;
            }
            StringBuilder sb = new StringBuilder();
            for (Contact contact : plan.attendees) {
                sb.append(contact.getPhoneNum()).append(";");
            }

            Uri sendSmsTo = Uri.parse("smsto:" + sb.toString());
            Intent intent = new Intent(
                    android.content.Intent.ACTION_SENDTO, sendSmsTo);
            intent.putExtra("sms_body", makeMessage());
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String makeMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("(EP)").append(plan.title).append(";")
                .append(plan.location).append(";")
                .append(plan.venue).append(";")
                .append(plan.startDate).append(";")
                .append(plan.endDate).append(";")
                .append(plan.note).append(";");
        return sb.toString();
    }

    public class AttendeeAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;
        private List<Contact> attendees;
        private Context mContext;
        private ViewHolder viewHolder;

        public AttendeeAdapter(Context context, List<Contact> list) {
            attendees = list;
            mContext = context;
            layoutInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return attendees.size();
        }

        @Override
        public Object getItem(int position) {
            return attendees.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.attendee_item, null);
                viewHolder.name = (TextView) convertView
                        .findViewById(R.id.tv_attendee_name);
                viewHolder.phone = (TextView) convertView
                        .findViewById(R.id.tv_attendee_phone);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.name.setText(attendees.get(position).name);
            viewHolder.phone.setText(attendees.get(position).phoneNum);
            return convertView;
        }

        public class ViewHolder {
            public TextView name;
            public TextView phone;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String name = data.getStringExtra("Name");
            String phoneNum = data.getStringExtra("Phone");
            Contact p = new Contact();
            p.name = name;
            p.phoneNum = phoneNum;
            boolean exist = false;
            for (Contact contact : plan.attendees) {
                if (contact.name.equals(p.name)) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                plan.attendees.add(p);
                plan.convertToString();
                attendeeAdapter.notifyDataSetChanged();
            }
        } else if(requestCode == 2 && resultCode == RESULT_OK) {
            double lat = data.getDoubleExtra("lat",0);
            double lon = data.getDoubleExtra("lon",0);
            editLocation.setText(lat+","+lon);
        }
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

        listView.setLayoutParams(params);
    }

    private DatePickerDialog.OnDateSetListener dateStartlistener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int myyear, int monthOfYear, int dayOfMonth) {
            year = myyear;
            month = monthOfYear + 1;
            day = dayOfMonth;
            updateDate();
            TimePickerDialog tpd = new TimePickerDialog(PlanDetailActivity.this, timeStartlistener, hour, minute, true);
            tpd.show();
        }

        private void updateDate() {
            editStartTime.setText(year + "-" + month + "-" + day);
        }
    };

    private DatePickerDialog.OnDateSetListener dateEndlistener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int myyear, int monthOfYear, int dayOfMonth) {
            year = myyear;
            month = monthOfYear + 1;
            day = dayOfMonth;
            updateDate();
            TimePickerDialog tpd = new TimePickerDialog(PlanDetailActivity.this, timeEndlistener, hour, minute, true);
            tpd.show();
        }

        private void updateDate() {
            editEndTime.setText(year + "-" + month + "-" + day);
        }
    };

    private TimePickerDialog.OnTimeSetListener timeStartlistener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int mhour, int mminute) {
            hour = mhour;
            minute = mminute;
            editStartTime.setText(year + "-" + month + "-" + day + " " + hour + ":" + minute);
        }
    };

    private TimePickerDialog.OnTimeSetListener timeEndlistener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int mhour, int mminute) {
            hour = mhour;
            minute = mminute;
            editEndTime.setText(year + "-" + month + "-" + day + " " + hour + ":" + minute);
        }
    };

}
