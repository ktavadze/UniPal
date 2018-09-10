package edu.ramapo.ktavadze.unipal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInActivity extends AppCompatActivity {
    private static final String TAG = "SignInActivity";

    private static final int RC_SIGN_IN = 13;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        addSignInListener();

        addAuthListener();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        removeAuthListener();

        removeSignInListener();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent()
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    /**/
    /*
    NAME

    firebaseAuthWithGoogle - Firebase authentication using Google account.

    SYNOPSIS

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct);
    acct--> the Google account to be used with Firebase.

    DESCRIPTION

    Will perform Firebase authentication using the provided Google account.

    RETURNS

    N/A
    */
    /**/
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle: " + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Log.d(TAG, "signInWithCredential: Success");
                        } else {
                            // If sign in fails, display a message to the user
                            Log.w(TAG, "signInWithCredential: Failure", task.getException());

                            Snackbar.make(findViewById(R.id.sign_in), "Authentication Failed", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**/
    /*
    NAME

    signIn - Google sign in.

    SYNOPSIS

    private void signIn();

    DESCRIPTION

    Will perform Google sign in.

    RETURNS

    N/A
    */
    /**/
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**/
    /*
    NAME

    addSignInListener - adds sign in button listener.

    SYNOPSIS

    private void addSignInListener();

    DESCRIPTION

    Will add the click listener to the Google sign in button.

    RETURNS

    N/A
    */
    /**/
    private void addSignInListener() {
        // Add sign in listener
        SignInButton sign_in_button = findViewById(R.id.sign_in_button);
        sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        Log.d(TAG, "addSignInListener: Listener added");
    }

    /**/
    /*
    NAME

    removeSignInListener - removes sign in button listener.

    SYNOPSIS

    private void removeSignInListener();

    DESCRIPTION

    Will remove the click listener from the Google sign in button.

    RETURNS

    N/A
    */
    /**/
    private void removeSignInListener() {
        // Remove sign in listener
        SignInButton sign_in_button = findViewById(R.id.sign_in_button);
        sign_in_button.setOnClickListener(null);

        Log.d(TAG, "removeSignInListener: Listener removed");
    }

    /**/
    /*
    NAME

    addAuthListener - adds authentication listener.

    SYNOPSIS

    private void addAuthListener();

    DESCRIPTION

    Will add the Firebase authentication state listener.

    RETURNS

    N/A
    */
    /**/
    private void addAuthListener() {
        // Add auth listener
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    startActivity(new Intent(SignInActivity.this, MainActivity.class));

                    finish();
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);

        Log.d(TAG, "addAuthListener: Listener added");
    }

    /**/
    /*
    NAME

    removeAuthListener - removes authentication listener.

    SYNOPSIS

    private void removeAuthListener();

    DESCRIPTION

    Will remove the Firebase authentication state listener.

    RETURNS

    N/A
    */
    /**/
    private void removeAuthListener() {
        // Remove auth listener
        mAuth.removeAuthStateListener(mAuthListener);

        Log.d(TAG, "removeAuthListener: Listener removed");
    }
}
