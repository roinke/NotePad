package net.roink.notepad.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    public static void showLongToast(Context context, CharSequence llw) {
        Toast.makeText(context, llw, Toast.LENGTH_LONG).show();
    }

    public static void showShortToast(Context context, CharSequence llw) {
        Toast.makeText(context.getApplicationContext(), llw, Toast.LENGTH_SHORT).show();
    }
}
