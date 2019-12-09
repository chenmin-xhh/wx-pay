package com.example.wx_pay.model.user;

import lombok.Data;

@Data
public class User {
    private String unionid;

    private String nickname;

    private String cover;

    public User() {
    }

    public User(String unionid, String nickname, String cover) {
        this.unionid = unionid;
        this.nickname = nickname;
        this.cover = cover;
    }
}
