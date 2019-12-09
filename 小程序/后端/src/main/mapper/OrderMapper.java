package com.example.demo.mapper;

import com.example.demo.model.order.Order;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Mapper
public interface OrderMapper {

    @Select("SELECT * FROM `order` where user_openid=#{id};")
    public List<Order> getOrder(@RequestParam("id")String id);

    @Update("UPDATE `order` SET state = #{state} WHERE id = #{outTradeNo}")
    public boolean updateOrderState(@Param("state")Integer state, @Param("outTradeNo")String outTradeNo);
}
