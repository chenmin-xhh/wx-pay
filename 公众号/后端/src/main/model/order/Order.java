package com.example.wx_pay.model.order;

import lombok.Data;

import java.sql.Date;

@Data
public class Order {
    private String id;

    private String userId;

    private Integer state;

    private Date createTime;

    private Date updateTime;
}
