package com.example.swalls.core.data.security.impl;

import com.example.swalls.core.data.security.DataSecurityAction;
import android.util.Base64;

/**
 * 对数据进行base64编码的方式
 *
 * @author fengshuonan
 * @date 2017-09-18 20:43
 */
public class Base64SecurityAction implements DataSecurityAction {

    @Override
    public String doAction(String beProtected) {
        return Base64.encodeToString(beProtected.getBytes(),Base64.NO_WRAP);
    }

    @Override
    public String unlock(String securityCode) {
        byte[] bytes = Base64.decode(securityCode,Base64.NO_WRAP);
        return new String(bytes);
    }
}
