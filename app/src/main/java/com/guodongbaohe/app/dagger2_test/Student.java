package com.guodongbaohe.app.dagger2_test;

import javax.inject.Inject;

public class Student {

    private String msg = "我是dagger2";

    @Inject
    public Student() {
    }

    public String showMessage() {
        return msg;
    }

}
