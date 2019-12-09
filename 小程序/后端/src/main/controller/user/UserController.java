package com.example.demo.controller.user;

import com.alibaba.fastjson.JSON;
import com.example.demo.model.res.BizResponse;
import org.springframework.*;

import java.util.HashMap;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/login")
    public BizResponse login(@RequestParam("code")String code){
        System.out.println(code);
        String url = String.format("https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&grant_type=authorization_code&js_code=%s", "AppID", "AppSecret", code);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> re = rt.getForEntity(url, String.class);
        HashMap userData = JSON.parseObject(re.getBody(), HashMap.class);
        System.out.println(userData);
        String openId = (String) userData.get("openid");
        System.out.println("openId:"+openId);
        return BizResponse.get(200,openId);
    }
}
