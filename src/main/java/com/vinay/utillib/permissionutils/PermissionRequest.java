package com.vinay.utillib.permissionutils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.os.ResultReceiver;

/**
 * Created by Farruxx on 30.04.2016.
 */
public class PermissionRequest {
    Context context;
    String[] permissions;
    int requestCode;

    PermissionResponse response;

    public PermissionRequest(Context context, String[] permissions, int requestCode) {
        this.context = context;
        this.permissions = permissions;
        this.requestCode = requestCode;
    }

    public static void sendNotification(Context context, String[] permissions, int requestCode, ResultReceiver receiver) {
        Intent intent = new Intent(context, PermissionActivity.class);
        intent.putExtra(Const.REQUEST_CODE, requestCode);
        intent.putExtra(Const.PERMISSIONS_ARRAY, permissions);
        intent.putExtra(Const.RESULT_RECEIVER, receiver);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        context.startActivity(intent);
    }

    public PermissionResponse call() throws InterruptedException {
        if (!Util.hasPermission(context, permissions)) {
            final Object lock = new Object();
            sendNotification(context, permissions, requestCode, new ResultReceiver(new Handler(Looper.getMainLooper())) {
                @Override
                protected void onReceiveResult(int resultCode, Bundle resultData) {
                    super.onReceiveResult(resultCode, resultData);
                    int[] grantResult = resultData.getIntArray(Const.GRANT_RESULT);
                    String[] permissions = resultData.getStringArray(Const.PERMISSIONS_ARRAY);
                    response = new PermissionResponse(permissions, grantResult, resultCode);

                    synchronized (lock) {
                        lock.notifyAll();
                    }
                }
            });
            synchronized (lock) {
                lock.wait();
            }
        } else {
            response = new PermissionResponse(permissions, new int[]{PackageManager.PERMISSION_GRANTED}, requestCode);
        }
        return response;
    }

    public void enqueue(final PermissionResultCallback callback) {
        if (!Util.hasPermission(context, permissions)) {
            sendNotification(context, permissions, requestCode, new ResultReceiver(new Handler()) {
                @Override
                protected void onReceiveResult(int resultCode, Bundle resultData) {
                    super.onReceiveResult(resultCode, resultData);
                    int[] grantResult = resultData.getIntArray(Const.GRANT_RESULT);
                    String[] permissions = resultData.getStringArray(Const.PERMISSIONS_ARRAY);
                    response = new PermissionResponse(permissions, grantResult, resultCode);
                    callback.onComplete(new PermissionResponse(permissions, grantResult, resultCode));
                }
            });
        } else {
            callback.onComplete(new PermissionResponse(permissions, new int[]{PackageManager.PERMISSION_GRANTED}, requestCode));
        }
    }
}
