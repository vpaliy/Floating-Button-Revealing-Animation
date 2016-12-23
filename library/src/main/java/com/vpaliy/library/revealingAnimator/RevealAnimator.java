package com.vpaliy.library.revealingAnimator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * Copyright (C) 2016 Vasyl Paliy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public  class RevealAnimator
        implements View.OnClickListener {

    private static final String TAG="RevealAnimator";
    private boolean DEBUG=false;

    /** View where all of  animations occur **/
    private View dependentLayout;

    private FloatingActionButton fab;

    /** Button container which covers the dependentLayout **/
    private FrameLayout fabContainer;

    /** Main Animator **/
    private ValueAnimator buttonAnimator;

    /** Listeners in order to add animators to the current animation **/
    private List<RevealingListener> listenerList;

    /** Listeners that will be called when the button animates to the previous state */
    private List<RevealingListener> backListenerList;

    /** Interpolator for animations **/

    private TimeInterpolator revealInter;
    private TimeInterpolator translationInter;


    private boolean isRevealed=false;

    private boolean reverseWay=false;

    private boolean  finished=true;

    /** Duration of the translation **/
    private int motionDuration;

    /** ScaleX and ScaleY animations duration **/
    private int revealingDuration;

    /**When the button goes underneath the container, it starts shifting down on offset **/
    private float offsetY;

    /** In order to prevent shuddering of the button when the container has shifted **/
    private boolean hasContainerShifted=false;

    /** Represents the start of the button scaling, regarding to the animation time  **/
    private float revealingInter=1.f;

    /** Type of the path (curve, line, etc. ) **/
    private int pathType;

    private String color=null;
    private int icon=Integer.MIN_VALUE;

    private int fabSize;
    private int margin;
    private int fabGravity;


    private RevealAnimator(Builder builder) {
        this.fabSize=builder.fabSize;
        this.revealingInter=builder.revealingInter;
        this.pathType=builder.pathType;
        this.margin=builder.margin;
        this.fabGravity=builder.fabGravity;
        this.motionDuration=builder.motionDuration;
        this.revealingDuration=builder.revealingDuration;
        this.revealInter=builder.revealInter;
        this.translationInter=builder.translateInter;
        this.icon=builder.fabIcon;
        this.color=builder.fabColor;
    }

    //only if you are sure that it will be the entire layout and it does not have any hidden parents( FrameLayout)
    private RevealAnimator(@NonNull View dependentLayout,
                     @NonNull FloatingActionButton fab, @NonNull Builder builder) {
        this(builder);
        this.fab=fab;
        this.dependentLayout=dependentLayout;
        fab.setOnClickListener(this);
    }

    private RevealAnimator(@NonNull FloatingActionButton fab,
                           @NonNull View dependentLayout,@NonNull Builder builder) {
        this(builder);
        this.fab=fab;
        this.dependentLayout=dependentLayout;
        if(dependentLayout.getParent()!=null) {
            determineFabLocation();
            initLayout();
        }
        this.fab.setOnClickListener(this);
    }

    private RevealAnimator(@NonNull View dependentLayout, @NonNull Builder builder) {
        this(builder);
        this.dependentLayout=dependentLayout;
        initFab();
    }


    private void initFab() {
        if(dependentLayout.getRootView()==null) {
            //TODO set at the bottom of the screen
        }else {

            initLayout();

            if(color!=null) {
                fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
            }

            if(icon!=Integer.MIN_VALUE) {
                fab.setImageResource(icon);
            }

            fab.setOnClickListener(this);
        }

    }

    private void initLayout() {
        try {
            Constructor<? extends ViewGroup.LayoutParams> constructor=dependentLayout.getLayoutParams().
                    getClass().getDeclaredConstructor(int.class,int.class);
            ViewGroup.LayoutParams cloneInstance=dependentLayout.getLayoutParams();
            ViewGroup.LayoutParams params = constructor.newInstance(cloneInstance.width,cloneInstance.height);
            FieldCopier.instance().copyFields(dependentLayout.getLayoutParams(),params);
            fabContainer = new FrameLayout(dependentLayout.getContext());
            fabContainer.setLayoutParams(params);
            fab = new FloatingActionButton(fabContainer.getContext());
            fabContainer.addView(fab, new FrameLayout.LayoutParams(
                    fabSize,fabSize,fabGravity));
            ViewGroup.MarginLayoutParams marginLayoutParams=(ViewGroup.MarginLayoutParams)
                    dependentLayout.getLayoutParams();
            ViewGroup.MarginLayoutParams layoutParams=(ViewGroup.MarginLayoutParams)(params);
            layoutParams.setMargins(marginLayoutParams.leftMargin,marginLayoutParams.topMargin,
                    marginLayoutParams.rightMargin,marginLayoutParams.bottomMargin);
            adjustContainer();
            ViewGroup viewGroup = (ViewGroup) (dependentLayout.getParent());
            viewGroup.addView(fabContainer);
        }catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void adjustButton() {
        FrameLayout.LayoutParams params=
                (FrameLayout.LayoutParams)(fab.getLayoutParams());
        switch (fabGravity) {
            case Gravity.TOP | Gravity.END:
            case Gravity.BOTTOM | Gravity.END:
                params.rightMargin = margin;
                break;
            case Gravity.TOP|Gravity.START:
            case Gravity.BOTTOM|Gravity.START:
                params.leftMargin =margin;
                break;
        }
        fab.setLayoutParams(params);
    }

    private void determineFabLocation() {
        if(fab!=null) {
            Rect currentLayoutLocation=new Rect();
            Rect currentButtonLocation=new Rect();
            fab.getGlobalVisibleRect(currentButtonLocation);
            dependentLayout.getGlobalVisibleRect(currentLayoutLocation);
            ViewGroup root = (ViewGroup) (dependentLayout.getParent());
            root.removeView(fab);
            fabSize=fab.getWidth();
            if(currentButtonLocation.top>currentLayoutLocation.top) {
                if(currentButtonLocation.top>currentLayoutLocation.centerY()) {
                    fabGravity=Gravity.BOTTOM;
                }else {
                    fabGravity=Gravity.CENTER_VERTICAL; //in this case the animation will not occur
                }
            }else {
                fabGravity=Gravity.TOP;
            }

            if((currentButtonLocation.left-fab.getWidth()/2)>=currentLayoutLocation.centerX()) {
                if((currentButtonLocation.left-fab.getWidth()/2)==currentLayoutLocation.centerX()) {
                    fabGravity |= Gravity.CENTER_HORIZONTAL;
                }else {
                    fabGravity|=Gravity.END;
                }
            }else {
                fabGravity|=Gravity.START;
            }
        }
    }

    private void adjustContainer() {
        ViewGroup.MarginLayoutParams params=
                (ViewGroup.MarginLayoutParams) (fabContainer.getLayoutParams());
        switch (fabGravity) {
            case Gravity.BOTTOM | Gravity.END:
            case Gravity.BOTTOM | Gravity.START: {
                offsetY = fabSize / 2f;
                boolean isRule = false;
                if (params instanceof RelativeLayout.LayoutParams) {
                    int[] rules=((RelativeLayout.LayoutParams) params).getRules();
                    if(rules[RelativeLayout.ABOVE]!=0 || rules[RelativeLayout.ALIGN_PARENT_BOTTOM]==-1) {
                        isRule=true;
                        params.bottomMargin-=fabSize/2f;

                    }
                }

                if (!isRule) {
                    params.topMargin += fabSize / 2f;
                }
                break;
            }

            case Gravity.TOP | Gravity.END:
            case Gravity.TOP | Gravity.START: {
                offsetY = -fabSize / 2f;
                boolean isRule = false;
                if (params instanceof RelativeLayout.LayoutParams) {
                    int[] rules=((RelativeLayout.LayoutParams) params).getRules();
                    if(rules[RelativeLayout.BELOW]!=0 || rules[RelativeLayout.ALIGN_PARENT_TOP]==-1) {
                        isRule=true;
                        params.topMargin-=fabSize/2f;

                    }
                }
                if (!isRule) {
                    params.bottomMargin += fabSize / 2f;
                }
                break;
            }
        }
        adjustButton();
        fabContainer.setLayoutParams(params);
    }

    private AnimatorListenerAdapter provideWithShiftBackListener(final boolean reverse) {
        return new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (backListenerList != null) {
                    for (RevealingListener listener : backListenerList) {
                        listener.onRevealingStart(fab, animation);
                    }
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setIcon(icon);
                if (backListenerList != null) {
                    for (RevealingListener listener : backListenerList) {
                        listener.onRevealingEnd(fab, animation);
                    }
                }
                AnimatorSet shiftAnimator = new AnimatorSet();
                shiftAnimator.playTogether(ObjectAnimator.ofFloat(fabContainer, View.TRANSLATION_Y, 0f),
                        ObjectAnimator.ofFloat(fab, View.TRANSLATION_Y, fab.getTranslationY() - offsetY));
                shiftAnimator.setDuration(0);
                shiftAnimator.removeAllListeners();
                shiftAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        transformBack(reverse);
                    }
                });
                shiftAnimator.start();

            }
        };
    }

    private void transformBack(boolean reverse) {
        isRevealed=false;
        if(!reverse) {
            backAnimation();
        }else {
            buttonAnimator.removeAllListeners();
            buttonAnimator.addListener(FINISH);
            buttonAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    if(backListenerList!=null) {
                        for (RevealingListener listener : backListenerList) {
                            listener.onTranslationStart(fab, animation);
                        }
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if(backListenerList!=null) {
                        for (RevealingListener listener : backListenerList) {
                            listener.onTranslationEnd(fab, animation);
                        }
                    }
                }
            });
            buttonAnimator.reverse();
        }

    }

    private AnimatorListenerAdapter provideWithBackListener(final boolean reverse) {
        return new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (backListenerList != null) {
                    for (RevealingListener listener : backListenerList) {
                        listener.onRevealingStart(fab, animation);
                    }
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (backListenerList != null) {
                    for (RevealingListener listener : backListenerList) {
                        listener.onRevealingEnd(fab, animation);
                    }
                }
                setIcon(icon);
                transformBack(reverse);
            }
        };
    }

    public void changeGravity(int gravity) {
        destroyContainer();
        this.fabGravity=gravity;
        initFab();

    }


    private void backAnimation() {
        if(pathType==PathType.CURVE) {
            hasContainerShifted = false;

            float startX = fab.getTranslationX();
            float startY = fab.getTranslationY();

            AnimatorPath path = new AnimatorPath();
            path.moveTo(startX, startY);
            if (!reverseWay) {
                path.curveTo(startX / 2f, startY, 0, startY / 2f, 0, 0);
            } else {
                path.curveTo(startX, startY / 2f, startX / 2f, 0, 0, 0);
            }
            ObjectAnimator back = ObjectAnimator.ofObject(this, "Location", new PathEvaluator(),
                    path.getPoints().toArray());
            back.setInterpolator(translationInter);
            back.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    if(backListenerList!=null) {
                        for(RevealingListener listener:backListenerList) {
                            listener.onTranslationStart(fab, animation);
                        }
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if(backListenerList!=null) {
                        for (RevealingListener listener : backListenerList) {
                            listener.onTranslationEnd(fab, animation);
                        }
                    }
                }
            });
            back.addListener(FINISH);
            back.setDuration(motionDuration);
            back.start();
        } else {
            isRevealed=false;
            buttonAnimator.removeAllListeners();
            buttonAnimator.addListener(FINISH);
            buttonAnimator.reverse();
        }

    }

    public void addBackListener(RevealingListener... listeners) {
        if(listeners!=null) {
            if (backListenerList == null) {
                backListenerList = new ArrayList<>();
            }
            backListenerList.addAll(Arrays.asList(listeners));
        }
    }

    public void hideIcon() {
        fab.setImageDrawable(null);
    }

    public void reverseHide() {
        if(finished) {
            hide(buttonAnimator==null);
        }
    }


    private void hide(boolean reverse) {
        if(finished) {
            hideIcon();
            isRevealed=false;
            finished=false;
            fab.animate().scaleX(1.f).scaleY(1.f)
                    .setListener(fabContainer != null ? provideWithShiftBackListener(reverse) : provideWithBackListener(reverse))
                    .setDuration(revealingDuration).start();
        }

    }

    public void hide() {
        hide(false);
    }

    private final AnimatorListenerAdapter FINISH=new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            finished=true;
        }
    };

    @Override
    public void onClick(View view) {
        if(finished && !isRevealed) {
            finished=false;
            hasContainerShifted = false;
            buttonAnimator = ObjectAnimator.ofObject(this, "Location",
                    new PathEvaluator(), PathProvider.provideWithPath(dependentLayout, fab, pathType, reverseWay).getPoints().toArray());

            buttonAnimator.setInterpolator(translationInter);
            buttonAnimator.setDuration(motionDuration);
            buttonAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    if(listenerList!=null) {
                        for (RevealingListener listener : listenerList) {
                            listener.onTranslationStart(fab, animation);
                        }
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if(!isRevealed) {
                        isRevealed = true;
                        performScaling();
                    }
                    if(listenerList!=null) {
                        for (RevealingListener listener : listenerList) {
                            listener.onTranslationEnd(fab, animation);
                        }
                    }
                }
            });
            buttonAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                private final float EPSILON = 0.00001F;

                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    if (!isRevealed) {
                        float currentFactor = (valueAnimator.getCurrentPlayTime() * 100f) / (valueAnimator.getDuration());
                        if (((currentFactor / 100f) - revealingInter) >= EPSILON) {
                            isRevealed = true;
                            performScaling();
                        }
                    }
                    // do it
                    if (fabContainer != null) {
                        if (Math.abs(fab.getTranslationY()) > fabSize / 2f) {
                            if (!hasContainerShifted) {
                                fabContainer.animate().setListener(null).translationY(-offsetY).setDuration(0).start();
                                hasContainerShifted = true;
                            }
                        }
                    }
                }
            });
            buttonAnimator.start();
        }

    }

    private void destroyContainer() {
        if(fabContainer!=null) {
            ViewGroup parent=(ViewGroup)(fabContainer.getParent());
            parent.removeView(fabContainer);
        }
    }

    public void addListener(RevealingListener ... listeners) {
        if(listeners!=null) {
            if(listenerList==null) {
                listenerList = new ArrayList<>();
            }
            listenerList.addAll(Arrays.asList(listeners));
        }
    }

    public View getDependentLayout() {
        return dependentLayout;
    }

    public RevealAnimator reverseWay(boolean reverseWay) {
        this.reverseWay=reverseWay;
        return this;
    }

    public void setIcon(int resource) {
        this.icon=resource;
        fab.setImageResource(resource);
    }

    public void setColor(String color) {
        if (color != null) {
            this.color = color;
            fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
        }
    }

    public void setTranslationInterpolator(TimeInterpolator interpolator) {
        this.translationInter=interpolator;
    }

    public void setRevealInterpolation(TimeInterpolator interpolator) {
        this.revealInter=interpolator;
    }

    public void setPathType(int pathType) {
        this.pathType=pathType;
    }

    public void setTranslationDuration(int duration) {
        this.motionDuration = duration;
    }

    public void setRevealingDuration(int duration) {
        this.revealingDuration=duration;
    }

    private void performScaling() {
        hideIcon();
        float byX=(float)(dependentLayout.getHeight())/(float)(dependentLayout.getWidth());
        float byY=1f/byX;
        final float SCALE_X=getScaleXFactor(byX);
        final float SCALE_Y=getScalyYFactor(byY);
        fab.animate().setDuration(revealingDuration).setStartDelay(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if(listenerList!=null) {
                    for (RevealingListener listener : listenerList) {
                        listener.onRevealingStart(fab, animation);
                    }

                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(listenerList!=null) {
                    for (RevealingListener listener : listenerList) {
                        listener.onRevealingEnd(fab, animation);
                    }
                }
                isRevealed=true;
                finished=true;
            }
        }).setInterpolator(revealInter)
                .scaleXBy(SCALE_X).scaleYBy(SCALE_Y).start();
    }

    public void setLocation(PathPoint pathPoint) {
        fab.setTranslationX(pathPoint.mX);
        if(hasContainerShifted) {
            fab.setTranslationY(pathPoint.mY + offsetY);
        }else {
            fab.setTranslationY(pathPoint.mY);
        }
    }

    public RevealAnimator setRevealInterval(float interval) {
        if(interval<0||interval>1.f) {
            throw new RuntimeException("Value supposed to be [0,1]");
        }
        this.revealingInter=interval;
        return this;
    }

    private float getScaleXFactor(float byX) {
        if(byX<1.f)
            byX=1.f;
        return byX*2f*((float)(dependentLayout.getWidth())/(fab.getWidth()));
    }

    private float getScalyYFactor(float byY) {
        if(byY<1.f)
            byY=1.f;
        return 2f*byY*((float)(dependentLayout.getHeight())/(fab.getHeight()));
    }

    public FloatingActionButton getActionButton() {
        return fab;
    }

    /**
     *   Builder approach is very useful in this case because it helps to initialize objects with multiple fields.
     *   It has two required fields (button gravity and motion type),
     *   and the rest of them are optional, so they have some default values.
     *
     *   Here are some examples how to use:
     *     Builder builder=new Builder(Gravity.TOP|Gravity.END,PathType.CURVED)
     *      .setMargin(16).setRevealDuration(1000).setRevealInterval(0.5f);
     *
     *     RevealAnimator animator=new Builder(Gravity.TOP|Gravity.END,PathType.CURVED)
     *     .setMargin(16).build(dependentLayoutInstance);
     */

    public static class Builder {

        private float revealingInter=1.f;
        private int pathType;
        private String fabColor=null;
        private int fabIcon;
        private int fabSize=ConvertUtils.dpToPx(56);
        private int margin=ConvertUtils.dpToPx(16);
        private int fabGravity;
        private int motionDuration=200;
        private int revealingDuration=300;
        private TimeInterpolator revealInter=new AccelerateInterpolator();
        private TimeInterpolator translateInter=new DecelerateInterpolator();

        /** Constructor with two required fields */
        public Builder(int fabGravity, int pathType) {
            this.fabGravity=fabGravity;
            this.pathType=pathType;
        }

        public Builder setSize(int fabSize) {
            this.fabSize=ConvertUtils.dpToPx(fabSize);
            return this;
        }

        public Builder setGravity(int gravity) {
            this.fabGravity=gravity;
            return this;
        }

        public Builder setMargin(int margin) {
            this.margin=ConvertUtils.dpToPx(margin);
            return this;
        }

        public Builder setPathType(int pathType) {
            this.pathType=pathType;
            return this;
        }

        public Builder setRevealInterval(float revealingInter) {
            if(revealingInter<0||revealingInter>1.f) {
                throw new RuntimeException("Value supposed to be [0,1]");
            }
            this.revealingInter=revealingInter;
            return this;
        }

        public Builder setRevealInterpolator(TimeInterpolator interpolator) {
            this.revealInter=interpolator;
            return this;
        }

        public Builder setTranslateInterpolator(TimeInterpolator interpolator) {
            this.translateInter=interpolator;
            return this;
        }

        //Need to have another solution of this
        public Builder setColor(String color) {
            this.fabColor=color;
            return this;
        }

        public Builder setIcon(int fabIcon) {
            this.fabIcon=fabIcon;
            return this;
        }

        /** Method that creates instance of Builder based on the passed @FloatingActionButton */
        public static Builder createBuilder(@NonNull FloatingActionButton fab) {
            Builder builder=new Builder(-1,PathType.CURVE);
            builder.fabSize=fab.getHeight();
            ViewGroup.MarginLayoutParams layoutParams=(ViewGroup.MarginLayoutParams)(fab.getLayoutParams());
            if(layoutParams.leftMargin!=0) {
                builder.margin = layoutParams.leftMargin;
            }else {
                builder.margin = layoutParams.rightMargin;
            }
            return builder;
        }


        /** This method creates instance with default properties */
        public static Builder defaultBuilder() {
            return new Builder(Gravity.TOP | Gravity.END, PathType.CURVE);
        }

        /** Builds the instance of RevealAnimator */
        public RevealAnimator build(@NonNull View dependentLayout) {
            return new RevealAnimator(dependentLayout,this);
        }
    }


    public static RevealAnimator withRoot(@NonNull View dependentLayout, @NonNull FloatingActionButton fab) {
        return new RevealAnimator(dependentLayout,fab,Builder.createBuilder(fab));
    }


    public static RevealAnimator withRoot(@NonNull View dependentLayout,
                   @NonNull FloatingActionButton fab, @NonNull Builder builder) {
        return new RevealAnimator(dependentLayout,fab,builder);
    }

    /**
     * Basically, this is the same method as one above, I've added it only for convenience
     * @param builder
     *      object that holds all important data for animation process
     * @param dependentLayout
     *      layout where animation will occur
     * @return
     *      returns the instance of RevealAnimator
     */
    public static RevealAnimator withLayout(@NonNull Builder builder, @NonNull View dependentLayout) {
        return new RevealAnimator(dependentLayout,builder);
    }

    /**
     *
     * @param dependentLayout
     *      layout where animation will occur
     * @return
     *      returns the instance of RevealAnimator
     */

    public static RevealAnimator withLayout(@NonNull View dependentLayout) {
        return Builder.defaultBuilder().build(dependentLayout);
    }

    /**
     *
     * @param fab
     *      FAB that is specified by programmer
     * @param dependentLayout
     *     layout where animation will occur
     * @return
     *    returns the instance of RevealAnimator
     */
    public static RevealAnimator with(@NonNull FloatingActionButton fab,
                                      @NonNull View dependentLayout) {
        return new RevealAnimator(fab,dependentLayout,Builder.createBuilder(fab));
    }


    public static RevealAnimator with(@NonNull FloatingActionButton fab,
                                      @NonNull View dependentLayout, @NonNull Builder builder) {
        return new RevealAnimator(fab,dependentLayout,builder);
    }

}
