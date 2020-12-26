package com.github.leeonky.jfactory.cucumber;

import com.github.leeonky.jfactory.JFactory;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.zh_cn.假如;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class DataPreparation {
    private final JFactory jFactory;

    public DataPreparation(JFactory jFactory) {
        this.jFactory = jFactory;
    }

    @假如("存在{string}：")
    public <T> List<T> prepareList(String spec, DataTable dataTable) {
        return prepareList(spec, dataTable.asMaps());
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> prepareList(String spec, List<Map<String, String>> data) {
        return (List<T>) data.stream().map(list ->
                jFactory.spec(spec).properties(list).create()).collect(toList());
    }
}
