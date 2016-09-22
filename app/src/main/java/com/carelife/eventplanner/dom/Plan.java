package com.carelife.eventplanner.dom;

import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carelife on 2016/8/11.
 */
public class Plan {
    public long _id;
    public String title;
    public long startDate;
    public long endDate;
    public String location;
    public String venue;
    public String note;
    public String attendString;
    public String recordPath;

    public List<Contact> attendees;

    public Plan() {

    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public List<Contact> getAttendees() {
        return attendees;
    }

    public long getEndDate() {
        return endDate;
    }

    public long getStartDate() {
        return startDate;
    }

    public String getLocation() {
        return location;
    }

    public String getNote() {
        return note;
    }

    public String getTitle() {
        return title;
    }

    public String getVenue() {
        return venue;
    }

    public void setAttendees(List<Contact> attendees) {
        this.attendees = attendees;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public void setNote(String note) {
        this.note = note;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getAttendString() {
        return attendString;
    }

    public void setAttendString(String attendString) {
        this.attendString = attendString;
    }

    public String getRecordPath() {
        return recordPath;
    }

    public void setRecordPath(String recordPath) {
        this.recordPath = recordPath;
    }

    public void convertToString() {
        Gson gson = new Gson();
        attendString = gson.toJson(attendees);
    }

    public void convertToList() {
        if(attendees == null || attendees.isEmpty()) {
            Gson gson = new Gson();
            attendees = gson.fromJson(attendString, new TypeToken<ArrayList<Contact>>() {
            }.getType());

            if (attendees == null) {
                attendees = new ArrayList<>();
            }
        }
    }

    public static Plan fromCursor(Cursor cursor) {
        Plan plan = new Plan();
        plan._id = cursor.getLong(cursor.getColumnIndex("_id"));
        plan.title = cursor.getString(cursor.getColumnIndex("title"));
        plan.startDate = cursor.getLong(cursor.getColumnIndex("startDate"));
        plan.endDate = cursor.getLong(cursor.getColumnIndex("endDate"));
        plan.location = cursor.getString(cursor.getColumnIndex("location"));
        plan.venue = cursor.getString(cursor.getColumnIndex("venue"));
        plan.note = cursor.getString(cursor.getColumnIndex("note"));
        plan.attendString = cursor.getString(cursor.getColumnIndex("attendString"));
        plan.recordPath = cursor.getString(cursor.getColumnIndex("recordPath"));
        return plan;
    }
}
