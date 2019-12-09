// pages/order/order.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    orderData: [{}]
  },
  refund: function () {
    wx.request({
      url: "http://127.0.0.1:端口/pay/refund",
      success: (res) => {
        this.getOrder();
        console.info("退款成功：" +res);
      }
    })
  },
  pay: function (event) {
    console.log(event.currentTarget.dataset.id)
    wx.request({
      url: 'http://127.0.0.1:端口/pay?openid=openid&outTradeNo=' +event.currentTarget.dataset.id,
      method: "POST",
      success: (res) => {
        let data = res.data.data;
        console.log(data.id);
        wx.requestPayment({
          timeStamp: data.timeStamp,
          nonceStr: data.nonceStr,
          package: data.package,
          signType: data.signType,
          paySign: data.paySign,
          success: res => {
            this.getOrder();
            console.info("支付成功："+res);
          },
          fail: res => {
            console.info(res);
          }
        })
      },
      fail: (res) => {
        console.info(res);
      }
    })
  },
  getOrder:function(){
    wx.request({
      url: 'http://127.0.0.1:端口/orders/order',
      success: (res) => {
        this.setData({ orderData: res.data.data })
      },
      fail: (res) => {
        console.info(res);
      }
    })
  }


})
