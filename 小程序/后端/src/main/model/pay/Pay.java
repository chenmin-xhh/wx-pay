package com.example.demo.model.pay;

import lombok.Data;

@Data
public class Pay {
    private Integer id;

    private String nonceStr;

    private String sign;

    private String body;

    private String mchId;

    private String notifyUrl;

    private String outTradeNo;

    private String openid;

    private Integer orderId;

    private String spbillCreateIp;

    private Integer totalFee;

    private String tradeType;

    private Integer state;

    public Pay() {
    }

    public Pay(Integer id, String nonceStr, String sign, String body, String mchId, String notifyUrl, String outTradeNo, String openid, Integer orderId, String spbillCreateIp, Integer totalFee, String tradeType, Integer state) {
        this.id = id;
        this.nonceStr = nonceStr;
        this.sign = sign;
        this.body = body;
        this.mchId = mchId;
        this.notifyUrl = notifyUrl;
        this.outTradeNo = outTradeNo;
        this.openid = openid;
        this.orderId = orderId;
        this.spbillCreateIp = spbillCreateIp;
        this.totalFee = totalFee;
        this.tradeType = tradeType;
        this.state = state;
    }
}
