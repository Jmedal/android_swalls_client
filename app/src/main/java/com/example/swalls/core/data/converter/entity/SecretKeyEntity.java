package com.example.swalls.core.data.converter.entity;

/**
 * 数据加密参数
 *
 * @author jyj
 * @date 2019-06-03 15:52
 */
public class SecretKeyEntity {

    private String token;       //签名令牌

    private String randomKey;   //盐值

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRandomKey() {
        return randomKey;
    }

    public void setRandomKey(String randomKey) {
        this.randomKey = randomKey;
    }
}
