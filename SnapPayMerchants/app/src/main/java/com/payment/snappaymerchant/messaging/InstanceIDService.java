package com.payment.snappaymerchant.messaging;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.payment.snappaymerchant.AppSharedPref;
import com.payment.snappaymerchant.firebase.FbContract;

public class InstanceIDService extends FirebaseInstanceIdService {

    private static final String LOG_TAG = InstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(LOG_TAG, "Refreshed token: " + refreshedToken);

        AppSharedPref.storeUserToken(getApplicationContext(), refreshedToken);

        String uid = AppSharedPref.getUID(getApplicationContext());
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(FbContract.ROOT_MERCHANT).child(uid).child("token").setValue(refreshedToken);
    }
}
