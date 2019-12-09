// pages/user/user.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    user: {}
  },



  fun: function () {
    wx.navigateTo({
      url: "/pages/order/order",
    })
  },

  /**
  * 生命周期函数--监听页面加载
  */
  onLoad: function (options) {
    let thiss = this;
    wx.login({
      success: res => {
        wx.request({
          url: 'http://127.0.0.1:端口/users/login?code=' + res.code,
          dataType: 'json',
          success: function (res) {
            wx.getUserInfo({
              success: res => {
                thiss.setData({
                  user: res.userInfo
                })
              }
            })
          },
          fail: function (res) { },
        })
      }
    })
  }

})
