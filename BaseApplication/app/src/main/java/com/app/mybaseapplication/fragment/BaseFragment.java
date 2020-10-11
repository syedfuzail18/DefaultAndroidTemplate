package com.app.mybaseapplication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;

import androidx.fragment.app.Fragment;

import com.app.mybaseapplication.activity.BaseActivity;

import static com.app.mybaseapplication.constants.AppConstants.KEY_BUNDLE_DATA;


/**
 * Created on 11-Oct-2020
 * author: Syed Fuzail
 * email: fuzail@imobisoft.co.uk
 */
public class BaseFragment extends Fragment {

    public void setUpToolBar(String title, boolean displayHomeButton, int icon, int currentScreen) {
        if (getActivity() == null) {
            return;
        }
    }

    /**
     * Method used to switch from current activity to other
     *
     * @param destinationActivity activity to open
     */
    public void switchActivity(Class<?> destinationActivity) {
        hideKeyBoard();
        startActivity(new Intent(getActivity(), destinationActivity));
    }

    /**
     * Method used to start another activity for result
     *
     * @param destinationActivity activity to open
     * @param requestCode         requestCode
     */
    public void switchActivityForResult(Class<?> destinationActivity, int requestCode) {
        hideKeyBoard();

        Intent intent = new Intent(getActivity(), destinationActivity);
        startActivityForResult(intent, requestCode);
    }

    /**
     * Method used to start another activity for result with data bundle
     *
     * @param destinationActivity activity to open
     * @param requestCode         request code
     * @param bundle              data bundle
     */
    public void switchActivityForResult(Class<?> destinationActivity, int requestCode, Bundle bundle) {
        hideKeyBoard();

        Intent intent = new Intent(getActivity(), destinationActivity);
        intent.putExtra(KEY_BUNDLE_DATA, bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, requestCode);
    }

    public void hideKeyBoard() {
        if (getActivity() == null) {
            return;
        }
        ((BaseActivity) getActivity()).hideKeyBoard();
    }



    public void switchFragment(Fragment fragment, boolean isAddedtoBackStack) {
        if (getActivity() != null)
            ((BaseActivity) getActivity()).switchFragment(fragment, isAddedtoBackStack);
    }

    public void showMessage(String message) {
        showMessage(SpannableString.valueOf(message));
    }

    public void showMessage(SpannableString message) {
        if (getActivity() == null) {
            return;
        }
        ((BaseActivity) getActivity()).showSnackBar(message);
    }

}
