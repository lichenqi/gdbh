package com.guodongbaohe.app.dagger2_test;

import dagger.Component;

@Component
public interface MainComponent {
    void inject(DaggerActvity daggerActvity);
}
