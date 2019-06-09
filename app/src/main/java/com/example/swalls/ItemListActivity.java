package com.example.swalls;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
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
import com.example.swalls.adapter.ListViewAdapter;
import com.example.swalls.constant.Const;
import com.example.swalls.constant.Mode;
import com.example.swalls.core.data.converter.entity.SecretKeyEntity;
import com.example.swalls.core.util.JsonUtils;
import com.example.swalls.modal.College;
import com.example.swalls.modal.Edu;
import com.example.swalls.modal.Lecture;
import com.example.swalls.modal.Share;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.swalls.constant.Mode.MODE_SHARE;

public class ItemListActivity extends AppCompatActivity implements AbsListView.OnScrollListener {

    public static String URL;//定义网络地址

    public static Mode MODE; //MODE_EDU：教务处公告； MODE_COLLEGE：社团活动； MODE_SHARE: 解题习集；MODE_LECTURE：讲座一览；

    private String url;

    private ListViewAdapter adapter;

    private ListView listView;

    private Thread mThread;

    private View convertView;

    //共享数据
    private SharedPreferences sharedPreferences;

    //Volley队列
    private RequestQueue request;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_list_activity);
        Bundle bundle = getIntent().getExtras();

        String mode = null;
        if(bundle!=null){
            ((TextView)findViewById(R.id.item_mode_name)).setText(bundle.getString("mode_name")); //设置模式名
            ItemListActivity.MODE = (Mode) bundle.get("modeI");                                        //设置模式
            ItemListActivity.URL = Const.URL + bundle.getString("mode");                          //设置模式 根URL
            if(ItemListActivity.MODE != MODE_SHARE)                                                    //设置各模式 获取列表的 URL
                this.url = ItemListActivity.URL + "/all";
            else
                this.url = ItemListActivity.URL + "/showShare";
        }
        init();
    }

    private void init() {
        //共享数据
        sharedPreferences = getSharedPreferences(Const.SECRET_KEY_DATEBASE,MODE_PRIVATE);
        //网络请求器
        request = Volley.newRequestQueue(ItemListActivity.this);


        convertView = LayoutInflater.from(this).inflate(R.layout.item_loading,null); //item模版
        adapter = new ListViewAdapter(this);
        listView = (ListView) findViewById(R.id.list);              // 得到一个ListView用来显示条目
        listView.addFooterView(convertView);                        // 添加到脚页显示
        listView.setAdapter(adapter);                               // 给ListView添加适配器
        listView.setOnScrollListener(this);                         // 给ListView注册滚动监听
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        // TODO Auto-generated method stub
        if(firstVisibleItem+visibleItemCount==totalItemCount)
        {
            //开线程去下载网络数据
            if ((mThread == null || !mThread.isAlive()) && adapter.getCount() == 0) {
                mThread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Message message = new Message();
                                    List data = null;
                                    switch (ItemListActivity.MODE){
                                        case MODE_EDU:
                                            data = JsonUtils.fromListJson(response, Edu.class);
                                            break;
                                        case MODE_COLLEGE:
                                            data = JsonUtils.fromListJson(response, College.class);
                                            break;
                                        case MODE_SHARE:
                                            data = JsonUtils.fromListJson(response, Share.class);
                                            break;
                                        case MODE_LECTURE:
                                            data = JsonUtils.fromListJson(response, Lecture.class);
                                            break;
                                    }
                                    message.what = 1;
                                    message.obj = data;
                                    handler.sendMessage(message);
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(ItemListActivity.this, "网络连接失败", Toast.LENGTH_LONG).show();
                                }
                            });
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

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // TODO Auto-generated method stub
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            List data = (List)msg.obj;
            switch (msg.what) {
                case 1:
                    if (adapter.getCount() < data.size()) {
                        adapter.setData(data);
                    } else {
                        listView.removeFooterView(convertView);
                    }
                    //重新刷新Listview的adapter里面数据
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }

    };
}

