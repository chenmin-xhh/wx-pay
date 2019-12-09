package com.example.demo.controller.pay;

import com.example.demo.controller.order.OrderController;
import com.example.demo.model.pay.Pay;
import com.example.demo.model.res.BizResponse;
import com.example.demo.utils.Base64Util;
import com.example.demo.utils.MD5;
import com.example.demo.utils.XmlHelper;
import okhttp3.*;
import org.apache.*;
import org.springframework.*;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.*;

@RestController
@RequestMapping("/pay")
public class PayController {
    private static int orders = 100;
    private static Pay pay = null;

    private static final String ALGORITHM = "AES";
    /**
     * 加解密算法/工作模式/填充方式
     */
    private static final String ALGORITHM_MODE_PADDING = "AES/ECB/PKCS5Padding";
    /**
     * 生成key
     */
    private static SecretKeySpec key = new SecretKeySpec(MD5.MD5Encode("商户密钥").toLowerCase().getBytes(), ALGORITHM);



    @Autowired
    OrderController orderController;

/*    支付*/
    @PostMapping
    public BizResponse createOrder(@RequestParam("openid")String openid,@RequestParam("outTradeNo")String outTradeNo) throws IOException {
        System.out.println("生成预支付订单中。。。--------------------------------"+outTradeNo);
        String nonceStr = UUID.randomUUID().randomUUID().toString().replaceAll("-", "");
        String spbillCreateIp = "127.0.0.1";
        String bodys="用户订单支付";
        String mchId="商户号";
        String notifyUrl="支付回调地址";

        pay = new Pay(orders++, nonceStr, null, bodys, mchId, notifyUrl, outTradeNo, openid, orders, spbillCreateIp, 1, "JSAPI", null);

        OkHttpClient client = new OkHttpClient();
        Map<String, Object> map = new TreeMap<>();
        map.put("appid", "AppID");
        map.put("body", bodys);//商品信息
        map.put("mch_id", mchId);//商户号
        map.put("nonce_str", nonceStr);//随机字符串
        map.put("notify_url", notifyUrl);//微信服务器回调通知接口地址
        map.put("openid", openid);//用户标识
        map.put("spbill_create_ip", spbillCreateIp);//终端IP地址
        map.put("total_fee", 1);//总金额
        map.put("out_trade_no", outTradeNo);//商户订单号
        map.put("trade_type", "JSAPI");//交易类型
        String sin = sign(map);
        map.put("sign",sin);//签名

        pay.setSign(sin);

        System.out.println("map:"+map);
        System.out.println("pay:"+pay);
        // XML
        String xml = toXml(map);

        // 请求体
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/xml"), xml);
        // 构建请求
        Request request = new Request.Builder().url("https://api.mch.weixin.qq.com/pay/unifiedorder").post(requestBody).build();
        //发送请求
        Response response = client.newCall(request).execute();
        ResponseBody responseBody = response.body();
        String stringXml = responseBody.string();
        System.out.println("stringXml:"+stringXml);
        Map<String, String> xmlMap = XmlHelper.of(stringXml).toMap();

        // 支付配置
        Map<String, Object> packageParams = new TreeMap<>();
        packageParams.put("appId", "AppID");
        packageParams.put("timeStamp", System.currentTimeMillis()/1000 + "");//当前时间戳
        packageParams.put("nonceStr", UUID.randomUUID().toString().replaceAll("-", ""));//随机字符串
        packageParams.put("package", "prepay_id=" + xmlMap.get("prepay_id"));//预支付订单id
        packageParams.put("signType", "MD5");//加密方式
        packageParams.put("paySign", sign(packageParams));

        return BizResponse.get(200,packageParams);
    }

