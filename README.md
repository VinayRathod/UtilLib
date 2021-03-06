 <a href='https://bintray.com/vinay/android/UtilLib?source=watch' alt='Get automatic notifications about new "UtilLib" versions'><img src='https://www.bintray.com/docs/images/bintray_badge_color.png'></a> 
 # UtilLib [ ![Download](https://api.bintray.com/packages/vinay/android/UtilLib/images/download.svg) ](https://bintray.com/vinay/android/UtilLib/_latestVersion)

Library contains Location, Run time Permission, Image chooser in Android Project

add a dependency in your application build.gradle file like this
``` 
dependencies {
    implementation "com.yudiz:vinay-utillib:{LATEST_VERSION}"
}
```


dialog with camera and gallery
and you can choose image from Camera directly without dialog with `ChooseType.REQUEST_CAPTURE_PICTURE`
or you can choose image from Gallery directly without dialog with `ChooseType.REQUEST_PICK_PICTURE`
```
UtilLib.getPhoto(mContext, ChooseType.REQUEST_ANY)
     .enqueue(new OnImageChooserListener() {
         @Override
         public void onImageChoose(String path) {
             Glide.with(MainActivity.this).load(new File(path)).into(iv);
         }
     });
```

Runtime Permission
```
UtilLib.getPermission(mContext, new String[]{ Manifest.permission.CAMERA })
    .enqueue(new PermissionResultCallback() {
        @Override
        public void onComplete(PermissionResponse permissionResponse) {
             Log.d("Tag", "Permission is "+(permissionResponse.isAllGranted() ? "Enable" : "Disable"));
        }
    });
```

get location from either GPS or Network
```
UtilLib.getLocationManager(mContext).getLocation(new OnLocationPickListener() {

    @Override
    public void onLocationChanged(Location location) {
        Log.d("Tag", ""lng:" + location.getLongitude() + " lat:" + location.getLatitude());
    }

    @Override
    public void onError(String error) {
        Log.d("Tag", "Location Error." + error);
    }
});
```

[![Analytics](https://ga-beacon.appspot.com/UA-136409797-3/VinayRathod/UtilLib)](https://github.com/VinayRathod)
