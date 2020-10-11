package com.app.mybaseapplication.permissions;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.app.mybaseapplication.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

/**
 * Created on 11-Oct-2020
 * author: Syed Fuzail
 * email: fuzail@imobisoft.co.uk
 */
public abstract class AbstractPermissionActivity extends AppCompatActivity {


    private ArrayList<String> mPermissionsList = new ArrayList<>();
    private PermissionResult mPermissionResultCallback = null;
    private boolean mIsLocationMandatory = false;
    public Dialog mAlertDialogSettings;

    public void requestPermission(String permission, PermissionResult permissionResult) {
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add(permission);
        requestEach(arrayList, false, permissionResult);
    }

    public void requestEach(ArrayList<String> permissions, boolean isLocationMandatory, PermissionResult permissionResult) {
        if (permissions == null || permissions.size() == 0)
            return;

        mIsLocationMandatory = isLocationMandatory;
        mPermissionsList.clear();
        mPermissionsList = permissions;
        mPermissionResultCallback = permissionResult;

        ArrayList<String> disGrantedPermissionsLists = new ArrayList<>();
        ArrayList<String> rationalePermissionsList = new ArrayList<>();

        for (int i = 0; i < permissions.size(); i++) {
            if (ContextCompat.checkSelfPermission(this, permissions.get(i)) != PackageManager.PERMISSION_GRANTED)
                disGrantedPermissionsLists.add(permissions.get(i));
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions.get(i)))
                rationalePermissionsList.add(permissions.get(i));
        }

        if (disGrantedPermissionsLists.size() > 0) {
           /* if (rationalePermissionsList.size() > 0) {

                showRationaleMessage(getDenialPermissionsMessage(rationalePermissionsList), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(AbstractPermissionActivity.this, disGrantedPermissionsLists.toArray(new String[0]),
                                PermissionUtils.REQUEST_CODE_PERMISSIONS);
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendPermissionResult(disGrantedPermissionsLists, false, false);
                    }
                });
                return;
            }*/

            ActivityCompat.requestPermissions(this, disGrantedPermissionsLists.toArray(new String[0]),
                    PermissionUtils.REQUEST_CODE_PERMISSIONS);
            return;
        }

        sendPermissionResult(mPermissionsList, true, false);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case PermissionUtils.REQUEST_CODE_PERMISSIONS:
                ArrayList<String> deniedPermissions = new ArrayList<>();
                ArrayList<String> neverAskAgainPermissions = new ArrayList<>();
                boolean mIsAllPermissionsGranted = true;
                if(grantResults.length>0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mIsAllPermissionsGranted = false;
                            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i]))
                                deniedPermissions.add(permissions[i]);
                            else
                                neverAskAgainPermissions.add(permissions[i]);
                        }
                    }

                    if (!mIsAllPermissionsGranted) {
                        if (deniedPermissions.size() > 0)
                            ActivityCompat.requestPermissions(AbstractPermissionActivity.this, deniedPermissions.toArray(new String[0])
                                    , PermissionUtils.REQUEST_CODE_PERMISSIONS);
                            //showPermissionRationale(deniedPermissions);
                        else if (neverAskAgainPermissions.size() > 0) {
                            sendPermissionResult(mPermissionsList, false, true);
                            showOpenSettingsSnackBar(neverAskAgainPermissions);
                        }

                    } else
                        sendPermissionResult(mPermissionsList, true, false);
                }else {
                    sendPermissionResult(mPermissionsList, false, false);
                }
                break;
            default:
                break;
        }

    }


    private void showOpenSettingsSnackBar(ArrayList<String> neverAskAgainPermissions) {
        if (!mIsLocationMandatory) {
            Snackbar.make(findViewById(android.R.id.content),
                    getDenialPermissionsMessage(neverAskAgainPermissions),
                    Snackbar.LENGTH_INDEFINITE)
                    .setActionTextColor(ContextCompat.getColor(this, android.R.color.white))
                    .setAction("open settings", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openSystemSettings();
                        }
                    })
                    .show();
        } else {
            showAlertMessageDialog("Location Permission Denied","Please go to App Settings and turn on Location Service on.","Open Settings");
            //todo create location dialog here

        }


    }

    /**
     * Method used to open System Settings
     */
    private void openSystemSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivityForResult(intent, PermissionUtils.REQUEST_CODE_SETTINGS_ACTIVITY);
    }


    private boolean checkIfAllPermissionsGranted(ArrayList<String> permissions) {
        boolean mIsAllPermissionsGranted = true;
        for (int i = 0; i < permissions.size(); i++)
            if (ContextCompat.checkSelfPermission(this, permissions.get(i)) != PackageManager.PERMISSION_GRANTED)
                mIsAllPermissionsGranted = false;
        return mIsAllPermissionsGranted;
    }

    private void showRationaleMessage(String message,
                                      DialogInterface.OnClickListener okClickListener,
                                      DialogInterface.OnClickListener cancelClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission Denied");
        builder.setPositiveButton("Ok", okClickListener);
        builder.setNegativeButton("Cancel", cancelClickListener);
        builder.setMessage(message);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String getDenialPermissionsMessage(ArrayList<String> permissions) {

        String mRationaleMessage = "";

        if (PermissionUtils.isPermissionWithCustomMessage(permissions.get(0))) {
            mRationaleMessage = PermissionUtils.getPermissionForCustomMessage(permissions.get(0));
        } else if (!isSinglePermission()) {

            if (permissions.size() == 1 && permissions.get(0).equals(PermissionUtils.ACCESS_BACKGROUND_LOCATION)) {
                mRationaleMessage = PermissionUtils.BACKGROUND_LOCATION_MESSAGE;
            } else {

                String text = "Please grant access to ";

                for (int i = 0; i < permissions.size(); i++) {
                    String msg = PermissionUtils.getPermissionRationaleTag(permissions.get(i));
                    if (!TextUtils.isEmpty(msg)) {
                        if (TextUtils.isEmpty(mRationaleMessage))
                            mRationaleMessage = text + PermissionUtils.getPermissionRationaleTag(permissions.get(i));
                        else
                            mRationaleMessage = mRationaleMessage + ", " + PermissionUtils.getPermissionRationaleTag(permissions.get(i));
                    }

                }
            }
        } else
            mRationaleMessage = PermissionUtils.getPermissionRationaleMessage(permissions.get(0));


        return mRationaleMessage;
    }


    public interface PermissionResult {
        void onPermissionResult(PermissionModel permissionResult);
    }

    public class PermissionModel {
        public boolean isPermissionGranted = false;
        public boolean isPermissionDenied = false;
        public boolean isNeverAskAgain = false;
        ArrayList<String> requestedPermissions = null;
    }

    private void sendPermissionResult(ArrayList<String> permissions, Boolean isGrant, boolean isNeverAskAgain) {
        PermissionModel permissionModel = new PermissionModel();
        if (isGrant) {
            permissionModel.isPermissionGranted = true;
            permissionModel.isPermissionDenied = false;
            permissionModel.isNeverAskAgain = false;
        } else {
            permissionModel.isPermissionGranted = false;
            if (isNeverAskAgain) {
                permissionModel.isPermissionDenied = false;
                permissionModel.isNeverAskAgain = true;
            } else {
                permissionModel.isPermissionDenied = true;
                permissionModel.isNeverAskAgain = false;
            }
        }

        permissionModel.requestedPermissions = permissions;
        mPermissionResultCallback.onPermissionResult(permissionModel);
    }

    private boolean isSinglePermission() {
        return mPermissionsList == null || mPermissionsList.size() <= 1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PermissionUtils.REQUEST_CODE_SETTINGS_ACTIVITY:
                if (checkIfAllPermissionsGranted(mPermissionsList))
                    sendPermissionResult(mPermissionsList, true, false);
                else
                    sendPermissionResult(mPermissionsList, false, false);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPermissionResultCallback = null;
    }

    /**
     *
     * @param alertHeader
     * @param message
     * @param positiveButtonText
     */
    public void showAlertMessageDialog(String alertHeader, String message,
                                       String positiveButtonText) {

        /*
         * no need to re-initialized every time
         */
        if (mAlertDialogSettings == null) {
            mAlertDialogSettings = new Dialog(this, R.style.CustomDialogTheme);
            mAlertDialogSettings.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mAlertDialogSettings.setContentView(R.layout.alert_dialog_with_both_btns_horizontal);

            // mAlertDialog.setContentView(R.layout.alert_dialog_with_both_btns);
            mAlertDialogSettings.setCanceledOnTouchOutside(false);
            mAlertDialogSettings.setCancelable(false);
        }

        TextView header = mAlertDialogSettings.findViewById(R.id.header);

        TextView dialogMessage = mAlertDialogSettings.findViewById(R.id.message);
        if (alertHeader != null)
            header.setText(alertHeader);
        else {
            header.setVisibility(View.GONE);
            dialogMessage.setTextSize(16);
            dialogMessage.setTypeface(null, Typeface.BOLD);
        }


        if (message != null)
            dialogMessage.setText(message);
        TextView ok = mAlertDialogSettings.findViewById(R.id.positive_btn);
        ok.setText(positiveButtonText);
        if (positiveButtonText != null) {
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAlertDialogSettings.dismiss();
                    openSystemSettings();
                }
            });
        }

        /*
         * do not show if already showing
         */
        if (!mAlertDialogSettings.isShowing())
            mAlertDialogSettings.show();
    }
}
