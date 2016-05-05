package com.vinay.utillib.permissionutils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.ResultReceiver;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vinay.utillib.R;

/**
 * Created by Farruxx on 30.04.2016.
 */
public class PermissionActivity extends Activity {

    int requestCode;
    String[] permissions;
    int[] grantResults;
    private ResultReceiver resultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }
        if (getIntent() != null) {
            startPermission();
        } else {
            onComplete(requestCode, permissions, grantResults);
//            finish();
        }
    }

    private void startPermission() {
        resultReceiver = getIntent().getParcelableExtra(Const.RESULT_RECEIVER);
        String[] permissionsArray = getIntent().getStringArrayExtra(Const.PERMISSIONS_ARRAY);
        int requestCode = getIntent().getIntExtra(Const.REQUEST_CODE, Const.DEFAULT_CODE);
        if (!hasPermissions(permissionsArray)) {
            ActivityCompat.requestPermissions(this, permissionsArray, requestCode);
        } else {
            onComplete(requestCode, permissionsArray, new int[]{PackageManager.PERMISSION_GRANTED});
        }
    }

    private void onComplete(int requestCode, String[] permissions, int[] grantResults) {
        Bundle bundle = new Bundle();
        bundle.putStringArray(Const.PERMISSIONS_ARRAY, permissions);
        bundle.putIntArray(Const.GRANT_RESULT, grantResults);
        bundle.putInt(Const.REQUEST_CODE, requestCode);
        resultReceiver.send(requestCode, bundle);
        finish();

    }

    private boolean hasPermissions(String[] permissionsArray) {
        boolean result = true;
        for (String permission : permissionsArray) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                result = false;
                break;
            }
        }
        return result;
    }

    private void setComplete(int requestCode, String[] permissions, int[] grantResults) {
        this.requestCode = requestCode;
        this.permissions = permissions;
        this.grantResults = grantResults;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        setComplete(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (grantResults.length > 0)
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(PermissionActivity.this, permissions[0]);
                    String permission = permissions[0].replace("android.permission.", "");
                    if (!showRationale) {
                        showSettingDialog("Goto Settings > Apps > " + getString(R.string.app_name) + " > Permissions and allow " + permission + " permission.");
                    } else {
                        showPermissionDialog(permission + " Permission is needed.\nAre you sure you want to deny this permission?");
                    }
                } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onComplete(requestCode, permissions, grantResults);
                }
        }
    }

    public void showPermissionDialog(String msg) {
        final android.app.Dialog myDialog = new android.app.Dialog(this);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.dialog_permission);
        myDialog.setCancelable(false);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));

        myDialog.show();
        LinearLayout llOk = (LinearLayout) myDialog.findViewById(R.id.dialog_permission_ll_ok);
        LinearLayout llCancel = (LinearLayout) myDialog.findViewById(R.id.dialog_permission_ll_cancel);
        ((TextView) llOk.getChildAt(0)).setText("RE-TRY");
        ((TextView) llCancel.getChildAt(0)).setText("I'M SURE");

        TextView tvTitle = (TextView) myDialog.findViewById(R.id.dialog_permission_title);
        TextView tvText = (TextView) myDialog.findViewById(R.id.dialog_permission_text);
        tvText.setText("Permission denied");
        tvText.setText(msg);

        llOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myDialog.dismiss();
                startPermission();
                //requestPermissions(permissionPosition);
            }
        });
        llCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myDialog.dismiss();
                onComplete(requestCode, permissions, grantResults);
            }
        });
        myDialog.show();
    }

    public void showSettingDialog(String msg) {
        final android.app.Dialog myDialog = new android.app.Dialog(this);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.dialog_permission);
        myDialog.setCancelable(false);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));

        myDialog.show();
        LinearLayout llOk = (LinearLayout) myDialog.findViewById(R.id.dialog_permission_ll_ok);
        LinearLayout llCancel = (LinearLayout) myDialog.findViewById(R.id.dialog_permission_ll_cancel);
        ((TextView) llOk.getChildAt(0)).setText("Open Settings");
        ((TextView) llCancel.getChildAt(0)).setText(getString(android.R.string.cancel));

        TextView tvTitle = (TextView) myDialog.findViewById(R.id.dialog_permission_title);
        TextView tvText = (TextView) myDialog.findViewById(R.id.dialog_permission_text);
        tvText.setText("Permission denied");
        tvText.setText(msg);

        llOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myDialog.dismiss();
                startInstalledAppDetailsActivity(PermissionActivity.this);
            }
        });
        llCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myDialog.dismiss();
                onComplete(requestCode, permissions, grantResults);
            }
        });
        myDialog.show();
    }

    public void startInstalledAppDetailsActivity(final Context context) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }

}
