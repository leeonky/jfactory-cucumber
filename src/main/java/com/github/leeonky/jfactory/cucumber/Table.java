package com.github.leeonky.jfactory.cucumber;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.leeonky.util.BeanClass;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.IntStream.range;

public class Table extends ArrayList<Map<String, ?>> {
    private static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper() {{
        configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        configure(JsonParser.Feature.ALLOW_TRAILING_COMMA, true);
    }};

    private static Function<String, Object> jsonDeserializer;

    static {
        resetJsonDeserializer();
    }

    public static void setJsonDeserializer(Function<String, Object> jsonDeserializer) {
        Table.jsonDeserializer = jsonDeserializer;
    }

    private static final ObjectMapper YAML_OBJECT_MAPPER = new ObjectMapper(new YAMLFactory()) {{
        configure(JsonParser.Feature.ALLOW_YAML_COMMENTS, true);
    }};

    @SafeVarargs
    public static Table create(Map<String, ?>... maps) {
        return create(asList(maps));
    }

    public static Table create(List<? extends Map<String, ?>> maps) {
        return maps.stream().map(LinkedHashMap::new).collect(toCollection(Table::new));
    }

    public static Table create(List<String> headers, List<?>... rows) {
        return create(headers, asList(rows));
    }

    public static Table create(List<String> headers, List<? extends List<?>> rows) {
        return create(rows.stream().map(row -> range(0, headers.size()).boxed().
                <LinkedHashMap<String, Object>>collect(LinkedHashMap::new,
                (map, i) -> map.put(headers.get(i), row.get(i)), LinkedHashMap::putAll)).collect(Collectors.toList()));
    }

    @SuppressWarnings("unchecked")
    public static Table create(String content) throws IOException {
        Object value = parse(content);
        return create(BeanClass.cast(value, List.class).orElseGet(() -> singletonList(value)));
    }

    private static Object parse(String content) throws IOException {
        try {
            return jsonDeserializer.apply(content);
        } catch (Exception e) {
            return YAML_OBJECT_MAPPER.readValue(content, Object.class);
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object>[] flatSub() {
        return stream().map(this::flat).toArray(Map[]::new);
    }

    public static void resetJsonDeserializer() {
        setJsonDeserializer(content -> {
            try {
                return JSON_OBJECT_MAPPER.readValue(content, Object.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private <T> Map<String, T> merge(Map<String, T> m1, Map<String, T> m2) {
        return new LinkedHashMap<String, T>() {{
            putAll(m1);
            putAll(m2);
        }};
    }

    @SuppressWarnings("unchecked")
    private Map<String, ?> flatSub(String key, Object value) {
        if (value instanceof Map)
            return combineKey(flat((Map<String, ?>) value), key, ".");
        else if (value instanceof List)
            return combineKey(flat((List<?>) value), key, "");
        else
            return singletonMap(key, value);
    }

    private Map<String, ?> combineKey(Map<String, ?> sub, String key, String dot) {
        Postfix postfix = new Postfix((String) sub.remove("_"));
        return sub.entrySet().stream().collect(LinkedHashMap::new,
                (map, e) -> map.put(key + postfix.apply() + dot + e.getKey(), e.getValue()), LinkedHashMap::putAll);
    }

    private Map<String, ?> flat(Map<String, ?> value) {
        return value.entrySet().stream().map(m -> flatSub(m.getKey(), m.getValue()))
                .reduce(new LinkedHashMap<>(), this::merge);
    }

    private Map<String, ?> flat(List<?> list) {
        Iterator<Integer> index = Stream.iterate(0, i -> i + 1).iterator();
        return list.stream().map(e -> flatSub("[" + index.next() + "]", e))
                .reduce(new LinkedHashMap<>(), this::merge);
    }

    private static class Postfix {
        private final String postfix;
        private boolean applied = false;

        public Postfix(String postfix) {
            this.postfix = postfix;
        }

        private String format(String postfix) {
            Matcher matcher = Pattern.compile("\\(?([^)!]*)\\)?(!?)").matcher(postfix);
            if (matcher.matches())
                return "(" + matcher.group(1) + ")" + matcher.group(2);
            throw new IllegalStateException("Invalid postfix: " + postfix);
        }

        public String apply() {
            return !applied && postfix != null && (applied = true) ? format(postfix) : "";
        }
    }
}
