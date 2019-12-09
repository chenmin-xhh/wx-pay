import com.example.wx_pay.model.Pay.OrderEntity;
import com.example.wx_pay.model.order.Order;
import com.example.wx_pay.model.res.BizResponse;
import com.example.wx_pay.utils.Base64Util;
import com.example.wx_pay.utils.MD5;
import com.example.wx_pay.utils.XmlHelper;
import okhttp3.*;
import org.apache.http.*;
import org.springframework.*;
import javax.*;
import java.*;

@CrossOrigin
@RestController
@RequestMapping("/pay")
public class PayController {

    @Autowired
    OrderController orderController;
    private static final String ALGORITHM = "AES";
    /**
     * 加解密算法/工作模式/填充方式
     */
    private static final String ALGORITHM_MODE_PADDING = "AES/ECB/PKCS5Padding";
    /**
     * 生成key
     */
    private static SecretKeySpec key = new SecretKeySpec(MD5.MD5Encode("商户密钥").toLowerCase().getBytes(), ALGORITHM);


    //订单支付
    @PostMapping
    public BizResponse createOrder(@RequestBody Order order, HttpServletRequest request) throws IOException, NoSuchAlgorithmException {
        //32位随机数(UUID去掉-就是32位的)
        String nonceStr = UUID.randomUUID().toString().replace("-", "");
        //商户订单号
        String outTradeNo = order.getId().toString();
        //商品描述
        String body = "书籍订单支付";
        //订单id
        String  orderId = order.getId();
        //终端ip
        String spbillCreateIp = "127.0.0.1";
        //公众号支付
        String tradeType = "JSAPI";
        //商户号
        String mchId = "商户号";
        //金额
        double totalFee = 1;
        //回调地址
        String notifyUrl = "付款回调地址";
        //openid
        String openid = "";

        OrderEntity orderEntity = new OrderEntity(null,body,null,mchId,nonceStr,null,outTradeNo,totalFee,tradeType,spbillCreateIp,openid,notifyUrl,orderId);
        Map<String,Object>temp = new TreeMap<>();
        temp.put("appid","开发者ID");
        temp.put("body",body);
        temp.put("mch_id",mchId);
        temp.put("nonce_str",nonceStr);
        temp.put("notify_url",notifyUrl);
        temp.put("openid",openid);
        temp.put("spbill_create_ip",spbillCreateIp);
        temp.put("total_fee", 1);
        temp.put("out_trade_no",outTradeNo);
        temp.put("trade_type","JSAPI");
        //签名算法
        String sign = generateSign(temp);
        orderEntity.setSign(sign);

        System.out.println("配置:"+temp);

        temp.put("sign",sign);
        OkHttpClient client = new OkHttpClient();
        okhttp3.RequestBody response = okhttp3.RequestBody.create(MediaType.parse("text/xml"),toXml(temp));

        /*调用统一下单接口*/
        Request req = new Request.Builder().url("https://api.mch.weixin.qq.com/pay/unifiedorder").post(response).build();
        Response res = client.newCall(req).execute();
        String responseBody = res.body().string();

        XmlHelper xmlHelper = XmlHelper.of(responseBody);
        Map<String, String> stringStringMap = xmlHelper.toMap();

        System.out.println(temp);
        System.out.println(stringStringMap);

        TreeMap<String, String> objectObjectHashMap = new TreeMap<>();
        objectObjectHashMap.put("appId", "开发者ID");
        objectObjectHashMap.put("timeStamp", System.currentTimeMillis()/1000 + "");//时间戳
        objectObjectHashMap.put("nonceStr", System.currentTimeMillis() + "");
        objectObjectHashMap.put("package", "prepay_id=" + stringStringMap.get("prepay_id"));//预支付订单id
        objectObjectHashMap.put("signType", "MD5");
        objectObjectHashMap.put("paySign", generateSign(objectObjectHashMap));//签名

        return BizResponse.get(200, objectObjectHashMap);
    }

