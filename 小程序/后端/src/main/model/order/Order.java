package com.example.demo.model.order;

import lombok.Data;

import java.sql.Date;

@Data
public class Order {
    private String id;

    private String userOpenid;

    private Integer state;

    private Integer money;

    private Date createTime;

    private Date updateTime;
}
