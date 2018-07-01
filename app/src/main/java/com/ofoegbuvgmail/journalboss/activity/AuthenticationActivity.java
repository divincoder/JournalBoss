package com.ofoegbuvgmail.journalboss.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ofoegbuvgmail.journalboss.R;
import com.ofoegbuvgmail.journalboss.database.UserDB;
import com.ofoegbuvgmail.journalboss.model.User;
import com.ofoegbuvgmail.journalboss.utills.DialogUtils;

import es.dmoral.toasty.Toasty;

public class AuthenticationActivity extends AppCompatActivity implements View.OnClickListener, UserDB.GetUser, UserDB.GetUserAfterSignIn{

    private static final String TAG = AuthenticationActivity.class.getSimpleName();
    private Button googleSignIn;
    private LinearLayout containerLayout;

    private static final int RC_SIGN_IN = 100;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseFirestore mDatabase;

    View spinKitView;
    View decorView;
    private AlertDialog errorDialog;
    MaterialDialog.Builder googleSignInErrordialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        setSupportActionBar(toolbar);

        decorView = getWindow().getDecorView();
        spinKitView = findViewById(R.id.spin_kit);
        googleSignIn = findViewById(R.id.sign_in_with_google_button);
        containerLayout = findViewById(R.id.container);
        googleSignIn.setOnClickListener(this);

// [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();
        // Access a Cloud Firestore instance from your Activity

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        //Get Firestore instance
        mDatabase = FirebaseFirestore.getInstance();

        String errorMessage = "There was a problem signin with google, please check your internet connection, " +
                "and make sure atleast one google account" +
                "is signined in on the phone.\n\n(NB: if problem persists try other signin options)";

        googleSignInErrordialog = new MaterialDialog.Builder(this)
                .title("Google authentication failed")
                .content(errorMessage).negativeText("Close")
                .onNegative((dialog, which) -> spinKitView.setVisibility(View.INVISIBLE));

        errorDialog = DialogUtils.getErrorDialogBuilder(this, "Connection failed",
                "No network connection\nPlease make sure you are connected to the internet")
                .setNegativeButton("Exit", (dialog, which) -> finish())
                .setCancelable(false).create();

        containerLayout.setVisibility(View.GONE);

        if (null != FirebaseAuth.getInstance().getCurrentUser()) {
            Intent intent = new Intent(AuthenticationActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else
            containerLayout.setVisibility(View.VISIBLE);

    }

    @Override
    public void onClick(View v) {
        spinKitView.setVisibility(View.VISIBLE);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        AuthenticationActivity.this.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);


            } catch (ApiException e) {
                Log.w("TAG", "Google sign in failed" + e.getMessage(), e);

                googleSignInErrordialog.show();

            }
        }
    }

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.i(TAG, "Google signIn success");

                        UserDB.getInstance(getApplicationContext())
                                .geUserAfterSigninProfile(AuthenticationActivity.this);

                        Log.d(TAG, "signInWithCredential:success");

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());

                        googleSignInErrordialog.show();
                    }

                });
    }

    //[GET USER AFTER SIGNIN SUCCESSFULL]
    @Override
    public void onUserReturnedUser(User user) {
        if (user != null) {
            startActivity(new Intent(AuthenticationActivity.this, MainActivity.class));
            finish();
        } else {
            UserDB.getInstance(this).signout(this);
            containerLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNoUserRegistered() {

    }

    @Override
    public void onUserAlreadyRegister(User user) {
        Intent intent = new Intent(AuthenticationActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onUserNotRegister() {

    }

    @Override
    public void onUserRetrieveError() {
        Toasty.error(this, "An error occured please retry login in",
                Toast.LENGTH_SHORT).show();
    }
    // [END auth_with_google]

}
