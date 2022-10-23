package com.github.leeonky.jfactory.cucumber;

import com.github.leeonky.jfactory.Builder;
import com.github.leeonky.jfactory.JFactory;
import com.github.leeonky.util.BeanClass;
import com.github.leeonky.util.Property;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.DataTableType;
import io.cucumber.java.DocStringType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.zh_cn.假如;
import io.cucumber.java.zh_cn.那么;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.leeonky.dal.Assertions.expect;
import static com.github.leeonky.jfactory.cucumber.Table.create;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

public class JData {
    private final JFactory jFactory;

    public JData(JFactory jFactory) {
        this.jFactory = jFactory;
    }

    @假如("存在{string}：")
    @假如("存在{string}:")
    @Given("Exists data {string}:")
    public <T> List<T> prepare(String traitsSpec, Table table) {
        return prepare(traitsSpec, table.flatSub());
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> prepare(String traitsSpec, Map<String, ?>... data) {
        return prepare(traitsSpec, asList(data));
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> prepare(String traitsSpec, List<? extends Map<String, ?>> data) {
        return (List<T>) data.stream().map(map -> toBuild(traitsSpec).properties(map).create()).collect(toList());
    }

    @DocStringType
    public Table transform(String content) throws IOException {
        return create(content);
    }

    @DataTableType
    public Table transform(DataTable dataTable) {
        if (needTranspose(dataTable))
            dataTable = DataTable.create(removeTransposeSymbol(dataTable));
        return create(dataTable.asMaps());
    }

    private List<List<String>> removeTransposeSymbol(DataTable dataTable) {
        List<List<String>> data = dataTable.transpose().asLists().stream().map(ArrayList::new).collect(toList());
        data.get(0).set(0, data.get(0).get(0).substring(1));
        return data;
    }

    private boolean needTranspose(DataTable dataTable) {
        return dataTable.cell(0, 0).startsWith("'");
    }

    @那么("所有{string}应为：")
    @那么("所有{string}应为:")
    @Then("All data {string} should be:")
    public <T> T allShould(String queryExpression, String dalExpression) {
        return assertData(queryAll(queryExpression), dalExpression);
    }

    @那么("{string}应为：")
    @那么("{string}应为:")
    @Then("Data {string} should be:")
    public <T> T should(String queryExpression, String dalExpression) {
        return assertData(query(queryExpression), dalExpression);
    }

    private <T> T assertData(Object instance, String dalExpression) {
        return expect(instance).get(dalExpression);
    }

    public <T> T query(String queryExpression) {
        return new QueryExpression(queryExpression).query();
    }

    public <T> Collection<T> queryAll(String queryExpression) {
        return new QueryExpression(queryExpression).queryAll();
    }

    @假如("存在{int}个{string}")
    @Given("Exists {int} data {string}")
    public <T> List<T> prepare(int count, String traitsSpec) {
        return prepare(traitsSpec, defaultProperties(count));
    }

    private List<Map<String, ?>> defaultProperties(int count) {
        return range(0, count).mapToObj(i -> new HashMap<String, Object>()).collect(toList());
    }

    @假如("存在{string}的{string}：")
    @假如("存在{string}的{string}:")
    @Given("Exists {string} as data {string}:")
    public <T> List<T> prepareAttachments(String beanProperty, String traitsSpec, Table table) {
        return prepareAttachments(beanProperty, traitsSpec, table.flatSub());
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> prepareAttachments(String beanProperty, String traitsSpec, Map<String, ?>... data) {
        return prepareAttachments(beanProperty, traitsSpec, asList(data));
    }

    public <T> List<T> prepareAttachments(String beanProperty, String traitsSpec, List<? extends Map<String, ?>> data) {
        return new BeanProperty(beanProperty).attach(prepare(traitsSpec, data));
    }

    @假如("存在{string}的{int}个{string}")
    @Given("Exists {string} as {int} data {string}")
    public <T> List<T> prepareAttachments(String beanProperty, int count, String traitsSpec) {
        return prepareAttachments(beanProperty, traitsSpec, defaultProperties(count));
    }

    @假如("存在如下{string}，并且其{string}为{string}：")
    @假如("存在如下{string}, 并且其{string}为{string}:")
    @Given("Exists following data {string}, and its {string} is {string}:")
    public <T> List<T> prepareAttachments(String traitsSpec, String reverseAssociationProperty, String queryExpression,
                                          Table table) {
        return prepareAttachments(traitsSpec, reverseAssociationProperty, queryExpression, table.flatSub());
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> prepareAttachments(String traitsSpec, String reverseAssociationProperty, String queryExpression,
                                          Map<String, ?>... data) {
        return prepareAttachments(traitsSpec, reverseAssociationProperty, queryExpression, asList(data));
    }

    public <T> List<T> prepareAttachments(String traitsSpec, String reverseAssociationProperty, String queryExpression,
                                          List<? extends Map<String, ?>> data) {
        return prepare(traitsSpec, addAssociationProperty(reverseAssociationProperty, queryExpression, data));
    }

    @假如("存在{int}个{string}，并且其{string}为{string}")
    @假如("存在{int}个{string}, 并且其{string}为{string}")
    @Given("Exists {int} data {string}, and its {string} is {string}")
    public <T> List<T> prepareAttachments(int count, String traitsSpec, String reverseAssociationProperty,
                                          String queryExpression) {
        return prepareAttachments(traitsSpec, reverseAssociationProperty, queryExpression, defaultProperties(count));
    }

    private List<Map<String, ?>> addAssociationProperty(String reverseAssociationProperty, String queryExpression,
                                                        List<? extends Map<String, ?>> data) {
        return data.stream().map(LinkedHashMap::new).peek(m -> m.put(reverseAssociationProperty, query(queryExpression)))
                .collect(toList());
    }

    private Builder<Object> toBuild(String traitsSpec) {
        return jFactory.spec(traitsSpec.split(", |,| "));
    }

    private class BeanProperty {
        private final Object bean;
        private Property property;

        public BeanProperty(String beanProperty) {
            int index = beanProperty.lastIndexOf('.');
            bean = query(beanProperty.substring(0, index));
            property = BeanClass.create(bean.getClass()).getProperty(beanProperty.substring(index + 1));
        }

        @SuppressWarnings("unchecked")
        private <T> List<T> attach(List<T> attachments) {
            if (Collection.class.isAssignableFrom(property.getReaderType().getType()))
                ((Collection<T>) property.getValue(bean)).addAll(attachments);
            else {
                if (attachments.size() != 1)
                    throw new IllegalStateException("More than one candidates");
                property.setValue(bean, attachments.get(0));
            }
            jFactory.getDataRepository().save(bean);
            return attachments;
        }
    }

    private class QueryExpression {
        private final String expression;
        private final String spec;
        private final Map<String, Object> properties = new LinkedHashMap<>();

        public QueryExpression(String expression) {
            Matcher matcher = Pattern.compile("([^.]*)\\.(.*)\\[(.*)]").matcher(expression);
            this.expression = expression;
            if (matcher.find()) {
                spec = matcher.group(1);
                properties.put(matcher.group(2), matcher.group(3));
            } else
                spec = expression;
        }

        @SuppressWarnings("unchecked")
        public <T> Collection<T> queryAll() {
            return (Collection<T>) jFactory.spec(spec).properties(properties).queryAll();
        }

        public <T> T query() {
            Collection<T> collection = queryAll();
            if (collection.size() != 1)
                throw new IllegalStateException(String.format("Got %d object of `%s`", collection.size(), expression));
            return collection.iterator().next();
        }
    }
}
