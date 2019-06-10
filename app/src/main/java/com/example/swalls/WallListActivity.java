package com.example.swalls;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.swalls.adapter.QuestionListViewAdapter;
import com.example.swalls.constant.Const;
import com.example.swalls.core.data.converter.MultipartHttpConverter;
import com.example.swalls.core.data.converter.entity.BaseTransferEntity;
import com.example.swalls.core.data.converter.entity.SecretKeyEntity;
import com.example.swalls.core.http.JsonArrayRequest;
import com.example.swalls.core.util.JsonUtils;
import com.example.swalls.modal.Wall;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WallListActivity extends AppCompatActivity implements AbsListView.OnScrollListener {

    public static String URL = Const.URL + "/wall";  //定义网络图片地址

    public String abstracts = "";

    private String url;

    private QuestionListViewAdapter adapter;

    private ListView listView;

    private Thread mThread;

    private Thread tokenThread;

    private View convertView;

    private Button button;

    //共享数据
    private SharedPreferences sharedPreferences;

    //Volley队列
    private RequestQueue request;

    private MultipartHttpConverter multipartHttpConverter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qlist_list);
        //模块标题  顶栏
        ((TextView)findViewById(R.id.item_mode_name)).setText("首页");
        this.url = WallListActivity.URL + "/selectByAbstracts";  //获取问题列表接口

        //接收abstracts值
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            abstracts = bundle.getString("abstracts","");
            Log.i("获取到的abstracts值为", abstracts);
        }

        initView();
        init();
        requestWallListInfo();
        keyRefresh();
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    /**
     * 初始化
     */
    private void init() {
        //共享数据
        sharedPreferences = getSharedPreferences(Const.SECRET_KEY_DATEBASE,MODE_PRIVATE);
        //网络请求器
        request = Volley.newRequestQueue(WallListActivity.this);
        //数据转换器
        multipartHttpConverter = new MultipartHttpConverter();

        convertView = LayoutInflater.from(this).inflate(R.layout.qlist_end,null);
        adapter = new QuestionListViewAdapter(this);
        listView = (ListView)findViewById(R.id.list);   //得到一个listView用来显示条目
        listView.addFooterView(convertView);            //添加到脚页显示
        listView.setAdapter(adapter);                   //给listview添加适配器
        listView.setOnScrollListener(this);             //给listview注册滚动监听

        //搜索按钮
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WallListActivity.this, SearchDemo.class));
            }
        });
    }

    /**
     * 初始化
     */
    private void initView() {
        final BottomNavigationView bnv = (BottomNavigationView) findViewById(R.id.navigation_1);
        bnv.setSelectedItemId(R.id.navigation_home);
        //点击选择item
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        return true;
                    case R.id.navigation_dashboard:
                        intent = new Intent(WallListActivity.this, NavigationActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        return true;
                    case R.id.navigation_notifications:
                        intent = new Intent(WallListActivity.this, UserActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture){

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int tableItemCount){

    }

    /**
     * 获取问题列表
     */
    private void requestWallListInfo() {

        mThread = new Thread() {
            @Override
            public void run() {
                try {
                    // 请求参数
                    BaseTransferEntity bte = null;
                    Wall wall = new Wall();
                    wall.setOpenId(sharedPreferences.getString("openId",""));
                    wall.setAbstracts(abstracts);
                    bte = multipartHttpConverter.encryption(wall, sharedPreferences.getString("randomKey",""));
                    //数据加密
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("object", bte.getObject());
                    jsonObject.put("sign", bte.getSign());
                    JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray jsonArray) {
                            Message message = new Message();
                            message.what = 1;
                            message.obj = JsonUtils.fromListJson(jsonArray.toString(), Wall.class);
                            handler.sendMessage(message);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            Toast.makeText(WallListActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState){

    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            List<Wall> data = (List<Wall>)msg.obj;

            switch (msg.what){
                case 1:
                    if(adapter.getCount() < data.size()){
                        adapter.setData(data);
                        Log.i("适配器数据注入 ", "数据大小: " + adapter.getCount());
                    }else{
                     listView.removeFooterView(convertView);
                    }
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 获取令牌
     */
    private void keyRefresh(){

        tokenThread = new Thread() {
            @Override
            public void run() {
                try {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                SecretKeyEntity sk = JsonUtils.fromJson(response, SecretKeyEntity.class);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("token",sk.getToken());
                                editor.putString("randomKey",sk.getRandomKey());
                                editor.putString("avatar","https://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKyskCV8S7lTKbf3YR1esyNlGyWe9MrptfqJhwkQAzynRtcQB7tyex9LmtsDpSbjEYqMZ7sxflHdA/132");
                                editor.putString("openId","ojnw95baofXIwxqN6R4PTYGytOaI");
                                editor.apply();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(WallListActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            // 请求参数
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("userName", "admin");
                            map.put("password", "admin");
                            return map;
                        }
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            // 请求头
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("Content-Type","application/x-www-form-urlencoded");
                            return map;
                        }
                    };
                    request.add(stringRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        tokenThread.start();
    }

}
