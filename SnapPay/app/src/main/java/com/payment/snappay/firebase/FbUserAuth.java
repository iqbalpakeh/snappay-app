package com.payment.snappay.firebase;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.payment.snappay.AppSharedPref;
import com.payment.snappay.model.User;

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
    private FbUserAuth(Context context, FbUserAuthAble anInterface) {

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
     * Build FbUserAuth object
     *
     * @param context     of application
     * @param anInterface to be implemented
     * @return FbUserAuth object
     */
    public static FbUserAuth build(Context context, FbUserAuthAble anInterface) {
        return new FbUserAuth(context, anInterface);
    }

    /**
     * Connect to Firebase to register user if it's not existed yet. If user is
     * already register, than try to login.
     *
     * @param email    of user
     * @param password of user
     */
    public void loginOrRegister(final String email, final String password) {

        showProgress(true);

        mEmail = email;
        mPassword = password;

        mAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener((Activity) mContext,
                new OnCompleteListener<AuthResult>() {
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
     * Connect to firebase to login existing user as attempt to register the user
     * was already failed
     */
    private void continueWithLogin() {
        mAuth.signInWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener((Activity) mContext,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Log.d(LOG_TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {

                            Log.w(LOG_TAG, "signInWithEmail:failed", task.getException());
                            mInterface.onLoginFailed();

                        } else {
                            userLogin();
                        }
                    }
                });
    }

    /**
     * Login user to firebase server
     */
    private void userLogin() {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String token = FirebaseInstanceId.getInstance().getToken();

        mDatabase.child(FbContract.ROOT_CONSUMER).child(uid).child("token").setValue(token);
        AppSharedPref.storeUserData(mContext, mEmail, token, uid);

        mInterface.onLoginSuccess();
    }

    /**
     * Create new user data in Firebase real time data base
     */
    private void createNewUser() {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String token = FirebaseInstanceId.getInstance().getToken();

        AppSharedPref.storeUserData(mContext, mEmail, token, uid);

        mDatabase.child(ROOT_CONSUMER).child(uid).setValue(new User(mEmail, token))
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
