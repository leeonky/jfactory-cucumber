package com.github.leeonky.jfactory.cucumber;

import io.cucumber.datatable.DataTable;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toCollection;

public class Table extends ArrayList<Row> {

    private Table() {
    }

    public static Table create(Row... rows) {
        Table table = new Table();
        table.addAll(asList(rows));
        return table;
    }

    public static Table create(DataTable dataTable) {
        return dataTable.asMaps().stream().map(Row::create).collect(toCollection(Table::new));
    }
}
