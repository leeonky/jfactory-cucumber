package com.github.leeonky.jfactory.cucumber;

import com.github.leeonky.jfactory.DataRepository;
import com.github.leeonky.jfactory.JFactory;
import com.github.leeonky.jfactory.cucumber.entity.Product;
import com.github.leeonky.jfactory.cucumber.spec.Products;
import io.cucumber.datatable.DataTable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JDataTest {
    private List<Object> persisted = new ArrayList<>();
    private JFactory jFactory = new JFactory(new DataRepository() {
        @Override
        public <T> Collection<T> queryAll(Class<T> type) {
            return null;
        }

        @Override
        public void clear() {
        }

        @Override
        public void save(Object object) {
            persisted.add(object);
        }
    });
    private JData jData = new JData(jFactory);

    @Nested
    class PrepareShould {

        @Test
        void persist_object_list_with_spec_name_and_row_list() {
            jFactory.register(Products.商品.class);

            List<Object> list = jData.prepare("商品",
                    Table.create(asList(
                            new Row().set("name", "book"),
                            new Row().set("name", "bicycle")
                    )));

            assertThat(list).isEqualTo(persisted);
            assertThat(persisted).extracting("name").containsExactly("book", "bicycle");
        }

        @Test
        void persist_object_with_default_properties_and_args() {
            jFactory.register(Products.商品.class);

            List<Object> list = jData.prepare(2, "商品");

            assertThat(list).hasSize(2);
            assertThat(list).allMatch(Product.class::isInstance);
        }
    }

    @Nested
    class TransformShould {

        @Nested
        class SupportYaml {
            @Test
            void array_to_table() throws IOException {
                Table table = jData.transform("- name: book\n" +
                        "- name: bicycle");

                assertThat(table).extracting("name").containsExactly("book", "bicycle");
            }

            @Test
            void object_to_table() throws IOException {
                Table table = jData.transform("name: book\n" +
                        "color: red");

                assertThat(table).extracting("name").containsExactly("book");
                assertThat(table).extracting("color").containsExactly("red");
            }
        }

        @Nested
        class SupportJson {
            @Test
            void array_to_table() throws IOException {
                Table table = jData.transform("[{" +
                        "\"name\": \"book\"" +
                        "}]");

                assertThat(table).extracting("name").containsExactly("book");
            }

            @Test
            void object_to_table() throws IOException {
                Table table = jData.transform("{" +
                        "\"name\": \"book\"" +
                        "}");

                assertThat(table).extracting("name").containsExactly("book");
            }
        }

        @Nested
        class SupportDataTable {

            @Test
            void to_table() {
                Table table = jData.transform(DataTable.create(asList(
                        asList("key1", "key2"),
                        asList("value1", "value2")
                )));

                assertThat(table).hasSize(1);
                assertThat(table).extracting("key1").containsExactly("value1");
                assertThat(table).extracting("key2").containsExactly("value2");
            }
        }
    }

    @Nested
    class AssertionShould {
        private JFactory jFactory = new JFactory();
        private JData jData = new JData(jFactory);

        @Nested
        class AssertAll {

            @Test
            void should_fetch_all_data_and_do_data_assert() {
                jFactory.createAs(Products.商品.class);

                jData.allShould("商品", ".size=1");
                assertThrows(AssertionError.class, () -> jData.allShould("商品", ".size=0"));
            }
        }

        @Nested
        class AssertOne {

            @Test
            void should_fetch_one_data_and_do_data_assert() {
                jFactory.spec(Products.商品.class).property("name", "book").create();

                jData.should("商品.name[book]", ".name='book'");
                assertThrows(AssertionError.class, () -> jData.should("商品.name[book]", ".name='bicycle'"));
            }

            @Test
            void should_raise_error_when_query_more_than_one_object() {
                jFactory.spec(Products.商品.class).property("name", "book").create();
                jFactory.spec(Products.商品.class).property("name", "book").create();

                assertThrows(IllegalStateException.class, () -> jData.should("商品.name[book]", "true"));
            }
        }
    }
}