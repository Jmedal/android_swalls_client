package com.example.swalls.core.util;

import android.annotation.SuppressLint;
import java.util.Date;
import java.text.SimpleDateFormat;

public class DateUtil {

    @SuppressLint("SimpleDateFormat")
    public static String getWallNowDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return simpleDateFormat.format(new Date());
    }

    public static void main(String[] args) {
        System.out.println(getWallNowDate());
    }
}
