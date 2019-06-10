package com.example.swalls;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.swalls.constant.Const;
import com.example.swalls.core.data.converter.MultipartHttpConverter;
import com.example.swalls.core.data.converter.entity.BaseTransferEntity;
import com.example.swalls.core.http.JsonObjectRequestString;
import com.example.swalls.core.util.DateUtil;
import com.example.swalls.modal.College;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class PublishActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int WRITE_PERMISSION = 0x01;

    private static final String TAG = "IssueActivity";

    private static String URL;

    private String url;

    private Button publish;                                 //发布按钮

    private Button cancel;                                  //取消按钮

    private EditText title;

    private EditText content;

    private EditText college;

    private Thread mThread;

    private SharedPreferences sharedPreferences;            //共享数据

    private RequestQueue request;                           //Volley队列

    private MultipartHttpConverter multipartHttpConverter; //数据转换器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publish_activity);
        Bundle bundle = getIntent().getExtras();
        String mode = null;
        if(bundle!=null){
            ((TextView)findViewById(R.id.item_mode_name)).setText(bundle.getString("mode_name")); //设置模式名
            PublishActivity.URL = Const.URL + bundle.getString("mode");                           //设置模式 根URL
            url = PublishActivity.URL + "/get";
        }
        init();
    }

    /**
     * 初始化
     */
    protected void init(){
        sharedPreferences = getSharedPreferences(Const.SECRET_KEY_DATEBASE,MODE_PRIVATE);
        request = Volley.newRequestQueue(PublishActivity.this);
        multipartHttpConverter = new MultipartHttpConverter();
        title = (EditText)findViewById(R.id.publish_title);
        content = (EditText)findViewById(R.id.publish_content);
        publish = (Button)findViewById(R.id.publish_btn);
        cancel = (Button)findViewById(R.id.publish_cancel_btn);
        college = (EditText)findViewById(R.id.college);
        publish.setOnClickListener(this);
        cancel.setOnClickListener(this);

        /*
         * 防止键盘挡住输入框
         * 不希望遮挡设置activity属性: android:windowSoftInputMode="adjustPan"
         * 希望动态调整高度: android:windowSoftInputMode="adjustResize"
         */
        getWindow().setSoftInputMode(WindowManager.LayoutParams.
                SOFT_INPUT_ADJUST_PAN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);          //锁定屏幕
    }

    /**
     * 监听button点击事件
     */
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.publish_btn){
            uploadCollegeInfo();
        }else if (v.getId() == R.id.publish_cancel_btn){
            this.finish();
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 1:
                    Intent intent = new Intent(PublishActivity.this, NavigationActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }

    };

    /**
     * 上传问题
     */
    protected void uploadCollegeInfo(){
        String title = this.title.getText().toString().trim();
        String content = this.content.getText().toString().trim();
        String college = this.college.getText().toString().trim();
        if(TextUtils.isEmpty(title)){
            Toast.makeText(PublishActivity.this, "请输入活动标题", Toast.LENGTH_LONG).show();
        }else if(TextUtils.isEmpty(content)){
            Toast.makeText(PublishActivity.this, "请输入活动内容", Toast.LENGTH_LONG).show();
        }else if(TextUtils.isEmpty(college)){
            Toast.makeText(PublishActivity.this, "请输入社团名称", Toast.LENGTH_LONG).show();
        }else {
            Log.i(TAG, title);
            Log.i(TAG, content);
            final College clgInfo = new College();
            clgInfo.setOpenId("ojnw95baofXIwxqN6R4PTYGytOaI");
            clgInfo.setTitle(title);
            clgInfo.setCollege(college);
            clgInfo.setContents(content);
            clgInfo.setTime(DateUtil.getWallNowDate());
            mThread = new Thread() {
                @Override
                public void run() {
                    try {
                        // 请求参数
                        BaseTransferEntity bte = multipartHttpConverter.encryption(clgInfo, sharedPreferences.getString("randomKey",""));
                        //数据加密
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("object", bte.getObject());
                        jsonObject.put("sign", bte.getSign());
                        JsonObjectRequestString stringRequest = new JsonObjectRequestString(Request.Method.POST, url, jsonObject, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String str) {
                                System.out.println("response: " + str);
                                String response  = str.replace("\"", "");
                                if(response.equals("accept")){
                                    Toast.makeText(PublishActivity.this, "发布成功", Toast.LENGTH_LONG).show();
                                    Message message = new Message();
                                    message.what = 1;
                                    handler.sendMessage(message);
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                Toast.makeText(PublishActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                            }
                        })
                        {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                // 请求头
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("Authorization","Bearer " + sharedPreferences.getString("token",""));
                                map.put("Content-Type","application/json");
                                return map;
                            }
                        };
                        request.add(stringRequest);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            mThread.start();
        }
    }
}
