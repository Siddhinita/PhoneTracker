package com.example.sid.phonetracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by sid on 6/2/18.
 */

public class SmsReceiver extends BroadcastReceiver {
    private static SmsListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data  = intent.getExtras();

        // PDU: “protocol data unit”, the industry format for an SMS message
        Object[] pdus = (Object[]) data.get("pdus");

        for(int i=0; i<pdus.length; i++){

            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);

            String sender = smsMessage.getDisplayOriginatingAddress();
            // We can use this sender to filter messages


                String messageBody = smsMessage.getMessageBody();
                messageBody=messageBody.trim().toLowerCase();
                messageBody=messageBody.replaceAll("\\s", "");
                // Pass the message text to interface
            if(messageBody.equals("locatephone")) {
                mListener.messageReceived(sender);
            }
        }
    }

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }

}
