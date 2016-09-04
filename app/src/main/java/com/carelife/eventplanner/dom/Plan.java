package com.carelife.eventplanner.dom;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carelife on 2016/8/11.
 */
public class Plan extends SugarRecord<Plan> {
    public String planId;
    public String title;
    public long startDate;
    public long endDate;
    public String location;
    public String venue;
    public String note;
    public String attendString;
    public String recordPath;

    @Ignore
    public List<Contact> attendees;

    public Plan() {

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

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getPlanId() {
        return planId;
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
}
