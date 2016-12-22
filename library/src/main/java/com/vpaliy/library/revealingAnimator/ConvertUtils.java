package com.vpaliy.library.revealingAnimator;

import android.content.res.Resources;

public final class ConvertUtils {

    private ConvertUtils() {
        throw new RuntimeException();
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
