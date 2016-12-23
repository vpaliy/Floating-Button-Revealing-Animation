package com.example.revealexample;

import android.animation.Animator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.vpaliy.library.revealingAnimator.PathType;
import com.vpaliy.library.revealingAnimator.RevealAnimator;
import com.vpaliy.library.revealingAnimator.RevealingAdapterListener;

public class PlayerExample extends Fragment
            implements View.OnClickListener{

    private final int gravity=Gravity.TOP|Gravity.END;
    private RevealAnimator rAnimator;
    private View root;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.player_layout,container,false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.mode_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.topStart:
                onClick(null);
                rAnimator.changeGravity(Gravity.TOP|Gravity.START);
                return true;
            case R.id.topEnd:
                onClick(null);
                rAnimator.changeGravity(Gravity.END|Gravity.TOP);
                return true;
            case R.id.bottomStart:
                onClick(null);
                rAnimator.changeGravity(Gravity.START|Gravity.BOTTOM);
                return true;
            case R.id.bottomEnd:
                onClick(null);
                rAnimator.changeGravity(Gravity.END|Gravity.BOTTOM);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(final View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);
        this.root=root;
        if(root!=null) {
            ImageView coverImage=(ImageView)(root.findViewById(R.id.coverImage));
            Glide.with(getContext())
                    .load(R.drawable.imagine_cover)
                    .asBitmap().centerCrop()
                    .into(coverImage);
            ImageButton playButton=(ImageButton)(root.findViewById(R.id.playPause));
            playButton.setOnClickListener(this);
            initAnimator(root);
        }
    }

    private void initAnimator(final View root) {
        rAnimator=new RevealAnimator.Builder(gravity, PathType.CURVE)
                .setColor("#FF9100").setIcon(android.R.drawable.ic_media_play)
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

}
