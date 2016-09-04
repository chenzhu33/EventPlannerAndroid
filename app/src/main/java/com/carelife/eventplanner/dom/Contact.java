package com.carelife.eventplanner.dom;

import com.orm.SugarRecord;

/**
 * Created by carelife on 2016/8/11.
 */
public class Contact extends SugarRecord<Contact> {
    public String name;
    public String phoneNum;

    public String getName() {
        return name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
