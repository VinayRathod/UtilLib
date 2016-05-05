package com.vinay.utillib.ImageChooser;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;

import com.vinay.utillib.permissionutils.Const;

/**
 * Created by Farruxx on 30.04.2016.
 */
public class PhotoRequest {
    Context context;
    int requestCode;

    public PhotoRequest(Context context, int requestCode) {
        this.context = context;
        this.requestCode = requestCode;
    }

    public static void sendNotification(Context context, int requestCode, ResultReceiver receiver) {
        Intent intent = new Intent(context, ChoosePhotoActivity.class);
        intent.putExtra(Const.REQUEST_CODE, requestCode);
        intent.putExtra(Const.RESULT_RECEIVER, receiver);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        context.startActivity(intent);
    }

    public void enqueue(final OnImageChooserListener callback) {
        sendNotification(context, requestCode, new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                super.onReceiveResult(resultCode, resultData);
                String grantResult = resultData.getString(Const.GRANT_RESULT);
                callback.onImageChoose(grantResult);
            }
        });
    }
}

