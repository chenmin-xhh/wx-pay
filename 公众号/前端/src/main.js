import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import axios from "axios";
Vue.config.productionTip = false;
Vue.prototype.$http = axios;
//公众账号id
let appId = "";
let state = "";
let scope = "snsapi_userinfo";
let responseType = "code";
//授权登录后跳转的页面地址（/about）
let url = "";
//授权接口
let accreditUri = "https://open.weixin.qq.com/connect/oauth2/authorize";

axios.interceptors.response.use(
  response =>{
    //用户登录没有token时跳转
    window.console.info("response", response);
    if(response.data.code == 0 || response.data.code == 10000){
      localStorage.setItem("TOKEN",'');
      let accredit = `${accreditUri}?appId=${appId}&redirect_uri=${url}&response_type=${responseType}&scope=${scope}&state=${state}#wechat_redirect`;
      window.location.href = accredit;
    }
    return response;
  }
)
//拦截axios
axios.interceptors.request.use(request => {
  let token = localStorage.getItem("token") || "";
  if(token) {
    //将token写进header
    request.headers.common['token'] = token;
  }
  return request;
})

new Vue({
  router,
  render: h => h(App)
}).$mount("#app");
