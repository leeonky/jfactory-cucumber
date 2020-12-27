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
                            Row.create().set("customer", "James"),
                            Row.create().set("customer", "Tomas")
                    )));

            assertThat(list).isEqualTo(persisted);
            assertThat(persisted).extracting("customer").containsExactly("James", "Tomas");
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
        void support_json_array_to_list_row() throws IOException {
            Table table = jData.transform("[{" +
                    "\"customer\": \"James\"" +
                    "}]");

            assertThat(table).extracting("customer").containsExactly("James");
        }

        @Test
        void support_json_object_to_list_row() throws IOException {
            Table table = jData.transform("{" +
                    "\"customer\": \"James\"" +
                    "}");

            assertThat(table).extracting("customer").containsExactly("James");
        }
    }
}