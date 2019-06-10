package com.example.swalls;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
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
import com.example.swalls.core.http.MultipartRequest;
import com.example.swalls.core.util.DateUtil;
import com.example.swalls.core.util.ImageTypeUtils;
import com.example.swalls.core.http.entity.MultipartEntity;
import com.example.swalls.modal.Wall;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class IssueActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final int WRITE_PERMISSION = 0x01;

    private static final String TAG = "IssueActivity";

    private static String URL;

    private String infoUrl;

    private String imageUrl;

    private GridView gridView;                              //网格显示缩略图

    private Button issue;                                   //发布按钮

    private Button cancel;                                  //取消按钮

    private EditText title;

    private EditText content;

    private Spinner grade;

    private final int IMAGE_OPEN = 1;                       //打开图片标记

    private String pathImage;                               //选择图片路径

    private String pathImage_sign;

    private Bitmap bmp;                                     //导入临时图片

    private List<HashMap<String, Object>> imageItem;

    private SimpleAdapter simpleAdapter;                    //适配器

    private Thread mThread;

    private SharedPreferences sharedPreferences;            //共享数据

    private RequestQueue request;                           //Volley队列

    private MultipartHttpConverter multipartHttpConverter;  //数据转换器

    private String gradeStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.issue_activity);
        ((TextView)findViewById(R.id.item_mode_name)).setText("提出问题");
        IssueActivity.URL = Const.URL + "/wall";
        this.infoUrl = IssueActivity.URL + "/get";
        this.imageUrl = IssueActivity.URL  + "/upLoadPicture";

        init();
        requestWritePermission();
    }

    /**
     * 初始化
     */
    protected void init(){
        sharedPreferences = getSharedPreferences(Const.SECRET_KEY_DATEBASE,MODE_PRIVATE);
        request = Volley.newRequestQueue(IssueActivity.this);
        multipartHttpConverter = new MultipartHttpConverter();
        title = (EditText)findViewById(R.id.issue_title);
        content = (EditText)findViewById(R.id.issue_content);
        issue = (Button)findViewById(R.id.issue_btn);
        cancel = (Button)findViewById(R.id.issue_cancel_btn);
        gridView = (GridView)findViewById(R.id.gridView1);
        grade = (Spinner)findViewById(R.id.grade);

        issue.setOnClickListener(this);
        cancel.setOnClickListener(this);
        grade.setOnItemSelectedListener(this);

        String[] myItem=getResources().getStringArray(R.array.grade);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,myItem);
        grade.setAdapter(arrayAdapter);

        /*
         * 防止键盘挡住输入框
         * 不希望遮挡设置activity属性: android:windowSoftInputMode="adjustPan"
         * 希望动态调整高度: android:windowSoftInputMode="adjustResize"
         */
        getWindow().setSoftInputMode(WindowManager.LayoutParams.
                SOFT_INPUT_ADJUST_PAN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);          //锁定屏幕
        bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.add_image);     //获取资源图片加号
        imageItem = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>();
        map.put("itemImage", bmp);
        imageItem.add(map);
        adapterInit(imageItem);
        gridView.setAdapter(simpleAdapter);
        gridView.setOnItemClickListener(this);
    }

    /**
     * 适配器初始化
     */
    protected void adapterInit(List imageItem){
        simpleAdapter = new SimpleAdapter(this,
                imageItem, R.layout.issue_picture,
                new String[] { "itemImage"}, new int[] { R.id.imageView});
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {        //HashMap载入bmp图片
            @Override
            public boolean setViewValue(View view, Object data,             //实现setViewBinder接口
                                        String textRepresentation) {
                // TODO Auto-generated method stub
                if(view instanceof ImageView && data instanceof Bitmap){
                    ImageView i = (ImageView)view;
                    i.setImageBitmap((Bitmap) data);
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 监听button点击事件
     */
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.issue_btn){
            uploadWallInfo();
        }else if (v.getId() == R.id.issue_cancel_btn){
            this.finish();
        }
    }

    /**
     * spinner监听器
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        gradeStr = parent.getItemAtPosition(position).toString();
    }

    /**
     * spinner监听器
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        gradeStr = parent.getItemAtPosition(0).toString();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 1:
                    if(msg.obj != null){
                        final BaseTransferEntity bte = (BaseTransferEntity)msg.obj;
                        mThread = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    if(imageItem.size() > 1){
                                        MultipartRequest multipartRequest = new MultipartRequest(
                                                imageUrl, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String string) {
                                                //Toast.makeText(IssueActivity.this, "成功", Toast.LENGTH_SHORT).show();
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                //Toast.makeText(IssueActivity.this,"失败",Toast.LENGTH_SHORT).show();
                                            }
                                       });
                                        // 添加header
                                        multipartRequest.addHeader("Authorization", "Bearer " + sharedPreferences.getString("token",""));
                                        // 通过MultipartEntity来设置参数
                                        MultipartEntity multi = multipartRequest.getMultiPartEntity();
                                        multi.addStringPart("object", bte.getObject());
                                        multi.addStringPart("sign", bte.getSign());
                                        //传二进制byte[]
                                        //multi.addBinaryPart("logo", bytes);
                                        //传文件(以图片为例)
                                        multi.addFilePart("first_image",new File(pathImage_sign), ImageTypeUtils.getImageMimeType(pathImage_sign));
                                        request.add(multipartRequest);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        mThread.start();
                        Intent intent = new Intent(IssueActivity.this, WallListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    break;
                default:
                    break;
            }
        }

    };

    /**
     * 上传问题
     */
    protected void uploadWallInfo(){
        String title = this.title.getText().toString().trim();
        String content = this.content.getText().toString().trim();
        if(TextUtils.isEmpty(title)){
            Toast.makeText(IssueActivity.this, "请输入问题摘要", Toast.LENGTH_LONG).show();
        }else if(TextUtils.isEmpty(content)){
            Toast.makeText(IssueActivity.this, "请输入问题内容", Toast.LENGTH_LONG).show();
        }else {
            Log.i(TAG, title);
            Log.i(TAG, content);
            final Wall wall = new Wall();
            wall.setOpenId(sharedPreferences.getString("openId",""));
            wall.setParentObjectId((long)0);
            wall.setAbstracts(title);
            wall.setLabel(gradeStr);
            wall.setWriteContests(content);
            wall.setWriterTime(DateUtil.getWallNowDate());
            mThread = new Thread() {
                @Override
                public void run() {
                    try {
                        // 请求参数
                        BaseTransferEntity bte = multipartHttpConverter.encryption(wall, sharedPreferences.getString("randomKey",""));
                        //数据加密
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("object", bte.getObject());
                        jsonObject.put("sign", bte.getSign());
                        final BaseTransferEntity finalBte = bte;
                        JsonObjectRequestString stringRequest = new JsonObjectRequestString(Request.Method.POST, infoUrl, jsonObject, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String str) {
                                System.out.println("response: " + str);
                                String response  = str.replace("\"", "");
                                if(response.equals("accept")){
                                    Toast.makeText(IssueActivity.this, "提问成功", Toast.LENGTH_LONG).show();
                                    Message message = new Message();
                                    message.what = 1;
                                    message.obj = finalBte;
                                    handler.sendMessage(message);
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                Toast.makeText(IssueActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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

    /**
     * 监听GridView点击事件
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if( imageItem.size() == 10) {
            Toast.makeText(IssueActivity.this, "图片数9张已满", Toast.LENGTH_SHORT).show();
        }
        else if(position == imageItem.size()-1) {                                   //点击图片位置
            Toast.makeText(IssueActivity.this, "添加图片", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI); //选择图片
            startActivityForResult(intent, IMAGE_OPEN);                             //通过onResume()刷新数据

        }
        else {
            dialog(position);                                                       //删除图片
        }
    }


    /**
     * 获取图片路径 响应startActivityForResult
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //打开图片
        if(resultCode==RESULT_OK && requestCode==IMAGE_OPEN) {
            Uri uri = data.getData();
            if (!TextUtils.isEmpty(uri.getAuthority())) {
                //查询选择图片
                Cursor cursor = getContentResolver().query(uri, new String[] { MediaStore.Images.Media.DATA }, null, null, null);
                //没找到选择图片
                if (null == cursor)
                    return;
                //光标移动至开头 获取图片路径
                cursor.moveToFirst();
                pathImage = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
        }
    }

    /**
     * 刷新图片
     */
    @Override
    protected void onResume() {
        super.onResume();
        if(!TextUtils.isEmpty(pathImage)){
            Bitmap addbmp=BitmapFactory.decodeFile(pathImage);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("itemImage", addbmp);
            if (imageItem.size() > 1)
                imageItem.remove(imageItem.size()-2);
            imageItem.add(imageItem.size()-1, map);
            adapterInit(imageItem);
            gridView.setAdapter(simpleAdapter);
            simpleAdapter.notifyDataSetChanged();
            pathImage_sign = pathImage;
            pathImage = null;
        }
    }

    /**
     * Dialog对话框提示用户删除操作
     * position为删除图片位置
     */
    protected void dialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(IssueActivity.this);
        builder.setMessage("确认移除已添加图片吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                imageItem.remove(position);
                simpleAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 获取权限
     */
    protected void requestWritePermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(requestCode == WRITE_PERMISSION){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Log.d(LOG_TAG, "Write Permission Failed");
                Toast.makeText(this, "You must allow permission write external storage to your mobile device.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
