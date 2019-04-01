package com.guodongbaohe.app.recyclerview_animation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;


/**
 * 右侧滑入动画
 */
public class SlideInRightAnimation implements BaseAnimation {


    @Override
    public Animator[] getAnimators(View view) {
        return new Animator[]{
                ObjectAnimator.ofFloat(view, "translationX", view.getRootView().getWidth(), 0)
        };
    }
}
