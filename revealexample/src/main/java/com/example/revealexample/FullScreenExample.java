package com.example.revealexample;

import android.animation.Animator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.vpaliy.library.revealingAnimator.RevealAnimator;
import com.vpaliy.library.revealingAnimator.RevealingAdapterListener;

public class FullScreenExample extends Fragment {

    private RevealAnimator rAnimator;
    private RecyclerView recyclerView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.full_screen_layout,container,false);
    }


    @Override
    public void onViewCreated(final View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);
        if(root!=null) {
            FloatingActionButton fab=(FloatingActionButton)(root.findViewById(R.id.fab));
            rAnimator=RevealAnimator.withRoot(root,fab);
            rAnimator.setIcon(android.R.drawable.ic_menu_share);
            rAnimator.reverseWay(true);
            rAnimator.addListener(new RevealingAdapterListener() {
                @Override
                public void onRevealingEnd(FloatingActionButton fab, Animator animator) {
                    super.onRevealingEnd(fab, animator);
                    fab.setVisibility(View.INVISIBLE);
                    root.setBackgroundResource(R.color.colorAccent);
                    recyclerView=(RecyclerView)(root.findViewById(R.id.recyclerView));
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
                    recyclerView.setAdapter(new Adapter());
                }
            });

        }
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.ItemHolder> {

        private boolean[] hasAnimated;
        private LayoutInflater inflater=LayoutInflater.from(getContext());

        private int[] itemArray=new int[] {R.drawable.github,R.drawable.facebook,
                R.drawable.instagram,R.drawable.twitter,R.drawable.linkedin,R.drawable.skype};

        public Adapter() {
            hasAnimated = new boolean[getItemCount()];
        }

        public class ItemHolder extends RecyclerView.ViewHolder {

            private ImageView icon;

            public ItemHolder(View itemView) {
                super(itemView);
                icon=(ImageView)(itemView.findViewById(R.id.icon));
            }

            public void onBindData() {
                icon.setImageResource(itemArray[getAdapterPosition()]);
                if(!hasAnimated[getAdapterPosition()]) {
                    hasAnimated[getAdapterPosition()]=true;
                    icon.setScaleX(0.f);
                    icon.setScaleY(0.f);
                    icon.animate().scaleX(1.f).scaleY(1.f)
                        .setStartDelay(getAdapterPosition()*50L).start();
                }
            }
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {
            holder.onBindData();
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItemHolder(inflater.inflate(R.layout.item,parent,false));
        }

        @Override
        public int getItemCount() {
            return itemArray.length;
        }
    }
}
