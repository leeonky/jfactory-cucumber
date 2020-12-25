package com.github.leeonky.jfactory.cucumber;

import com.github.leeonky.jfactory.JFactory;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.zh_cn.假如;

public class DataPreparation {
    private final JFactory jFactory;

    public DataPreparation(JFactory jFactory) {
        this.jFactory = jFactory;
    }

    @假如("存在{string}：")
    public void prepareList(String spec, DataTable dataTable) {
        dataTable.asMaps().forEach(list ->
                jFactory.spec(spec).properties(list).create());
    }
}
