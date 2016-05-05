package com.vinay.utillib.ImageChooser;

import android.content.Context;

/**
 * Created by Farruxx on 30.04.2016.
 */
public class ChoosePhoto {
    public static PhotoRequest getPhoto(Context context, int type) {
        return new PhotoRequest(context, type);
    }
}
