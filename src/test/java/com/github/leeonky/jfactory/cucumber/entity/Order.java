package com.github.leeonky.jfactory.cucumber.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(name = "t_order")
public class Order {

    @Id
    public long id;

    public String customer;
}

