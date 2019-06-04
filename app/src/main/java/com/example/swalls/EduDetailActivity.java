package com.example.swalls;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.swalls.constant.Const;
import com.example.swalls.core.data.converter.MultipartHttpConverter;
import com.example.swalls.core.data.converter.entity.BaseTransferEntity;
import com.example.swalls.core.util.JsonUtils;
import com.example.swalls.modal.Edu;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EduDetailActivity extends AppCompatActivity {

    private final String url = Const.URL + "/auth/edu/selectById";//定义网络图片渎地址

    private TextView tv_title;

    private TextView tv_time;

    private TextView tv_content;

    private Thread mThread;

    //共享数据
    private SharedPreferences sharedPreferences;

    //Volley队列
    private RequestQueue request;

    private MultipartHttpConverter multipartHttpConverter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edu_detail);
        init();
        tv_title = (TextView)findViewById(R.id.edu_title);
        tv_time = (TextView)findViewById(R.id.edu_time);
        tv_content = (TextView)findViewById(R.id.edu_content);
        tv_content.setMovementMethod(ScrollingMovementMethod.getInstance());

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null)
            requestEduInfo(bundle.getLong("id"));


    }

    private void init() {
        //共享数据
        sharedPreferences = getSharedPreferences(Const.SECRET_KEY_DATEBASE,MODE_PRIVATE);
        //网络请求器
        request = Volley.newRequestQueue(EduDetailActivity.this);
        //数据转换器
        multipartHttpConverter = new MultipartHttpConverter();

    }


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 1:
                    Edu data = (Edu)msg.obj;
                    tv_title.setText(data.getEaTitle());
                    tv_time.setText(data.getEaTime());
                    tv_content.setText(data.getEaCont());
                    break;
                default:
                    break;
            }
        }

    };



    /**
     * 获取教务处公告
     */
    private void requestEduInfo(final Long id){
        mThread = new Thread() {
            @Override
            public void run() {
                try {
                    // 请求参数
                    Edu edu = new Edu();
                    edu.setId(id);
                    //数据加密
                    BaseTransferEntity bte = multipartHttpConverter.encryption(edu, sharedPreferences.getString("randomKey",""));
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("object", bte.getObject());
                    jsonObject.put("sign", bte.getSign());
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            try {
                                Edu data = JsonUtils.fromJson(jsonObject.toString(), Edu.class);
                                Message message = new Message();
                                message.what = 1;
                                message.obj = data;
                                handler.sendMessage(message);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(EduDetailActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            // 请求头
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("Authorization","Bearer " + sharedPreferences.getString("token",""));
                            map.put("Content-Type","application/json");
                            return map;
                        }
                    };
                    request.add(jsonObjectRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        mThread.start();
    }

}
