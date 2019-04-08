package com.payment.snappaymerchant.firebase;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.payment.snappaymerchant.AppSharedPref;
import com.payment.snappaymerchant.model.User;

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
         * Call back when registration is success
         */
        void onRegisterSuccess();

        /**
         * Call back when registration is failed
         */
        void onRegisterFailed();

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
     * Progress dialog view
     */
    private ProgressDialog mProgressDialog;

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
     * @param context     of application
     * @param anInterface of user authentication
     */
    public FbUserAuth(Context context, FbUserAuthAble anInterface) {

        this.mInterface = anInterface;
        this.mContext = context;

        this.mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    Log.d(LOG_TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    mInterface.onUserSignedIn();

                } else {

                    Log.d(LOG_TAG, "onAuthStateChanged:signed_out");
                    mInterface.onUserSignedOut();
                }
            }
        };
    }

    /**
     * Connect to Firebase to register user if it's not existed yet. If user is already register,
     * than try to login.
     *
     * @param email    of user
     * @param password of user
     */
    public void loginOrRegister(final String email, final String password) {

        showProgress(true);

        mEmail = email;
        mPassword = password;

        mAuth.createUserWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener((Activity) mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Log.d(LOG_TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        showProgress(false);

                        if (!task.isSuccessful()) {

                            mInterface.onRegisterFailed();
                            continueWithLogin();

                        } else {
                            createNewUser();
                        }
                    }
                });
    }

    /**
     * Connect to firebase to login existing user as attempt to register
     * the user was already failed
     */
    private void continueWithLogin() {
        mAuth.signInWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener((Activity) mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Log.d(LOG_TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {

                            Log.w(LOG_TAG, "signInWithEmail:failed", task.getException());
                            mInterface.onLoginFailed();

                        } else {
                            mInterface.onLoginSuccess();
                        }
                    }
                });
    }

    /**
     * Create new user data in Firebase real time data base
     */
    private void createNewUser() {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String token = FirebaseInstanceId.getInstance().getToken();

        AppSharedPref.storeUserData(mContext, mEmail, token, uid);

        mDatabase.child(ROOT_MERCHANT).child(uid).setValue(new User(mEmail, token))
                .addOnCompleteListener((Activity) mContext, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mInterface.onRegisterSuccess();
                        } else {
                            Log.w(LOG_TAG, "createNewUser:failed", task.getException());
                        }
                    }
                });
    }

    /**
     * Shows the progress UI and hides the login form
     *
     * @param show flag to activate progress ui
     */
    void showProgress(final boolean show) {

        if (show) {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setMessage("Accessing SnapPay server...");
            mProgressDialog.setCancelable(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();

        } else {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
        }
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
