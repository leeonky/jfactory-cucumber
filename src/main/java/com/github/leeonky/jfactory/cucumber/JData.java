package com.github.leeonky.jfactory.cucumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.leeonky.jfactory.JFactory;
import io.cucumber.datatable.DataTable;
import io.cucumber.docstring.DocString;
import io.cucumber.java.zh_cn.假如;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class JData {
    private final JFactory jFactory;

    public JData(JFactory jFactory) {
        this.jFactory = jFactory;
    }

    @假如("存在{string}：")
    @SuppressWarnings("unchecked")
    public <T> List<T> prepare(String spec, Object data) {
        try {
            if (data instanceof DataTable)
                return prepareList(spec, (List) ((DataTable) data).asMaps());
            if (data instanceof DocString)
                return prepareList(spec, toMap(((DocString) data).getContent()));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, ?>> toMap(String content) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Object value = objectMapper.readValue(content, Object.class);
        if (value instanceof List)
            return (List) value;
        return (List) singletonList(value);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> prepareList(String spec, List<Map<String, ?>> data) {
        return (List<T>) data.stream().map(map -> jFactory.spec(spec).properties(map).create()).collect(toList());
    }
}