    //支付回调
    @PostMapping("/callback")
    public String orderCallback(@RequestBody String content,HttpServletRequest request){
        XmlHelper xmlHelper = XmlHelper.of(content);
        Map<String, String> map = xmlHelper.toMap();
        System.out.println("支付回调"+content);
      
        String outTradeNo = map.get("out_trade_no");
         //根据商户订单号修改订单状态（已支付）
        orderController.updateOrder('1',outTradeNo);

        Map result = new HashMap<>();
        result.put("return_code", "SUCCESS");
        result.put("return_msg", "OK");
        return toXml(result);
    }

    //退款
    @PostMapping("/refund")
    public BizResponse refund(@RequestBody HashMap data,HttpServletRequest request) throws IOException, NoSuchAlgorithmException, KeyStoreException, CertificateException, UnrecoverableKeyException, KeyManagementException {
        String orderId = data.get("id").toString();

        Map<String,Object>temp = new TreeMap<>();
        temp.put("appid","开发者ID");
        temp.put("mch_id","商户号");
        temp.put("nonce_str",UUID.randomUUID().toString().replace("-", ""));
        temp.put("out_refund_no",orderId);
        temp.put("out_trade_no",orderId);
        temp.put("refund_fee",1);
        temp.put("total_fee",1);
        temp.put("notify_url","退款回调地址");

        String sign = generateSign(temp);
        temp.put("sign",sign);
        String xmlMap = toXml(temp);

        File file = ResourceUtils.getFile("证书路径");

        InputStream is = new FileInputStream(file);
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        String mchId = "商户号";
        keyStore.load(is, mchId.toCharArray());

        SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, mchId.toCharArray()).build();
        SSLConnectionSocketFactory connectionSocketFactory
                = new SSLConnectionSocketFactory(sslcontext, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(connectionSocketFactory).build();

        HttpPost post = new HttpPost("https://api.mch.weixin.qq.com/secapi/pay/refund");
        StringEntity entity = new StringEntity(xmlMap);
        post.setEntity(entity);
        CloseableHttpResponse response = httpclient.execute(post);
        String result = EntityUtils.toString(response.getEntity(), "UTF-8");

        System.out.println("退款信息："+result);
        return null;

    }

    //退款回调
    @PostMapping("/refund/callBack")
    public String refundCallBack(@RequestBody String content,HttpServletRequest request) throws Exception {
        System.out.println("退款回调："+content);
        XmlHelper xmlHelper = XmlHelper.of(content);
        Map<String,String>map = xmlHelper.toMap();
        System.out.println("map="+map);
        String reqInfo = map.get("req_info");
        String data = decryptData(reqInfo);
        System.out.println(data);

        Map<String, String> root = XmlHelper.of(data).toMap();
        String outTradeNo = root.get("out_trade_no");
         //根据商户订单号修改订单状态（已退款）
        orderController.updateOrder(2,outTradeNo);

        Map result = new HashMap<>();
        result.put("return_code", "SUCCESS");
        result.put("return_msg", "OK");
        return toXml(result);
    }

    public String generateSign(Map<String, ?> paras) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        // 字符拼接
        StringBuffer sign = new StringBuffer();
        paras.forEach((k, v) -> sign.append(k).append("=").append(v).append("&"));
        sign.append("key=").append("商户密钥");
        // MD5
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] array = md.digest(sign.toString().getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString();
    }

    private static String toXml(Map<String, Object> params) {
        StringBuilder xml = new StringBuilder();
        xml.append("<xml>");
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key   = entry.getKey();
            String value = String.valueOf(entry.getValue());
            // 略过空值
            if (StringUtils.isEmpty(value)) continue;
            xml.append("<").append(key).append(">");
            xml.append(entry.getValue());
            xml.append("</").append(key).append(">");
        }
        xml.append("</xml>");
        return xml.toString();
    }

    public static String decryptData(String base64Data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(Base64Util.decode(base64Data)));
    }
}
