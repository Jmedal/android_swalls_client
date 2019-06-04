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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.swalls.constant.Const;
import com.example.swalls.core.data.converter.MultipartHttpConverter;
import com.example.swalls.core.data.converter.entity.BaseTransferEntity;
import com.example.swalls.core.util.JsonUtils;
import com.example.swalls.modal.College;
import com.example.swalls.modal.Edu;
import com.example.swalls.modal.Lecture;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EduDetailActivity extends AppCompatActivity {

    private final String url = EduListActivity.URL+ "/selectById";//定义网络图片渎地址

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
        setContentView(R.layout.item_detail);
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
            try {
                switch (msg.what) {
                    case 1:
                        if(EduListActivity.MODE ==0){
                            Edu data = JsonUtils.fromJson(msg.obj.toString(), Edu.class);
                            tv_title.setText(data.getEaTitle());
                            tv_time.setText(data.getEaTime());
                            tv_content.setText(data.getEaCont());
                        }
                        else if(EduListActivity.MODE ==1){
                            College data = JsonUtils.fromJson(msg.obj.toString(), College.class);
                            tv_title.setText(data.getTitle());
                            tv_time.setText(data.getTime());
                            tv_content.setText(data.getContents());
                        }
                        else if(EduListActivity.MODE ==2){
                            Lecture data = JsonUtils.fromJson(msg.obj.toString(), Lecture.class);
                            tv_title.setText(data.getLeTitle());
                            tv_time.setText(data.getLeTime());
                            tv_content.setText(data.getLeCont());
                        }
                        break;
                    default:
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
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
                    BaseTransferEntity bte = null;
                    if(EduListActivity.MODE ==0){
                        Edu item  = new Edu();
                        item.setId(id);
                        bte = multipartHttpConverter.encryption(item, sharedPreferences.getString("randomKey",""));
                    }
                    else if(EduListActivity.MODE ==1){
                        College item = new College();
                        item.setId(id);
                        bte = multipartHttpConverter.encryption(item, sharedPreferences.getString("randomKey",""));
                    }
                    else if(EduListActivity.MODE ==2){
                        Lecture item = new Lecture();
                        item.setId(id);
                        bte = multipartHttpConverter.encryption(item, sharedPreferences.getString("randomKey",""));
                    }
                    //数据加密
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("object", bte.getObject());
                    jsonObject.put("sign", bte.getSign());
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            Message message = new Message();
                            message.what = 1;
                            message.obj = jsonObject;
                            handler.sendMessage(message);
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
