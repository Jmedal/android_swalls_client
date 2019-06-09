package com.example.swalls.core.data.converter;

import com.alibaba.fastjson.JSON;
import com.example.swalls.core.data.converter.entity.BaseTransferEntity;
import com.example.swalls.core.data.security.DataSecurityAction;
import com.example.swalls.core.data.security.impl.Base64SecurityAction;
import com.example.swalls.core.util.MD5Util;

import java.lang.reflect.Type;

public class MultipartHttpConverter{

    private final DataSecurityAction dataSecurityAction = new Base64SecurityAction();

    /**
     * 数据解密
     * @param o
     * @param s
     * @param t
     * @return
     */
    public Object read(String o,String s,Type t) {
        BaseTransferEntity baseTransferEntity = new BaseTransferEntity();
        baseTransferEntity.setObject(o);
        baseTransferEntity.setSign(s);

        String json = dataSecurityAction.unlock(baseTransferEntity.getObject());
        return JSON.parseObject(json, t);
    }

    /**
     * 数据加密
     * @param o
     * @param randomKey
     * @return
     */
    public BaseTransferEntity encryption(Object o, String randomKey){

        String jsonString = JSON.toJSONString(o);
        String encode = new Base64SecurityAction().doAction(jsonString);
        String md5 = MD5Util.encrypt(encode + randomKey);

        BaseTransferEntity baseTransferEntity = new BaseTransferEntity();
        baseTransferEntity.setObject(encode);
        baseTransferEntity.setSign(md5);

        return baseTransferEntity;
    }



}
