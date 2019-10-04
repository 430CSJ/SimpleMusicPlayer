package com.zyh.example.simplemusicplayer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class TestPermission {
    public static final int CODE_REQUEST_READ_EXTERNAL = 10;
    public static final int REQUEST_READ_EXTERNAL_SETTING = 11;
    public static final int OPEN_SYSTEM_DIALOG = 0;
    public static final int GOTO_SETTING = 1;

    public static boolean getPermission(String pPermission, Activity pActivity, int pMode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(pActivity, pPermission) != PackageManager.PERMISSION_GRANTED) {
                Log.d(pActivity.getPackageName(), pPermission + " permission is not granted.");
                if (pMode != GOTO_SETTING || ActivityCompat.shouldShowRequestPermissionRationale(pActivity, pPermission)) {
                    ActivityCompat.requestPermissions(pActivity, new String[]{pPermission}, CODE_REQUEST_READ_EXTERNAL);
                } else {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", pActivity.getPackageName(), null);
                    intent.setData(uri);
                    pActivity.startActivityForResult(intent, REQUEST_READ_EXTERNAL_SETTING);
                    return false;
                }
            }
        }
        return true;
    }
}
