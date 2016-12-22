package com.example.vasya.playerfabrevealing;

import android.animation.Animator;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.vpaliy.library.revealingAnimator.PathType;
import com.vpaliy.library.revealingAnimator.RevealAnimator;
import com.vpaliy.library.revealingAnimator.RevealingAdapterListener;

public class MainActivity extends AppCompatActivity {

    private RevealAnimator animator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_layout);

       final ImageView image = (ImageView) (findViewById(R.id.coverImage));

        Glide.with(this)
            .load(android.R.mipmap.sym_def_app_icon)
            .asBitmap()
            .centerCrop()
            .into(image);

        /*animator=RevealAnimator.withLayout(findViewById(R.id.container)).setRevealInterval(1.f);
        animator.addListener(new RevealingAdapterListener() {
            @Override
            public void onRevealingEnd(FloatingActionButton fab, Animator animator) {
                super.onRevealingEnd(fab, animator);
                fab.setVisibility(View.INVISIBLE);

                MainActivity.this.animator.setColor("#ffffff");
                MainActivity.this.animator.setIcon(android.R.drawable.ic_menu_camera);

                ViewGroup internalContainer=(ViewGroup)(findViewById(R.id.container));
                ViewGroup container=(ViewGroup)(findViewById(R.id.controllerContainer));
                internalContainer.setBackgroundResource(R.color.colorAccent);

                for(int index=0;index<container.getChildCount();index++) {
                    View child=container.getChildAt(index);
                    child.setVisibility(View.VISIBLE);
                    child.animate().scaleY(1.f).scaleX(1.f)
                        .setStartDelay(index*50L).start();
                }
            }
        });*/
        initAnimator();
    }

    private void initAnimator() {
        animator=new RevealAnimator.Builder(Gravity.TOP|Gravity.END, PathType.CURVE)
            .setColor("#009688").setIcon(android.R.drawable.ic_media_play).build(findViewById(R.id.container));

        animator.addListener(new RevealingAdapterListener() {
            @Override
            public void onRevealingEnd(FloatingActionButton fab, Animator animator) {
                super.onRevealingEnd(fab, animator);
                fab.setVisibility(View.INVISIBLE);
                ViewGroup internalContainer=(ViewGroup)(findViewById(R.id.container));
                ViewGroup container=(ViewGroup)(findViewById(R.id.controllerContainer));
                internalContainer.setBackgroundResource(R.color.colorAccent);

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
        ViewGroup internalContainer=(ViewGroup)(findViewById(R.id.container));
        ViewGroup container=(ViewGroup)(findViewById(R.id.controllerContainer));
        internalContainer.setBackgroundResource(R.color.musicPrimary);

        for(int index=0;index<container.getChildCount();index++) {
            View child=container.getChildAt(index);
            child.setVisibility(View.INVISIBLE);
            child.animate().scaleY(0.f).scaleX(0.f)
                    .setStartDelay(index*50L).start();
        }
        animator.getActionButton().setVisibility(View.VISIBLE);
        animator.hide();
    }
}
