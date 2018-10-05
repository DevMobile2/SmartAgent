package com.ramesh.smartagent.Utils;

import android.app.Activity;
import android.widget.Toast;

public class Utils {
    public static void showErrorAlert(Activity mActivity, String message) {
        Toast.makeText(mActivity,message , Toast.LENGTH_SHORT).show();
    }
}
