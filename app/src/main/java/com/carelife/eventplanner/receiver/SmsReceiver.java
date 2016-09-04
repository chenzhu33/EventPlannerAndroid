package com.carelife.eventplanner.receiver;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.view.WindowManager;

import com.carelife.eventplanner.R;
import com.carelife.eventplanner.dom.Plan;

/**
 * Created by carelife on 2016/8/13.
 */
public class SmsReceiver extends BroadcastReceiver {

    private AlertDialog alertDialog;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage msg;
        String messageBody = "";
        if (null != bundle) {
            Object[] smsObj = (Object[]) bundle.get("pdus");
            for (Object object : smsObj) {
                msg = SmsMessage.createFromPdu((byte[]) object);
                messageBody += msg.getDisplayMessageBody();
            }
            if(messageBody.startsWith("(EP)")) {
                showDialog(context, messageBody);
            }
        }
    }

    public boolean showDialog(Context context, final String messageBody) {
        alertDialog = new AlertDialog.Builder(context).
                setTitle(context.getResources().getString(R.string.tips_confirm_accept_title)).
                setMessage(context.getResources().getString(R.string.tips_confirm_accept_plan)).
                setIcon(R.drawable.ic_launcher).
                setPositiveButton(context.getResources().getString(R.string.tips_confirm_accept_ok), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String info = messageBody.replace("(EP)","");
                        String[] planInfo = info.split(";");
                        Plan plan = new Plan();
                        plan.setTitle(planInfo[0]);
                        plan.setLocation(planInfo[1]);
                        plan.setVenue(planInfo[2]);
                        plan.setStartDate(Long.parseLong(planInfo[3]));
                        plan.setEndDate(Long.parseLong(planInfo[4]));
                        plan.setNote(planInfo[5]);
                        plan.save();
                        alertDialog.dismiss();
                    }
                }).
                setNegativeButton(context.getResources().getString(R.string.tips_confirm_accept_cancel), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                }).
                create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();
        return true;
    }
}