   /* 支付回调*/
   @PostMapping("/callback")
   public String orderCallback(@org.springframework.web.bind.annotation.RequestBody String content, HttpServletRequest request){
       XmlHelper xmlHelper = XmlHelper.of(content);
       Map<String, String> map = xmlHelper.toMap();
       System.out.println("支付回调"+map);

       System.out.println("支付id:"+map.get("out_trade_no"));
       String outTradeNo = map.get("out_trade_no");
       //支付后修改订单状态
       orderController.updateOrder(1,outTradeNo);

       Map result = new HashMap<>();
       result.put("return_code", "SUCCESS");
       result.put("return_msg", "OK");
       return toXml(result);
   }


    /*退款*/
    @GetMapping("/refund")
    public String tkPayment() throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, KeyManagementException {
        System.out.println("退款中。。。");
        System.out.println("退款的pay："+pay);

        Map<String, Object> map = new TreeMap<>();
        map.put("appid", "AppID");
        map.put("mch_id", pay.getMchId());//商户号
        map.put("nonce_str", UUID.randomUUID().toString().replaceAll("-", ""));//随机字符串
        map.put("out_refund_no", pay.getOutTradeNo());//商户退款单号
        map.put("out_trade_no", pay.getOutTradeNo());//商户订单号
        map.put("refund_fee", pay.getTotalFee());//退款金额
        map.put("total_fee", pay.getTotalFee());//订单金额
        map.put("notify_url", "");//退款结果通知url
        String signs = sign(map);
        map.put("sign",signs);//签名

        System.out.println("退款map:"+map);

        String xmlMap = toXml(map);

        File file = ResourceUtils.getFile("退款证书地址");

        InputStream is = new FileInputStream(file);
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(is, (pay.getMchId()+"").toCharArray());

        SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, pay.getMchId().toCharArray()).build();
        SSLConnectionSocketFactory connectionSocketFactory
                = new SSLConnectionSocketFactory(sslcontext, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
        CloseableHttpClient httpclient = HttpClients.custom()
                .setSSLSocketFactory(connectionSocketFactory)
                .build();

        HttpPost post = new HttpPost("https://api.mch.weixin.qq.com/secapi/pay/refund");
        StringEntity entity = new StringEntity(xmlMap);
        post.setEntity(entity);
        CloseableHttpResponse response = httpclient.execute(post);
        String result = EntityUtils.toString(response.getEntity(), "UTF-8");

        System.out.println("退款信息："+result);
        return null;
    }

   /* 退款回调*/
   @PostMapping("/refund/callBack")
   public String refundCallBack(@org.springframework.web.bind.annotation.RequestBody String content, HttpServletRequest request) throws Exception {
       System.out.println("退款回调："+content);
       XmlHelper xmlHelper = XmlHelper.of(content);
       Map<String,String>map = xmlHelper.toMap();
       System.out.println("map="+map);
       String reqInfo = map.get("req_info");
       String data = decryptData(reqInfo);
       System.out.println(data);

       Map<String, String> root = XmlHelper.of(data).toMap();
       String outTradeNo = root.get("out_trade_no");
       System.out.println(root);
       //退款后修改订单状态
       orderController.updateOrder(2,outTradeNo);

       Map result = new HashMap<>();
       result.put("return_code", "SUCCESS");
       result.put("return_msg", "OK");
       return toXml(result);
   }


    public static String toXml(Map<String, Object> params) {
        StringBuilder xml = new StringBuilder();
        xml.append("<xml>");
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = String.valueOf(entry.getValue());
            // 略过空值
            if (StringUtils.isEmpty(value))
                continue;
            xml.append("<").append(key).append(">");
            xml.append(entry.getValue());
            xml.append("</").append(key).append(">");
        }
        xml.append("</xml>");
        return xml.toString();
    }

    public String sign(Map<String, Object> params){
        Set<String> packageSet = params.keySet();
        StringBuffer sign = new StringBuffer();
        for (String param : packageSet) {
            if(param == null || param.trim().length() == 0) continue;
            if(sign.length() > 0){
                sign.append("&");
            }
            sign.append(param).append("=").append(params.get(param));
        }
        sign.append("&key=").append("商户密钥");
        return DigestUtils.md5Hex(sign.toString());
    }

    public static String decryptData(String base64Data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(Base64Util.decode(base64Data)));
    }

}
