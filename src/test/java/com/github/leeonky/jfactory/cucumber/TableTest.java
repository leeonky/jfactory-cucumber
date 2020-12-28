package com.github.leeonky.jfactory.cucumber;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class TableTest {

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
}