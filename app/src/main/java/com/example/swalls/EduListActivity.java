package com.example.swalls;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
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
import com.example.swalls.core.data.converter.entity.SecretKeyEntity;
import com.example.swalls.core.util.JsonUtils;
import com.example.swalls.modal.Edu;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EduListActivity extends AppCompatActivity implements AbsListView.OnScrollListener {

    private final String url = Const.URL + "/auth/edu/all";//定义网络图片渎地址

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
        setContentView(R.layout.items_list);
        init();
    }

    private void init() {
        //共享数据
        sharedPreferences = getSharedPreferences(Const.SECRET_KEY_DATEBASE,MODE_PRIVATE);
        //网络请求器
        request = Volley.newRequestQueue(EduListActivity.this);
        keyRefresh();

        convertView = LayoutInflater.from(this).inflate(R.layout.item_loading,null); //item模版
        adapter = new ListViewAdapter(this);
        listView = (ListView) findViewById(R.id.list);// 得到一个ListView用来显示条目
        listView.addFooterView(convertView);// 添加到脚页显示
        listView.setAdapter(adapter);// 给ListView添加适配器
        listView.setOnScrollListener(this);// 给ListView注册滚动监听
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
                                    Log.i("教务处信息", "post请求成功");
                                    List<Edu> data = JsonUtils.fromListJson(response,Edu.class);
                                    Message message = new Message();
                                    message.what = 1;
                                    message.obj = data;
                                    handler.sendMessage(message);
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.i("教务处信息", "post请求失败" + error.toString());
                                    Toast.makeText(EduListActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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
            List<Edu> data = (List<Edu>)msg.obj;
            switch (msg.what) {
                case 1:
                    if (adapter.getCount() < data.size()) {
                        adapter.setData(data);
                        Log.i("适配器数据注入", "数据大小：" + adapter.getCount() );
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

    /**
     * 获取令牌
     */
    private void keyRefresh(){
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.URL+"/auth", new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                SecretKeyEntity sk = JsonUtils.fromJson(response, SecretKeyEntity.class);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("token",sk.getToken());
                                editor.putString("randomKey",sk.getRandomKey());
                                editor.apply();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(EduListActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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
        thread.start();
    }

//Toast.makeText(EduListActivity.this, adapter.getCount()+"", Toast.LENGTH_LONG).show();
}

