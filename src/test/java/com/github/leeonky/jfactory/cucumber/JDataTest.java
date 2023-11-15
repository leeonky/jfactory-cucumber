package com.github.leeonky.jfactory.cucumber;

import com.github.leeonky.jfactory.DataRepository;
import com.github.leeonky.jfactory.JFactory;
import com.github.leeonky.jfactory.cucumber.entity.Cart;
import com.github.leeonky.jfactory.cucumber.entity.Order;
import com.github.leeonky.jfactory.cucumber.entity.Product;
import com.github.leeonky.jfactory.cucumber.entity.ProductStock;
import com.github.leeonky.jfactory.cucumber.factory.Carts;
import com.github.leeonky.jfactory.cucumber.factory.Orders;
import com.github.leeonky.jfactory.cucumber.factory.ProductStocks;
import com.github.leeonky.jfactory.cucumber.factory.Products;
import io.cucumber.datatable.DataTable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JDataTest {
    private final List<Object> persisted = new ArrayList<>();
    private final JFactory jFactory = new JFactory(new DataRepository() {
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
    private final JData jData = new JData(jFactory);

    @Nested
    class PrepareShould {

        @Test
        void persist_object_list_with_spec_name_and_row_list() {
            jFactory.register(Products.商品.class);

            List<Object> list = jData.prepare("红色的 商品",
                    Table.create(
                            new HashMap<String, Object>() {{
                                put("name", "book");
                            }},
                            new HashMap<String, Object>() {{
                                put("name", "bicycle");
                            }}
                    ));

            assertThat(list).isEqualTo(persisted)
                    .extracting("name", "color").containsExactly(tuple("book", "red"), tuple("bicycle", "red"));
        }

        @Test
        void persist_object_with_default_properties_and_args() {
            jFactory.register(Products.商品.class);

            List<Object> list = jData.prepare(2, "商品");

            assertThat(list)
                    .hasSize(2)
                    .allMatch(Product.class::isInstance);
        }

        @Nested
        class Attachment {
            private final JFactory jFactory = new JFactory();
            private final JData jData = new JData(jFactory);

            @Test
            void support_create_sub_children_list() {
                jFactory.register(Products.商品.class);
                Cart cart = jFactory.spec(Carts.购物车.class).property("customer", "Tom").create();

                List<Product> products = jData.prepareAttachments("购物车.customer[Tom].products", "商品", asList(
                        new HashMap<String, Object>() {{
                            put("name", "book");
                        }}
                ));

                assertThat(cart.getProducts()).containsAll(products);
            }

            @Test
            void support_create_sub_child() {
                jFactory.register(Products.商品.class);
                Order order = jFactory.spec(Orders.订单.class).property("customer", "Tom").create();

                List<Product> products = jData.prepareAttachments("订单.customer[Tom].product", "商品", asList(
                        new HashMap<String, Object>() {{
                            put("name", "book");
                        }}
                ));

                assertThat(order.getProduct()).isEqualTo(products.get(0));
            }

            @Test
            void support_create_sub_default_children_list() {
                jFactory.register(Products.商品.class);
                Cart cart = jFactory.spec(Carts.购物车.class).property("customer", "Tom").create();

                List<Product> products = jData.prepareAttachments("购物车.customer[Tom].products", 1, "商品");

                assertThat(cart.getProducts()).containsAll(products);
            }

            @Test
            void support_create_reverse_associated_sub_children_list() {
                jFactory.register(ProductStocks.库存.class);
                Product product = jFactory.spec(Products.商品.class).property("name", "book").create();

                List<ProductStock> stocks = jData.prepareAttachments("库存", "product", "商品.name[book]", asList(
                        new HashMap<String, Object>() {{
                            put("size", "A4");
                        }}
                ));

                assertThat(stocks)
                        .hasSize(1)
                        .extracting("product").containsExactly(product);
            }

            @Test
            void support_create_reverse_associated_default_sub_children_list() {
                jFactory.register(ProductStocks.库存.class);
                Product product = jFactory.spec(Products.商品.class).property("name", "book").create();

                List<ProductStock> stocks = jData.prepareAttachments(1, "库存", "product", "商品.name[book]");

                assertThat(stocks)
                        .hasSize(1)
                        .extracting("product").containsExactly(product);
            }


            @Test
            void should_raise_error_when_give_more_than_one_bean_for_one_to_one_relation() {
                jFactory.register(Products.商品.class);
                jFactory.spec(Orders.订单.class).property("customer", "Tom").create();

                assertThrows(RuntimeException.class, () -> jData.prepareAttachments("订单.customer[Tom].product", "商品",
                        asList(new HashMap<String, Object>() {{
                                   put("name", "book");
                               }},
                                new HashMap<String, Object>() {{
                                    put("name", "bicycle");
                                }}
                        )));
            }
        }
    }

    @Nested
    class TransformShould {

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

                assertThat(table).hasSize(1)
                        .extracting("key1", "key2").containsExactly(tuple("value1", "value2"));
            }

            @Test
            void to_transpose_table() {
                Table table = jData.transform(DataTable.create(asList(
                        asList("'key1", "value1"),
                        asList("key2", "value2")
                )));

                assertThat(table).hasSize(1)
                        .extracting("key1", "key2").containsExactly(tuple("value1", "value2"));
            }
        }
    }

    @Nested
    class AssertionShould {
        private final JFactory jFactory = new JFactory();
        private final JData jData = new JData(jFactory);

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