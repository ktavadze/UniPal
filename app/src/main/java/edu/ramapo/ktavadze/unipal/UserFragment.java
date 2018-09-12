package edu.ramapo.ktavadze.unipal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

/**
 * UserFragment Class to implement the user view.
 */

public class UserFragment extends Fragment {
    private static final String TAG = "EventFragment";

    private View mView;

    public UserFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mView = inflater.inflate(R.layout.fragment_user, null);

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        getActivity().setTitle("User");

        addSignOutListener();

        displayUserData();
    }

    @Override
    public void onStop() {
        super.onStop();

        removeSignOutListener();
    }

    /**/
    /*
    NAME

    addSignOutListener - adds sign out listener.

    SYNOPSIS

    private void addSignOutListener();

    DESCRIPTION

    Will add the click listener to the sign out button.

    RETURNS

    N/A
    */
    /**/
    private void addSignOutListener() {
        // Add sign out listener
        final Button sign_out_button = mView.findViewById(R.id.sign_out_button);
        sign_out_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Sign out
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        Log.d(TAG, "addDeleteListener: Listener added");
    }

    /**/
    /*
    NAME

    removeSignOutListener - removes sign out listener.

    SYNOPSIS

    private boolean removeSignOutListener();

    DESCRIPTION

    Will remove the click listener from the sign out button.

    RETURNS

    N/A
    */
    /**/
    private void removeSignOutListener() {
        // Remove sign out listener
        final Button sign_out_button = mView.findViewById(R.id.sign_out_button);
        sign_out_button.setOnClickListener(null);

        Log.d(TAG, "removeDeleteListener: Listener removed");
    }

    /**/
    /*
    NAME

    displayUserData - displays user info.

    SYNOPSIS

    private void displayUserData();

    DESCRIPTION

    Will display the data for the current user.

    RETURNS

    N/A
    */
    /**/
    private void displayUserData() {
        final TextView user_name_text = mView.findViewById(R.id.user_name_text);
        final TextView user_email_text = mView.findViewById(R.id.user_email_text);
        final TextView user_uid_text = mView.findViewById(R.id.user_uid_text);

        user_name_text.setText(User.getDisplayName());
        user_email_text.setText(User.getEmail());
        user_uid_text.setText(User.getUid());
    }
}
