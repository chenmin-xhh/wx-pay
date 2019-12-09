package com.example.demo.controller.order;

import com.example.demo.mapper.OrderMapper;
import com.example.demo.model.order.Order;
import com.example.demo.model.res.BizResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    OrderMapper orderMapper;

    @GetMapping("/order")
    public BizResponse queryOrder(){
        List<Order> order = orderMapper.getOrder("");
        return BizResponse.get(200,order);
    }

    public boolean updateOrder(Integer state,String outTradeNo){
        return orderMapper.updateOrderState(state,outTradeNo);
    }
}
