package com.example.swalls;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.example.swalls.constant.Const;
import com.example.swalls.core.http.VolleySingleton;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout ask_question;
    private LinearLayout my_question;
    private LinearLayout my_collection;
    private LinearLayout publish_college;
    private ImageView imageView;

    //共享数据
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);
        initView();
        init();
        initImageView();
    }

    /**
     * 初始化头像
     */
    private void initImageView(){
//        System.out.println(sharedPreferences.getString("avatar",""));
        ImageRequest request = new ImageRequest(
//                sharedPreferences.getString("avatar",""),
                "https://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKyskCV8S7lTKbf3YR1esyNlGyWe9MrptfqJhwkQAzynRtcQB7tyex9LmtsDpSbjEYqMZ7sxflHdA/132",
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        imageView.setImageBitmap(bitmap);
                    }
                },
                0, 0,
                Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                imageView.setImageResource(R.mipmap.ic_launcher);  //异常时加载的图片
            }
        });
        VolleySingleton.getVolleySingleton(getApplicationContext())
                .addToRequestQueue(request);
    }
    /**
     * 初始化
     */
    private void init() {
        ask_question = (LinearLayout)findViewById(R.id.ask_question);
        my_collection = (LinearLayout)findViewById(R.id.my_collection);
        my_question = (LinearLayout)findViewById(R.id.my_question);
        publish_college = (LinearLayout)findViewById(R.id.publish_college);
        imageView = (ImageView)findViewById(R.id.imageView);
        ask_question.setOnClickListener(this);
        my_collection.setOnClickListener(this);
        my_question.setOnClickListener(this);
        publish_college.setOnClickListener(this);

        //共享数据
        sharedPreferences = getSharedPreferences(Const.SECRET_KEY_DATEBASE,MODE_PRIVATE);
    }

    /**
     * 初始化导航栏
     */
    private void initView() {
        final BottomNavigationView bnv = (BottomNavigationView) findViewById(R.id.navigation_3);
        bnv.setSelectedItemId(R.id.navigation_notifications);
        //点击选择item
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        intent = new Intent(UserActivity.this, WallListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        return true;
                    case R.id.navigation_dashboard:
                        intent = new Intent(UserActivity.this, NavigationActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        return true;
                    case R.id.navigation_notifications:
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.ask_question:
                intent = new Intent(UserActivity.this, IssueActivity.class);
                intent.putExtra("mode_name","提出问题");
                intent.putExtra("mode","/wall");
                startActivity(intent);
                break;

            case R.id.my_question:
                intent = new Intent(UserActivity.this,MyquestionListActivity.class);
                //启动
                startActivity(intent);
                break;

            case R.id.my_collection:
                intent = new Intent(UserActivity.this,MycollectListActivity.class);
                //启动
                startActivity(intent);
                break;

            case R.id.publish_college:
                intent = new Intent(UserActivity.this, PublishActivity.class);
                intent.putExtra("mode_name","发布活动");
                intent.putExtra("mode","/college");
                startActivity(intent);
                break;
        }
    }
}
