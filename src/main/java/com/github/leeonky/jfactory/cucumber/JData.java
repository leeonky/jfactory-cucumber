package com.github.leeonky.jfactory.cucumber;

import com.github.leeonky.jfactory.JFactory;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.DataTableType;
import io.cucumber.java.DocStringType;
import io.cucumber.java.zh_cn.假如;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.github.leeonky.jfactory.cucumber.Table.create;
import static java.util.stream.Collectors.toList;

public class JData {
    private final JFactory jFactory;

    public JData(JFactory jFactory) {
        this.jFactory = jFactory;
    }

    @假如("存在{string}：")
    @SuppressWarnings("unchecked")
    public <T> List<T> prepare(String spec, Table table) {
        return prepare(spec, table.toArray(new Map[0]));
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> prepare(String spec, Map<String, ?>... data) {
        return (List<T>) Stream.of(data).map(map -> jFactory.spec(spec).properties(map).create()).collect(toList());
    }

    @DocStringType
    public Table transform(String content) throws IOException {
        return create(content);
    }

    @DataTableType
    public Table transform(DataTable dataTable) {
        return create(dataTable);
    }
}
