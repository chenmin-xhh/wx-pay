package com.example.wx_pay.model.Pay;

import lombok.Data;

@Data
/*下单接口参数实体类*/
public class OrderEntity {
    private Integer id;

    private String body;

    private Integer state;

    private String mchId;

    private String nonceStr;

    private String sign;

    private String outTradeNo;

    private double totalFee;

    private String tradeType;

    private String spbillCreateIp;

    private String openid;

    private String notifyUrl;

    private String orderId;

    public OrderEntity() {
    }

    public OrderEntity(Integer id, String body, Integer state, String mchId, String nonceStr, String sign, String outTradeNo, double totalFee, String tradeType, String spbillCreateIp, String openid, String notifyUrl, String orderId) {
        this.id = id;
        this.body = body;
        this.state = state;
        this.mchId = mchId;
        this.nonceStr = nonceStr;
        this.sign = sign;
        this.outTradeNo = outTradeNo;
        this.totalFee = totalFee;
        this.tradeType = tradeType;
        this.spbillCreateIp = spbillCreateIp;
        this.openid = openid;
        this.notifyUrl = notifyUrl;
        this.orderId = orderId;
    }
}
