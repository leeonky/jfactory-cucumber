package com.github.leeonky.jfactory.cucumber.factory;

import com.github.leeonky.jfactory.Spec;
import com.github.leeonky.jfactory.Trait;
import com.github.leeonky.jfactory.cucumber.entity.Product;

public class Products {
    public static class 商品 extends Spec<Product> {
        @Override
        public void main() {
            property("stocks").reverseAssociation("product");
        }

        @Trait
        public 商品 红色的() {
            property("color").value("red");
            return this;
        }
    }

    public static class ProductFactory extends Spec<Product> {

        @Override
        public void main() {
            property("stocks").reverseAssociation("product");
        }

        @Trait("Red")
        public ProductFactory red() {
            property("color").value("red");
            return this;
        }

        @Override
        protected String getName() {
            return "Product";
        }
    }
}
