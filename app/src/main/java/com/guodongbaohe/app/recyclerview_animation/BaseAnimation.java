package com.guodongbaohe.app.recyclerview_animation;

import android.animation.Animator;
import android.view.View;

/**
 * 动画接口
 */
public interface  BaseAnimation {

    Animator[] getAnimators(View view);

}
