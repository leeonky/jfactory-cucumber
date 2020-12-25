package com.github.leeonky.jfactory.cucumber;

import io.cucumber.java.zh_cn.那么;

public class DataAssertStep {

    @那么("数据{string}应为：")
    public void dataShould(String spec, String docString) {
        throw new io.cucumber.java.PendingException();
    }
}
