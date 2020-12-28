package com.github.leeonky.jfactory.cucumber.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(name = "t_products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String color;
}

