
package com.androidbegin.callblocker;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

// Extend the class from BroadcastReceiver to listen when there is a incoming call
public class CallBarring extends BroadcastReceiver {
    // This String will hold the incoming phone number
    private String number;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        // If, the received action is not a type of "Phone_State", ignore it
        if (!intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            return;
        } else {
            // Fetch the number of incoming call
            number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

            // Check, whether this is a member of "Black listed" phone numbers stored in the database
            for (final Blacklist bl : MainActivity.blockList) {
                if (number.startsWith(bl.phoneNumber)) {
                    // If yes, invoke the method
                    disconnectPhoneItelephony(context);
                    return;
                }
            }
            // if (MainActivity.blockList.contains(new Blacklist(number))) {
            // // If yes, invoke the method
            // disconnectPhoneItelephony(context);
            // return;
            // }
        }
    }

    // Method to disconnect phone automatically and programmatically
    // Keep this method as it is
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void disconnectPhoneItelephony(final Context context) {
        ITelephony telephonyService;
        final TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            final Class c = Class.forName(telephony.getClass().getName());
            final Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            telephonyService = (ITelephony) m.invoke(telephony);
            telephonyService.endCall();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
