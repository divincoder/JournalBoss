package com.ofoegbuvgmail.journalboss.database;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ofoegbuvgmail.journalboss.R;
import com.ofoegbuvgmail.journalboss.model.User;

public class UserDB {

    private static UserDB userDB;
    private FirebaseUser currentUser;
    private FirebaseFirestore database;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private Context context;

    public static UserDB getInstance(Context context) {
        if (userDB == null) {
            userDB = new UserDB(context);
        }

        return userDB;
    }

    public UserDB(Context context) {
        this.context = context;

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseFirestore.getInstance();

    }


    public void getCurrentUserProfile(GetUser getUser) {

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (null != currentUser) {

        }
    }


    public void geUserAfterSigninProfile(GetUserAfterSignIn getUser) {

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (null != currentUser) {
            User user = new User();
            user.setEmail(currentUser.getEmail());

            getUser.onUserAlreadyRegister(user);

        } else {
            getUser.onUserNotRegister();
        }
    }

    public void signInWithGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);

    }
    public void signOut(Activity activity, UserSignout userSignout) {
        mAuth.signOut();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient
                = GoogleSignIn.getClient(context, gso);

        mGoogleSignInClient.signOut().addOnCompleteListener(activity,
                task -> {
                    // Google revoke access
                    mGoogleSignInClient.revokeAccess();
                    userSignout.onSignoutSuccess();
                }).addOnFailureListener(e -> userSignout.onSignoutFailed());
    }

    public void signout(Activity activity) {
        FirebaseAuth.getInstance().signOut();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient
                = GoogleSignIn.getClient(context, gso);

        mGoogleSignInClient.signOut().addOnCompleteListener(activity,
                task -> {
                    // Google revoke access
                    mGoogleSignInClient.revokeAccess();
                });
    }

    public void deleteUser(Context context) {
        mAuth.signOut();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignIn.getClient(context, gso).signOut();
        currentUser.delete();
    }


    public interface UserSignout {
        void onSignoutSuccess();

        void onSignoutFailed();
    }

    public interface GetUser {
        void onUserReturnedUser(User user);
        void onNoUserRegistered();
    }

    public interface GetUserAfterSignIn {
        void onUserAlreadyRegister(User user);
        void onUserNotRegister();
        void onUserRetrieveError();
    }
}
