package com.github.leeonky.jfactory.cucumber;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class TableTest {

    @Nested
    class Flat {

        @Test
        void should_fatten_sub_property() throws IOException {
            Table table = Table.create("" +
                    "customer:\n" +
                    "  name: Tom\n" +
                    "  age: 18\n" +
                    "");

            assertThat(table.flatSub()[0]).containsExactly(
                    entry("customer.name", "Tom"),
                    entry("customer.age", 18));
        }

        @Test
        void should_fatten_2level_sub_property() throws IOException {
            Table table = Table.create("" +
                    "customer:\n" +
                    "  account:\n" +
                    "    balance: 100" +
                    "");

            assertThat(table.flatSub()[0]).containsExactly(
                    entry("customer.account.balance", 100));
        }

        @Test
        void should_fatten_sub_collection_property() throws IOException {
            Table table = Table.create("" +
                    "customers:\n" +
                    "-\n" +
                    "  name: Tom\n" +
                    "  age: 18\n" +
                    "");

            assertThat(table.flatSub()[0]).containsExactly(
                    entry("customers[0].name", "Tom"),
                    entry("customers[0].age", 18));
        }

        @Test
        void should_fatten_2level_sub_collection_property() throws IOException {
            Table table = Table.create("" +
                    "stores:\n" +
                    "- customers:\n" +
                    "  -\n" +
                    "    name: Tom\n" +
                    "    age: 18\n" +
                    "");

            assertThat(table.flatSub()[0]).containsExactly(
                    entry("stores[0].customers[0].name", "Tom"),
                    entry("stores[0].customers[0].age", 18));
        }

        @Test
        void should_fatten_sub_property_with_spec_postfix() throws IOException {
            Table table = Table.create("" +
                    "customers:\n" +
                    "- _: (Customer)!\n" +
                    "  name: Tom\n" +
                    "- _: (Customer)\n" +
                    "  name: Tom\n" +
                    "- _: \n" +
                    "  name: Tom\n" +
                    "- _: Available Customer\n" +
                    "  name: Tom\n" +
                    "- _: Available Customer!\n" +
                    "  name: Tom\n" +
                    "");

            assertThat(table.flatSub()[0]).containsExactly(
                    entry("customers[0](Customer)!.name", "Tom"),
                    entry("customers[1](Customer).name", "Tom"),
                    entry("customers[2].name", "Tom"),
                    entry("customers[3](Available Customer).name", "Tom"),
                    entry("customers[4](Available Customer)!.name", "Tom")
            );
        }
    }
}