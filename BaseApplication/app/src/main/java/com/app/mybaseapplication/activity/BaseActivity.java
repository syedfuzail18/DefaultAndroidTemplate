package com.app.mybaseapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.app.mybaseapplication.R;
import com.app.mybaseapplication.permissions.AbstractPermissionActivity;
import com.google.android.material.snackbar.Snackbar;

import static com.app.mybaseapplication.constants.AppConstants.KEY_BUNDLE_DATA;

/**
 * Created on 11-Oct-2020
 * author: Syed Fuzail
 * email: fuzail@imobisoft.co.uk
 */
public class BaseActivity extends AbstractPermissionActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.left_to_right_start, R.anim.left_to_right_end);
        setStatusBarColor();
    }

    /**
     * Method used to switch from current activity to other
     *
     * @param destinationActivity activity to open
     */
    public void switchActivity(Class<?> destinationActivity) {
        hideKeyBoard();

        startActivity(new Intent(this, destinationActivity));
    }

    /**
     * Method used to switch from current activity to other with data
     *
     * @param destinationActivity activity to open
     * @param bundle              data that carry to destination activity
     */
    public void switchActivity(Class<?> destinationActivity, Bundle bundle) {
        hideKeyBoard();
        Intent intent = new Intent(this, destinationActivity);
        intent.putExtra(KEY_BUNDLE_DATA, bundle);
        startActivity(intent);
    }
    /**
     * method used to starting another activity for result
     *
     * @param destinationActivity activity to open
     * @param requestCode         result code
     */
    public void switchActivityForResult(Class<?> destinationActivity, int requestCode) {
        hideKeyBoard();

        Intent intent = new Intent(this, destinationActivity);
        startActivityForResult(intent, requestCode);
    }

    /**
     * This method Switches the Fragment when any of drawerList item gets
     * clicked
     *
     * @param fragment
     * @param isAddedtoBackStack
     */
    public void switchFragment(Fragment fragment, boolean isAddedtoBackStack) {
        if (fragment != null) {
            FragmentTransaction fragTrans = getSupportFragmentManager().beginTransaction();
            fragTrans.replace(R.id.content_framelayout, fragment);
            if (isAddedtoBackStack) {
                fragTrans.addToBackStack(null);
            }
            fragTrans.commitAllowingStateLoss();
        }
    }

    public Fragment getCurrentFragment() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.content_framelayout);
        return currentFragment;
    }

    /*Method used to set status bar color...*/
    public void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.white));
        }
    }

    /*Method used to set status bar color...*/
    public void setStatusBarColorForMenu() {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.transparent_black));
        }
    }

    /**
     * Method to hide soft keyboard
     */
    public void hideKeyBoard() {
        // Check if no view has focus:
        try {
            View view = getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void showSnackBar(String message) {
        showSnackBar(SpannableString.valueOf(message));
    }


    /**
     * This method displays provided message on SnackBar
     *
     * @param message
     */
    public void showSnackBar(SpannableString message) {

        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, 3500);
        final View snackBarView = snackbar.getView();
        snackBarView.setPadding(0, (int) getResources().getDimension(R.dimen.minus_dp_8),
                0, (int) getResources().getDimension(R.dimen.minus_dp_8));

        snackBarView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        TextView textView = (TextView) snackBarView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        textView.setMaxLines(5);
        snackbar.show();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_to_left_start, R.anim.right_to_left_end);
    }
}
