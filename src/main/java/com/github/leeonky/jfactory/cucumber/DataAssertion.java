package com.github.leeonky.jfactory.cucumber;

import com.github.leeonky.dal.DataAssert;
import com.github.leeonky.jfactory.JFactory;
import io.cucumber.java.zh_cn.那么;

public class DataAssertion {
    private final JFactory jFactory;
    private final DataAssert dataAssert = new DataAssert();

    public DataAssertion(JFactory jFactory) {
        this.jFactory = jFactory;
    }

    @那么("所有{string}数据应为：")
    public void dataShould(String spec, String docString) {
        dataAssert.assertData(jFactory.spec(spec).queryAll(), docString);
    }
}
