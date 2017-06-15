package org.allseen.lsf.sampleapp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by admin on 6/12/2017.
 */

public class MessageExceptionUtil {
    public static void NullPointerCause(Exception e, String Tag, String errorAtMethod) {
        Log.e(Tag, errorAtMethod + ": " + e.getMessage());
    }

    public static void NullPointerCause(Exception e, String mess, Context context) {
        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    public static void ExceptionCause(Exception e, String Tag, String errorAtMethod, String mess) {
        Log.e(Tag, mess + " :" + e.getMessage());
    }

    public static void ExceptionCause(Exception e, String Tag, String mess) {
        Log.e(Tag, mess + " :" + e.getMessage());
    }
}
