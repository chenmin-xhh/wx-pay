package com.example.demo.model.res;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class BizResponse {
    private Object data;
    private Integer code;
    private String desc;
    private static Map<Integer, String> DESC_BY_CODE = new HashMap<>();

    static{
        DESC_BY_CODE.put(200,"成功");

    }

    public static BizResponse get(Object data) {
        final Integer code = 200;
        BizResponse bizResponse = new BizResponse();
        bizResponse.setData(data);
        bizResponse.setCode(code);
        bizResponse.setDesc(DESC_BY_CODE.get(code));
        return bizResponse;
    }

    public static BizResponse get(Integer code, Object data) {
        BizResponse bizResponse = new BizResponse();
        bizResponse.setData(data);
        bizResponse.setCode(code);
        bizResponse.setDesc(DESC_BY_CODE.get(code));
        return bizResponse;
    }
}
