package com.github.leeonky.jfactory.cucumber;

import com.github.leeonky.jfactory.DataRepository;
import com.github.leeonky.jfactory.JFactory;
import com.github.leeonky.jfactory.cucumber.spec.Orders;
import io.cucumber.datatable.DataTable;
import io.cucumber.docstring.DocString;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class JDataTest {

    @Nested
    class PrepareShould {
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
        private JData JData = new JData(jFactory);

        @Test
        void persist_object_list_with_spec_name_and_data_table() {
            jFactory.register(Orders.订单.class);

            List<Object> list = JData.prepare("订单", DataTable.create(asList(
                    asList("customer"),
                    asList("James"),
                    asList("Tomas")
            )));

            assertThat(list).isEqualTo(persisted);
            assertThat(persisted).extracting("customer").containsExactly("James", "Tomas");
        }

        @Test
        void persist_object_list_with_spec_name_and_json_array() {
            jFactory.register(Orders.订单.class);

            List<Object> list = JData.prepare("订单", DocString.create("[{" +
                    "\"customer\": \"James\"" +
                    "}]"));

            assertThat(list).isEqualTo(persisted);
            assertThat(persisted).extracting("customer").containsExactly("James");
        }

        @Test
        void persist_object_list_with_spec_name_and_json_object() {
            jFactory.register(Orders.订单.class);

            List<Object> list = JData.prepare("订单", DocString.create("{" +
                    "\"customer\": \"James\"" +
                    "}"));

            assertThat(list).isEqualTo(persisted);
            assertThat(persisted).extracting("customer").containsExactly("James");
        }
    }
}