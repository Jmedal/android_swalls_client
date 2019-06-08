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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.swalls.constant.Const;
import com.example.swalls.core.security.converter.MultipartHttpConverter;
import com.example.swalls.core.security.converter.entity.BaseTransferEntity;
import com.example.swalls.core.util.JsonUtils;
import com.example.swalls.modal.College;
import com.example.swalls.modal.Edu;
import com.example.swalls.modal.Lecture;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ItemDetailActivity extends AppCompatActivity {

    private final String url = ItemListActivity.URL+ "/selectById";//定义网络图片渎地址

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
        setContentView(R.layout.item_detail_activity);
        init();
        tv_title = (TextView)findViewById(R.id.item_detail_title);
        tv_time = (TextView)findViewById(R.id.item_detail_time);
        tv_content = (TextView)findViewById(R.id.item_detail_content);
        tv_content.setMovementMethod(ScrollingMovementMethod.getInstance());

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null)
            requestDetailInfo(bundle.getLong("id")); //获取数据id
    }

    private void init() {
        //共享数据
        sharedPreferences = getSharedPreferences(Const.SECRET_KEY_DATEBASE,MODE_PRIVATE);
        //网络请求器
        request = Volley.newRequestQueue(ItemDetailActivity.this);
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
                        switch (ItemListActivity.MODE){
                            case MODE_EDU:
                                Edu edu = JsonUtils.fromJson(msg.obj.toString(), Edu.class);
                                tv_title.setText(edu.getEaTitle());
                                tv_time.setText(edu.getEaTime());
                                tv_content.setText(edu.getEaCont());
                                break;
                            case MODE_COLLEGE:
                                College college = JsonUtils.fromJson(msg.obj.toString(), College.class);
                                tv_title.setText(college.getTitle());
                                tv_time.setText(college.getTime());
                                tv_content.setText(college.getContents());
                                break;
                            case MODE_LECTURE:
                                Lecture lecture = JsonUtils.fromJson(msg.obj.toString(), Lecture.class);
                                tv_title.setText(lecture.getLeTitle());
                                tv_time.setText(lecture.getLeTime());
                                tv_content.setText(lecture.getLeCont());
                                break;
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
     * 获取详细数据
     */
    private void requestDetailInfo(final Long id){
        mThread = new Thread() {
            @Override
            public void run() {
                try {
                    // 请求参数
                    BaseTransferEntity bte = null;
                    switch (ItemListActivity.MODE){
                        case MODE_EDU:
                            Edu edu  = new Edu();
                            edu.setId(id);
                            bte = multipartHttpConverter.encryption(edu, sharedPreferences.getString("randomKey",""));
                            break;
                        case MODE_COLLEGE:
                            College college = new College();
                            college.setId(id);
                            bte = multipartHttpConverter.encryption(college, sharedPreferences.getString("randomKey",""));
                            break;
                        case MODE_LECTURE:
                            Lecture lecture = new Lecture();
                            lecture.setId(id);
                            bte = multipartHttpConverter.encryption(lecture, sharedPreferences.getString("randomKey",""));
                            break;
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
                            Toast.makeText(ItemDetailActivity.this, "网络连接失败", Toast.LENGTH_LONG).show();
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
