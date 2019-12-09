package com.example.wx_pay.controller;

import com.example.wx_pay.Mapper.OrderMapper;
import com.example.wx_pay.model.order.Order;
import org.springframework.*；

@CrossOrigin
@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    OrderMapper orderMapper;
    //获取所有订单
    @GetMapping
    public Object getOrder(){
        return orderMapper.getOrder();
    }
    //修改订单状态
    public boolean updateOrder(Integer state,String outTradeNo){
        return orderMapper.updateOrderState(state,outTradeNo);
    }
}
