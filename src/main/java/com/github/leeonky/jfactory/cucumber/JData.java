package com.github.leeonky.jfactory.cucumber;

import com.github.leeonky.dal.AssertResult;
import com.github.leeonky.dal.DataAssert;
import com.github.leeonky.jfactory.Builder;
import com.github.leeonky.jfactory.JFactory;
import com.github.leeonky.util.BeanClass;
import com.github.leeonky.util.PropertyReader;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.DataTableType;
import io.cucumber.java.DocStringType;
import io.cucumber.java.zh_cn.假如;
import io.cucumber.java.zh_cn.那么;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static com.github.leeonky.jfactory.cucumber.Table.create;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class JData {
    private final JFactory jFactory;
    private final DataAssert dataAssert = new DataAssert();

    public JData(JFactory jFactory) {
        this.jFactory = jFactory;
    }

    @假如("存在{string}：")
    @SuppressWarnings("unchecked")
    public <T> List<T> prepare(String traitsSpec, Table table) {
        return prepare(traitsSpec, table.flatSub());
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> prepare(String traitsSpec, Map<String, ?>... data) {
        return prepare(traitsSpec, asList(data));
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> prepare(String traitsSpec, List<Map<String, ?>> data) {
        return (List<T>) data.stream().map(map -> toBuild(traitsSpec).properties(map).create()).collect(toList());
    }

    @DocStringType
    public Table transform(String content) throws IOException {
        return create(content);
    }

    @DataTableType
    @SuppressWarnings("unchecked")
    public Table transform(DataTable dataTable) {
        return create((List) transposeAsNeeded(dataTable).asMaps());
    }

    private DataTable transposeAsNeeded(DataTable dataTable) {
        if (dataTable.cell(0, 0).startsWith("'")) {
            List<List<String>> data = dataTable.transpose().asLists().stream().map(ArrayList::new).collect(toList());
            List<String> columns = data.get(0);
            columns.set(0, columns.get(0).substring(1));
            dataTable = DataTable.create(data);
        }
        return dataTable;
    }

    @那么("所有{string}数据应为：")
    public void allShould(String spec, String docString) {
        report(dataAssert.assertData(queryAll(spec), docString));
    }

    private void report(AssertResult assertResult) {
        if (!assertResult.isPassed())
            throw new AssertionError(assertResult.getMessage());
    }

    @那么("{string}数据应为：")
    public void should(String specExpression, String docString) {
        report(dataAssert.assertData(query(specExpression), docString));
    }

    public <T> T query(String specExpression) {
        Collection<T> collection = queryAll(specExpression);
        if (collection.size() != 1)
            throw new IllegalStateException(String.format("Got %d object of `%s`", collection.size(), specExpression));
        return collection.iterator().next();
    }

    @SuppressWarnings("unchecked")
    public <T> Collection<T> queryAll(String specExpression) {
        Matcher matcher = Pattern.compile("([^\\.]*)\\.(.*)\\[(.*)\\]").matcher(specExpression);
        if (matcher.find())
            return (Collection<T>) jFactory.spec(matcher.group(1)).property(matcher.group(2), matcher.group(3)).queryAll();
        else
            return (Collection<T>) jFactory.spec(specExpression).queryAll();
    }

    @假如("存在{int}个{string}")
    @SuppressWarnings("unchecked")
    public <T> List<T> prepare(int count, String traitsSpec) {
        return IntStream.range(0, count).mapToObj(value -> (T) toBuild(traitsSpec).create()).collect(toList());
    }

    @假如("存在{string}的{string}：")
    @SuppressWarnings("unchecked")
    public <T> List<T> prepareAttachments(String specExpressionProperty, String traitsSpec, List<Map<String, ?>> data) {
        int index = specExpressionProperty.lastIndexOf('.');
        Object parent = query(specExpressionProperty.substring(0, index));
        String property = specExpressionProperty.substring(index + 1);
        BeanClass beanClass = BeanClass.create(parent.getClass());
        PropertyReader propertyReader = beanClass.getPropertyReader(property);
        List<T> attachments = prepare(traitsSpec, data);
        if (Collection.class.isAssignableFrom(propertyReader.getType().getType()))
            ((Collection) propertyReader.getValue(parent)).addAll(attachments);
        else
            beanClass.setPropertyValue(parent, property, attachments.get(0));
        jFactory.getDataRepository().save(parent);
        return attachments;
    }

    private Builder<Object> toBuild(String traitsSpec) {
        return jFactory.spec(traitsSpec.split(", |,| "));
    }

    //TODO prepare many to many
    //TODO support English colon
}
