package com.example.swalls.core.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileRequestUtils {

    public static File downFile(String URL, String fileName, String filePath){
        File file = null;
        try {
            URL url = new URL(URL);
            HttpURLConnection connection = (HttpURLConnection)
                    url.openConnection();
            //connection.setRequestMethod("GET");
            connection.setDoInput(true);
            //connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(500000);
            //实现连接
            connection.connect();
            if (connection.getResponseCode() == 200) {
                InputStream is = connection.getInputStream();
                //以下为下载操作
                byte[] arr = new byte[1];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                BufferedOutputStream bos = new BufferedOutputStream(baos);
                int n = is.read(arr);
                while (n > 0) {
                    bos.write(arr);
                    n = is.read(arr);
                }
                bos.close();
                //文件存放目录地址
                String dirPath = Environment.getExternalStorageDirectory() + "/download" + filePath;
                File dir = new File(dirPath);
                if(!dir.exists())
                    dir.mkdir();
                //文件存放地址
                String path = dirPath + "/" + fileName;
                file = new File(path);
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(baos.toByteArray());
                fos.close();
                //关闭网络连接
                connection.disconnect();
            }
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
        }
        return file;
    }

    public static void openPDF(Context context, String fileName, String filePath, String URL) {
        String path = Environment.getExternalStorageDirectory()
                + "/download" + filePath + "/" + fileName;
        File file = new File(path);
        //文件不存在，下载文件
        if (!file.exists())
            file = downFile(URL, fileName, filePath);
        //文件存在，显示文件
        if (file.exists()) {
            Log.d("FileRequestUtils","打开pdf");
            Uri path1 = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(path1, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            try {
                context.startActivity(intent);
            }
            catch (Exception e) {
                Log.d("打开失败","打开失败");
            }
        }
    }
}
