package com.github.leeonky.jfactory.cucumber.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String color;
    private String camelCaseName;

    @OneToMany(mappedBy = "product")
    private List<ProductStock> stocks = new ArrayList<>();

    @ManyToOne
    private Category category;
}

