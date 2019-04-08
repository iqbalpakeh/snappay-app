package com.payment.snappaytable.firebase;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.payment.snappaytable.AppSharedPref;

public class FbUserAuth extends FbContract {

    /**
     * for debugging purpose
     */
    private static final String LOG_TAG = FbUserAuth.class.getSimpleName();

    /**
     * Interface to be implemented
     */
    public interface FbUserAuthAble {

        /**
         * Call back when login is success
         */
        void onLoginSuccess();

        /**
         * Call back when login is failed
         */
        void onLoginFailed();

        /**
         * Call back when user signed in
         */
        void onUserSignedIn();

        /**
         * Call back when user signed out
         */
        void onUserSignedOut();
    }

    /**
     * Interface to be implemented in activity class
     */
    private FbUserAuthAble mInterface;

    /**
     * User email
     */
    private String mEmail;

    /**
     * User password
     */
    private String mPassword;

    /**
     * Constructor of FbUserAuth
     *
     * @param context of application
     */
    public FbUserAuth(Context context) {

        this.mInterface = (FbUserAuthAble) context;
        this.mContext = context;

        this.mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    AppSharedPref.storeUserData(mContext, user.getEmail(), user.getUid());
                    mInterface.onUserSignedIn();

                } else {
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_out");
                    AppSharedPref.storeUserData(mContext, "", "");
                    mInterface.onUserSignedOut();
                }
            }
        };
    }

    /**
     * Connect to firebase to login existing user as attempt to register
     * the user was already failed
     *
     * @param email    of user
     * @param password of user
     */
    public void userLogin(final String email, final String password) {

        showProgress(true);

        mEmail = email;
        mPassword = password;

        mAuth.signInWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener((Activity) mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Log.d(LOG_TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {

                            Log.w(LOG_TAG, "signInWithEmail:failed", task.getException());
                            mInterface.onLoginFailed();

                        } else {
                            showProgress(false);
                            mInterface.onLoginSuccess();
                        }
                    }
                });
    }

    /**
     * Add authentication listener to firebase authentication object
     */
    public void addAuthListener() {
        mAuth.addAuthStateListener(mAuthListener);
    }

    /**
     * Remove authentication listener from firebase authentication object
     */
    public void removeAuthListener() {
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
