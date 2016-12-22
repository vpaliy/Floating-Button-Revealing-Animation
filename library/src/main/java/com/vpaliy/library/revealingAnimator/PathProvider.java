package com.vpaliy.library.revealingAnimator;

import android.graphics.Rect;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

public final class PathProvider {


    public static AnimatorPath provideWithPath(View dependentLayout,FloatingActionButton fab, int pathType, boolean reverse) {
        AnimatorPath path=new AnimatorPath();

        final float startX=fab.getTranslationX();
        final float startY=fab.getTranslationY();

        final Rect currentLayoutLocation=new Rect();
        final Rect currentButtonLocation=new Rect();
        dependentLayout.getGlobalVisibleRect(currentLayoutLocation);
        fab.getGlobalVisibleRect(currentButtonLocation);

        final float deltaX=currentLayoutLocation.centerX()-(currentButtonLocation.left+fab.getHeight()/2f);
        final float deltaY=currentLayoutLocation.centerY()-(currentButtonLocation.top+fab.getHeight()/2f);

        switch (pathType) {

            case PathType.CURVE: {
                path.moveTo(startX,startY);
                if(!reverse) {
                    float point0Y=deltaY/2f;
                    float point1X=deltaX/2f;
                    path.curveTo(startX,point0Y,point1X,deltaY,deltaX,deltaY);
                }else {
                    float point0X = deltaX / 2f;
                    float point1Y = deltaY / 2f;
                    path.curveTo(point0X,startY,deltaX,point1Y,deltaX,deltaY);
                }

                break;

            }

            case PathType.LINE: {

                path.moveTo(startX,startY);
                path.curveTo(startX,startY,startX,startY,deltaX,deltaY);
                break;
            }

        }


        return path;
    }

}
