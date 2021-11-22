package com.example.joboui.broadcast;

import static com.example.joboui.login.SignInActivity.getObjectMapper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

public class SMSBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();

            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);


            try {
                System.out.println(getObjectMapper().writeValueAsString(extras.get(SmsRetriever.EXTRA_STATUS)) + " ::: STATUS");
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            switch(status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    // Get SMS message contents
                    String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                    System.out.println("CODE IS "+message);
                    // Extract one-time code from the message and complete verification
                    // by sending the code back to your server.
                    break;
                case CommonStatusCodes.TIMEOUT:
                    // Waiting for SMS timed out (5 minutes)
                    // Handle the error ...
                    break;
            }
        }
/*
            while (codes.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    var otp: String = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String

                    if (otpReceiveInterface != null) {

                        otp = otp.replace("<#> Your otp code is : ", "").split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                        //otp = otp.replace("<#> Your otp code is: ", "")
                        //You can filter OTP here & send to activity                   }
                    }

                    CommonStatusCodes.TIMEOUT ->
                    if (otpReceiveInterface != null) {
                        //SMS retriving timeout, you can notify activity for same           }
                    }

                }
    }*/
        }

}