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
            mDatabase.addEventsListener();
            mDatabase.addCoursesListener();
            mDatabase.addSchoolsListener();

            initDashboard();

            BottomNavigationView navigation = findViewById(R.id.main_navigation);
            navigation.setOnNavigationItemSelectedListener(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        removeAuthListener();

        if (mCurrentUser != null) {
            mDatabase.removeEventsListener();
            mDatabase.removeCoursesListener();
            mDatabase.removeSchoolsListener();
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

    private void addAuthListener() {
        // Add auth state listener
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

    private void removeAuthListener() {
        // remove auth listener
        mAuth.removeAuthStateListener(mAuthListener);

        Log.d(TAG, "removeAuthListener: Listener removed");
    }

    private void initUser() {
        // Get Google provider data
        UserInfo profile = mCurrentUser.getProviderData().get(1);

        final String displayName = profile.getDisplayName();
        final String email = profile.getEmail();
        final String uid = profile.getUid();

        User.init(displayName, email, uid);
    }

    private void initDashboard() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_container, new DashboardFragment())
                    .commit();
        }
    }

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

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(MainActivity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }
}
