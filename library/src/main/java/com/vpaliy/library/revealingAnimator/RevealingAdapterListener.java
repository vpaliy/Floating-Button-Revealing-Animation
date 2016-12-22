package com.vpaliy.library.revealingAnimator;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.support.design.widget.FloatingActionButton;

public  abstract class RevealingAdapterListener
                implements RevealingListener {

    @Override
    public void onTranslationEnd(FloatingActionButton fab, Animator animator) {

    }

    @Override
    public void onRevealingEnd(FloatingActionButton fab, Animator  animator) {

    }

    @Override
    public void onRevealingStart(FloatingActionButton fab, Animator  animator) {

    }

    @Override
    public void onTranslationStart(FloatingActionButton fab, Animator  animator) {

    }
}
