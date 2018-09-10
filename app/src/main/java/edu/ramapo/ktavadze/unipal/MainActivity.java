package edu.ramapo.ktavadze.unipal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public Database mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addAuthListener();

        // Initialize
        if (mCurrentUser != null) {
            initUser();

            mDatabase = new Database(this);
            mDatabase.addListeners();
            mDatabase.selectEvents(31);

            initView();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        removeAuthListener();

        if (mCurrentUser != null) {
            mDatabase.removeListeners();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_dashboard:
                return loadFragment(new DashboardFragment());
            case R.id.navigation_calendar:
                return loadFragment(new CalendarFragment());
            case R.id.navigation_courses:
                return loadFragment(new CoursesFragment());
            case R.id.navigation_schools:
                return loadFragment(new SchoolsFragment());
            case R.id.navigation_user:
                return loadFragment(new UserFragment());
            default:
                return false;
        }
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
        mCurrentUser = mAuth.getCurrentUser();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
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
        // remove auth listener
        mAuth.removeAuthStateListener(mAuthListener);

        Log.d(TAG, "removeAuthListener: Listener removed");
    }

    /**/
    /*
    NAME

    initUser - initializes user.

    SYNOPSIS

    private void initUser();

    DESCRIPTION

    Will populate the static fields of the user class for local use.

    RETURNS

    N/A
    */
    /**/
    private void initUser() {
        // Get Google provider data
        UserInfo profile = mCurrentUser.getProviderData().get(1);

        final String displayName = profile.getDisplayName();
        final String email = profile.getEmail();
        final String uid = profile.getUid();

        User.init(displayName, email, uid);
    }

    /**/
    /*
    NAME

    initView - initializes view.

    SYNOPSIS

    private void initView();

    DESCRIPTION

    Will load the dashboard fragment on launch.

    RETURNS

    N/A
    */
    /**/
    private void initView() {
        // Load dashboard in container
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_container, new DashboardFragment())
                    .commit();
        }

        // Select dashboard in nav
        BottomNavigationView navigation = findViewById(R.id.main_navigation);
        navigation.setOnNavigationItemSelectedListener(this);
    }

    /**/
    /*
    NAME

    loadFragment - loads fragment.

    SYNOPSIS

    private boolean loadFragment(Fragment fragment);
    fragment--> the fragment to be loaded into the main container.

    DESCRIPTION

    Will load the specified fragment into the main container.

    RETURNS

    Returns a boolean value depending on whether or not the fragment was successfully loaded.
    */
    /**/
    private boolean loadFragment(Fragment fragment) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
        if (fragment != null && !currentFragment.getClass().equals(fragment.getClass())) {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    /**/
    /*
    NAME

    addFragment - adds fragment.

    SYNOPSIS

    public void addFragment(Fragment fragment, Bundle bundle);
    fragment--> the fragment to be added onto the main container.
    bundle--> the data to be passed to the fragment.

    DESCRIPTION

    Will add the specified fragment onto the loaded fragment, passing along the required data.
    Differs from loadFragment in that the new fragment is added on TOP of the loaded fragment,
    rather than replacing it in the main container.

    RETURNS

    N/A
    */
    /**/
    public void addFragment(Fragment fragment, Bundle bundle) {
        if (fragment != null && bundle != null) {
            fragment.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    /**/
    /*
    NAME

    hideKeyboard - hides keyboard.

    SYNOPSIS

    public void hideKeyboard();

    DESCRIPTION

    Will hide the keyboard from view.

    RETURNS

    N/A
    */
    /**/
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(MainActivity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }
}
