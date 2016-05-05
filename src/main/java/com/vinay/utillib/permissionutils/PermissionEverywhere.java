package com.vinay.utillib.permissionutils;

import android.content.Context;

/**
 * Created by Farruxx on 30.04.2016.
 */
public class PermissionEverywhere {
    public static PermissionRequest getPermission(Context context, String[] permissions, int requestCode) {
        return new PermissionRequest(context, permissions, requestCode);
    }
}
