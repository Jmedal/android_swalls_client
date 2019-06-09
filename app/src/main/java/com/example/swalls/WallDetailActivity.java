package com.example.swalls;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.swalls.adapter.AnswerListViewAdapter;
import com.example.swalls.adapter.CommentExpandAdapter;
import com.example.swalls.constant.Const;
import com.example.swalls.core.http.JsonArrayRequest;
import com.example.swalls.core.http.JsonObjectRequestString;
import com.example.swalls.core.security.converter.MultipartHttpConverter;
import com.example.swalls.core.security.converter.entity.BaseTransferEntity;
import com.example.swalls.core.util.JsonUtils;
import com.example.swalls.core.util.VolleySingleton;
import com.example.swalls.modal.College;
import com.example.swalls.modal.Edu;
import com.example.swalls.modal.Lecture;
import com.example.swalls.modal.Wall;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WallDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private Context context;
    public ImageView wall_picture;

    private final String url = WallListActivity.URL + "/showQuestion";
    private TextView tv_title;
    private TextView tv_time;
    private TextView tv_content;
    private Thread mThread;

    private final String Answerurl = WallListActivity.URL + "/showAnswers";
    private AnswerListViewAdapter adapter;
    private ListView listView;
    private Thread mThreadAnswer;
    private View convertView;

    private final String PictureUrl = WallListActivity.URL + "/getPictureFileName";
    private Thread mThreadPicture;

    private final String sendDataUrl = WallListActivity.URL + "/get";

    private static final String TAG = "WallDetailActivity";
    private android.support.v7.widget.Toolbar toolbar;
    private TextView bt_comment;
    private CommentExpandAdapter commentadapter;
    private BottomSheetDialog dialog;

    private Long parentId;
    private LayoutInflater mInflater;


    //共享数据
    private SharedPreferences sharedPreferences;

    //Volley队列
    private RequestQueue request;

    private MultipartHttpConverter multipartHttpConverter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qlist_detail);
        init();
        initView();
        tv_title = (TextView)findViewById(R.id.wall_title);
        tv_content = (TextView)findViewById(R.id.wall_content);
        tv_content.setMovementMethod(ScrollingMovementMethod.getInstance());

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            parentId = bundle.getLong("id");
            System.out.println("parentId: " + parentId);
            requestWallInfo(bundle.getLong("id"));
            requestPictureInfo(bundle.getLong("id"));
            requestAnswerList(bundle.getLong("id"));
        }
    }

    //Activity创建或者从后台重新回到前台时被调用
    @Override
    protected void onStart() {
        super.onStart();
        init();
        initView();
        Log.i(TAG, "onStart called.");
    }

    //Activity从后台重新回到前台时被调用
    @Override
    protected void onRestart() {
        super.onRestart();
        init();
        initView();
        Log.i(TAG, "onRestart called.");
    }

    //Activity创建或者从被覆盖、后台重新回到前台时被调用
    @Override
    protected void onResume() {
        super.onResume();
        init();
        initView();
        Log.i(TAG, "onResume called.");
    }

    private void init(){
        //共享数据
        sharedPreferences = getSharedPreferences(Const.SECRET_KEY_DATEBASE,MODE_PRIVATE);
        //网络请求器
        request = Volley.newRequestQueue(WallDetailActivity.this);
        //数据转换器
        multipartHttpConverter = new MultipartHttpConverter();

//        convertView = LayoutInflater.from(this).inflate(R.layout.qlist_end,null);
        adapter = new AnswerListViewAdapter(this);
        listView = (ListView)findViewById(R.id.list);  //得到一个listView用来显示条目
//        listView.addFooterView(convertView);  //添加到脚页显示
        listView.setAdapter(adapter);  //给listview添加适配器
//        listView.setOnScrollListener(this.listView);  //给listview注册滚动监听
    }

    private void initView() {
        bt_comment = (TextView) findViewById(R.id.detail_page_do_comment);
        bt_comment.setOnClickListener((View.OnClickListener) this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.detail_page_do_comment){
            showCommentDialog();
        }
    }

    /**
     * 回答问题
     */
    @SuppressLint("Handlerleak")
    private Handler handlerSendData = new Handler(){
        @Override
        public void handleMessage(Message msg){
            try{
                switch (msg.what){
                    case 1:
                        Wall data = JsonUtils.fromJson(msg.obj.toString(), Wall.class);
                        System.out.println(data);
                        break;
                    default:
                        break;
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    };

    /**
     * by moos on 2018/04/20
     * func:弹出评论框
     */
    private void showCommentDialog(){
        dialog = new BottomSheetDialog(this);
        View commentView = LayoutInflater.from(this).inflate(R.layout.comment_dialog_layout,null);
        final EditText commentText = (EditText) commentView.findViewById(R.id.dialog_comment_et);
        final Button bt_comment = (Button) commentView.findViewById(R.id.dialog_comment_bt);
        dialog.setContentView(commentView);
        /**
         * 解决bsd显示不全的情况
         */
        final View parent = (View) commentView.getParent();
        BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
        commentView.measure(0,0);
        behavior.setPeekHeight(commentView.getMeasuredHeight());

        bt_comment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String commentContent = commentText.getText().toString().trim();
                if(!TextUtils.isEmpty(commentContent)){
                    System.out.println(commentContent);

                    final Wall wall = new Wall();
                    wall.setOpenId("ojnw95baofXIwxqN6R4PTYGytOaI");
                    wall.setParentObjectId(parentId);
                    wall.setWriteContests(commentContent);
                    wall.setWriterTime("2019/06/10");
//                    System.out.println(wall);

                    mThread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                // 请求参数
                                BaseTransferEntity bte = null;
                                System.out.println(wall);
                                bte = multipartHttpConverter.encryption(wall, sharedPreferences.getString("randomKey",""));
                                //数据加密
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("object", bte.getObject());
                                jsonObject.put("sign", bte.getSign());
                                JsonObjectRequestString stringRequest = new JsonObjectRequestString(Request.Method.POST, sendDataUrl, jsonObject, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String jsonObject) {
                                        System.out.println("response: " + jsonObject);
                                        String response  = jsonObject.replace("\"", "");
                                        if(response.equals("accept")){
                                            System.out.println("回答成功");

                                            //新建一个显式意图，第一个参数为当前Activity类对象，第二个参数为你要打开的Activity类
                                            Intent intent = new Intent(WallDetailActivity.this,WallDetailActivity.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putLong("id", parentId);
                                            intent.putExtras(bundle);
                                            startActivity(intent);

//                                            Message message = new Message();
//                                            message.what = 1;
//                                            message.obj = jsonObject;
//                                            handlerSendData.sendMessage(message);
                                        }else{
                                            System.out.println("回答了");
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                        Toast.makeText(WallDetailActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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

                    dialog.dismiss();
//                    CommentDetailBean detailBean = new CommentDetailBean("小明", commentContent,"刚刚");
//                    commentadapter.addTheCommentData(detailBean);
                    Toast.makeText(WallDetailActivity.this,"评论成功",Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(WallDetailActivity.this,"评论内容不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });
        commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence) && charSequence.length()>2){
                    bt_comment.setBackgroundColor(Color.parseColor("#FFB568"));
                }else {
                    bt_comment.setBackgroundColor(Color.parseColor("#D8D8D8"));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dialog.show();
    }

    /**
     * 传递参数
     */
    @SuppressLint("Handlerleak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            try{
                switch (msg.what){
                    case 1:
                        Wall data = JsonUtils.fromJson(msg.obj.toString(), Wall.class);
                        tv_title.setText(data.getAbstracts());
//                        tv_time.setText(data.getWriterTime());
                        tv_content.setText(data.getWriteContests());
                        break;
                    default:
                        break;
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    };

    /**
     * 获取问题
     */
    private void requestWallInfo(final Long id){
        mThread = new Thread() {
            @Override
            public void run() {
                try {
                    // 请求参数
                    BaseTransferEntity bte = null;

                    Wall wall  = new Wall();

                    wall.setId(id);
                    bte = multipartHttpConverter.encryption(wall, sharedPreferences.getString("randomKey",""));

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
                            Toast.makeText(WallDetailActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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

    /**
     * 传递图片名
     */
    @SuppressLint("Handlerleak")
    private Handler handlerImage = new Handler(){
        @Override
        public void handleMessage(Message msg){

            wall_picture = (ImageView)findViewById(R.id.wall_picture);

            System.out.println(msg.obj.toString());

            switch (msg.what){
                case 1:
//                        Wall data = JsonUtils.fromJson(msg.obj.toString(), Wall.class);
//                        System.out.println("url: " + data);

                    String imagestr  = msg.obj.toString().replace("\"", "");

                    String Imageulr = WallListActivity.URL + "/downLoadPicture/" + imagestr;
                    System.out.println("拼接url: " + Imageulr);


                    ImageRequest request = new ImageRequest(
//                "https://wx.qlogo.cn/mmopen/vi_32/yqiciclJJuEQNm3iadNQmFxjwiax3lRo1Ipc1cjD6zDWIov6hcic2NqnibxZ1zdMicoObkhulut26OicjOeXLG6SdwIAcA/132",
                            Imageulr,
                            new Response.Listener<Bitmap>() {
                                @Override
                                public void onResponse(Bitmap bitmap) {
                                    wall_picture.setImageBitmap(bitmap);
                                }
                            }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
//                            wall_picture.setImageResource(R.mipmap.ic_launcher);  //异常时加载的图片
                        }
                    });
                    VolleySingleton.getVolleySingleton(WallDetailActivity.this)
                            .addToRequestQueue(request);

//                        tv_title.setText(data.getAbstracts());
//                        tv_time.setText(data.getWriterTime());
//                        tv_content.setText(data.getWriteContests());
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * 获取图片路径
     * @param id
     */
    private void requestPictureInfo(final long id) {
        mThread = new Thread() {
            @Override
            public void run() {
                try {
                    // 请求参数
                    BaseTransferEntity bte = null;
                    Wall wall = new Wall();
//                    wall.setOpenId("ojnw95baofXIwxqN6R4PTYGytOaI");
//                    wall.setAbstracts("");
                    wall.setId(id);
                    bte = multipartHttpConverter.encryption(wall, sharedPreferences.getString("randomKey",""));
                    //数据加密
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("object", bte.getObject());
                    jsonObject.put("sign", bte.getSign());
                    JsonObjectRequestString stringRequest = new JsonObjectRequestString(Request.Method.POST, PictureUrl, jsonObject, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String jsonObject) {
                            System.out.println("url: " + jsonObject);
                            Message message = new Message();
                            message.what = 1;
                            message.obj = jsonObject;
                            handlerImage.sendMessage(message);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            Toast.makeText(WallDetailActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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

    @SuppressLint("HandlerLeak")
    private Handler handlerAnswer = new Handler(){

        @Override
        public void handleMessage(Message msg){

//            System.out.println(msg.obj);

            List<Wall> data = (List<Wall>)msg.obj;
            switch (msg.what){
                case 1:
                    if(adapter.getCount() < data.size()){
                        adapter.setData(data);
                        Log.i("适配器数据注入 ", "数据大小: " + adapter.getCount());
                    }else{
                        listView.removeFooterView(convertView);
                    }

                    //重新刷新listview的adapter里面数据
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 获取回答列表
     */
    private void requestAnswerList(final Long id) {

        mThread = new Thread() {
            @Override
            public void run() {
                try {
                    // 请求参数
                    BaseTransferEntity bte = null;
                    Wall wall = new Wall();
//                    wall.setOpenId("ojnw95baofXIwxqN6R4PTYGytOaI");
//                    wall.setAbstracts("");
                    wall.setId(id);
                    bte = multipartHttpConverter.encryption(wall, sharedPreferences.getString("randomKey",""));
                    //数据加密
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("object", bte.getObject());
                    jsonObject.put("sign", bte.getSign());
                    JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.POST, Answerurl, jsonObject, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray jsonArray) {
                            System.out.println("jsonArray: " + jsonArray);
                            Message message = new Message();
                            message.what = 1;
                            message.obj = JsonUtils.fromListJson(jsonArray.toString(), Wall.class);
                            handlerAnswer.sendMessage(message);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            Toast.makeText(WallDetailActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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
