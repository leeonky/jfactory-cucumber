package com.github.leeonky.jfactory.cucumber;

import com.github.leeonky.jfactory.DataRepository;
import com.github.leeonky.jfactory.JFactory;
import com.github.leeonky.jfactory.cucumber.spec.Orders;
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
            jFactory.register(Orders.订单.class);

            List<Object> list = jData.prepare("订单",
                    Table.create(asList(
                            new Row().set("customer", "James"),
                            new Row().set("customer", "Tomas")
                    )));

            assertThat(list).isEqualTo(persisted);
            assertThat(persisted).extracting("customer").containsExactly("James", "Tomas");
        }

        @Test
        void persist_object_with_default_properties_and_args() {
            jFactory.register(Orders.订单.class);

            List<Object> list = jData.prepare(2, "订单");

            assertThat(list).hasSize(2);
        }
    }

    @Nested
    class TransformShould {

        @Test
        void support_datatable_to_table() {
            Table table = jData.transform(DataTable.create(asList(
                    asList("key1", "key2"),
                    asList("value1", "value2")
            )));

            assertThat(table).hasSize(1);
            assertThat(table).extracting("key1").containsExactly("value1");
            assertThat(table).extracting("key2").containsExactly("value2");
        }

        @Test
        void support_json_array_to_table() throws IOException {
            Table table = jData.transform("[{" +
                    "\"customer\": \"James\"" +
                    "}]");

            assertThat(table).extracting("customer").containsExactly("James");
        }

        @Test
        void support_json_object_to_table() throws IOException {
            Table table = jData.transform("{" +
                    "\"customer\": \"James\"" +
                    "}");

            assertThat(table).extracting("customer").containsExactly("James");
        }

        @Test
        void support_yaml_array_to_table() throws IOException {
            Table table = jData.transform("- customer: James\n" +
                    "- customer: Tomas");

            assertThat(table).extracting("customer").containsExactly("James", "Tomas");
        }

        @Test
        void support_yaml_object_to_table() throws IOException {
            Table table = jData.transform("customer: James\n" +
                    "merchant: JD");

            assertThat(table).extracting("customer").containsExactly("James");
            assertThat(table).extracting("merchant").containsExactly("JD");
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
                jFactory.createAs(Orders.订单.class);

                jData.allShould("订单", ".size=1");
                assertThrows(AssertionError.class, () -> jData.allShould("订单", ".size=0"));
            }
        }

        @Nested
        class AssertOne {

            @Test
            void should_fetch_one_data_and_do_data_assert() {
                jFactory.spec(Orders.订单.class).property("customer", "Tom").create();

                jData.should("订单.customer[Tom]", ".customer='Tom'");
                assertThrows(AssertionError.class, () -> jData.should("订单.customer[Tom]", ".customer='Tomas'"));
            }

            @Test
            void should_raise_error_when_query_more_than_one_object() {
                jFactory.spec(Orders.订单.class).property("customer", "Tom").create();
                jFactory.spec(Orders.订单.class).property("customer", "Tom").create();

                assertThrows(IllegalStateException.class, () -> jData.should("订单.customer[Tom]", "true"));
            }
        }
    }
}