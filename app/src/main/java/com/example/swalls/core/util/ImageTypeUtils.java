package com.example.swalls.core.util;

import android.graphics.BitmapFactory;
import android.text.TextUtils;

public class ImageTypeUtils {

    public static String getImageMimeType(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        return options.outMimeType;
    }

    public static String getImageTinyType(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        String type = options.outMimeType;
        if (TextUtils.isEmpty(type)) {
            type = "未能识别的图片";
        } else {
            type = type.substring(6, type.length());
        }
        return type;
    }
}
