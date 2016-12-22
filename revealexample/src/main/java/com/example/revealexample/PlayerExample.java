package com.example.revealexample;

import android.animation.Animator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.vpaliy.library.revealingAnimator.PathType;
import com.vpaliy.library.revealingAnimator.RevealAnimator;
import com.vpaliy.library.revealingAnimator.RevealingAdapterListener;

public class PlayerExample extends Fragment{

    private int gravity;
    private RevealAnimator rAnimator;

    public static PlayerExample newInstance(int gravity) {
        Bundle args=new Bundle();
        args.putInt("gravity",gravity);
        PlayerExample example=new PlayerExample();
        example.setArguments(args);
        return example;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedInstanceState=getArguments();
        if(savedInstanceState!=null) {
            gravity = savedInstanceState.getInt("gravity");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.player_layout,container,false);
    }

    @Override
    public void onViewCreated(final View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);
        if(root!=null) {
            ImageView coverImage=(ImageView)(root.findViewById(R.id.coverImage));
            Glide.with(getContext())
                    .load(R.drawable.cover)
                    .asBitmap().centerCrop()
                    .into(coverImage);
            ImageButton playButton=(ImageButton)(root.findViewById(R.id.playPause));
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rAnimator.setIcon(android.R.drawable.ic_media_play);
                    ViewGroup internalContainer=(ViewGroup)(root.findViewById(R.id.container));
                    internalContainer.setBackgroundResource(R.color.musicPrimaryDark);
                    ViewGroup container=(ViewGroup)(root.findViewById(R.id.controllerContainer));
                    rAnimator.setRevealingDuration(0);  //very important
                    for(int index=0;index<container.getChildCount();index++) {
                        View child=container.getChildAt(index);
                        child.setVisibility(View.INVISIBLE);
                        child.animate().scaleY(0.f).scaleX(0.f)
                                .setStartDelay(index*50L).start();
                    }
                    rAnimator.addBackListener(new RevealingAdapterListener() {
                        @Override
                        public void onTranslationEnd(FloatingActionButton fab, Animator animator) {
                            super.onTranslationEnd(fab, animator);
                            rAnimator.setRevealingDuration(300);
                        }
                    });
                    rAnimator.getActionButton().setVisibility(View.VISIBLE);
                    rAnimator.hide();
                }
            });

            initAnimator(root);
        }
    }

    private void initAnimator(final View root) {
        rAnimator=new RevealAnimator.Builder(gravity, PathType.CURVE)
                .setColor("#009688").setIcon(android.R.drawable.ic_media_play)
                .build(root.findViewById(R.id.container));
        rAnimator.setRevealInterval(0.5f);


        rAnimator.addListener(new RevealingAdapterListener() {
            @Override
            public void onTranslationStart(FloatingActionButton fab, Animator animator) {
                super.onTranslationStart(fab, animator);
                rAnimator.setIcon(android.R.drawable.ic_media_pause);
            }

            @Override
            public void onRevealingEnd(FloatingActionButton fab, Animator animator) {
                super.onRevealingEnd(fab, animator);
                fab.setVisibility(View.INVISIBLE);
                ViewGroup internalContainer=(ViewGroup)(root.findViewById(R.id.container));
                ViewGroup container=(ViewGroup)(root.findViewById(R.id.controllerContainer));
                internalContainer.setBackgroundResource(R.color.musicPrimary);

                for(int index=0;index<container.getChildCount();index++) {
                    View child=container.getChildAt(index);
                    child.setVisibility(View.VISIBLE);
                    child.animate().scaleY(1.f).scaleX(1.f)
                            .setStartDelay(index*50L).start();
                }
            }
        });
    }

}
