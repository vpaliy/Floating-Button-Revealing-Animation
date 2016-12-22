package com.vpaliy.library.revealingAnimator;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.support.design.widget.FloatingActionButton;

public interface RevealingListener {

     void onTranslationStart(FloatingActionButton fab, Animator animator);
     void onTranslationEnd(FloatingActionButton fab, Animator  animator);
     void onRevealingStart(FloatingActionButton fab, Animator animator);
     void onRevealingEnd(FloatingActionButton fab, Animator  animator);


}
