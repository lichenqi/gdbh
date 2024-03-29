package com.guodongbaohe.app.recyclerview_animation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;


/**
 * 底部划入动画
 */
public class SlideInBottomAnimation implements BaseAnimation {

    public SlideInBottomAnimation(){

    }

    @Override
    public Animator[] getAnimators(View view) {
        return new Animator[]{
                ObjectAnimator.ofFloat(view, "translationY", view.getMeasuredHeight(), 0)
        };
    }
}
