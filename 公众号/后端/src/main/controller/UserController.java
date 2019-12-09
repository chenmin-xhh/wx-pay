package com.example.wx_pay.controller;

import com.alibaba.fastjson.JSON;
import com.example.wx_pay.model.res.BizResponse;
import com.example.wx_pay.model.user.User;
import com.example.wx_pay.utils.UserUtils;
import org.springframework.*;
import javax.servlet.http.HttpServletRequest;
import java.*;

@CrossOrigin
@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/login")
    public BizResponse set(@RequestParam("code") String code, HttpServletRequest request) throws UnsupportedEncodingException {
        String token = request.getHeader("token");
        System.out.println("token:"+token);
        String appid = "开发者ID";
        String secret = "开发者密码";
        
        //获取access_token
        String url = String.format("https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code",appid,secret,code);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String>re = rt.getForEntity(url,String.class);
        HashMap userData = JSON.parseObject(re.getBody(),HashMap.class);
        String openId = (String) userData.get("openid");
        String accessToken = (String) userData.get("access_token");
        System.out.println(openId);

        //根据access_token获取用户信息
        String userInfoUrl = String.format("https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN", accessToken, openId);
        LinkedHashMap userInfoMap = JSON.parseObject(rt.getForObject(userInfoUrl, String.class), LinkedHashMap.class);
        String nickname = (String) userInfoMap.get("nickname");
        String headimgurl = (String) userInfoMap.get("headimgurl");
        nickname = new String(nickname.getBytes("ISO-8859-1"), "utf-8");
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        User us= new User(uuid,nickname,headimgurl);
        UserUtils.users.put(uuid, openId);
        return BizResponse.get(200, us);
    }
}
