package com.example.swalls;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import static com.example.swalls.constant.Mode.MODE_COLLEGE;
import static com.example.swalls.constant.Mode.MODE_EDU;
import static com.example.swalls.constant.Mode.MODE_LECTURE;
import static com.example.swalls.constant.Mode.MODE_SHARE;

public class NavigationActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView edu_btn;

    private ImageView college_btn;

    private ImageView sahre_btn;

    private ImageView lecture_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_activity);
        init();
        initView();
    }

    protected void init(){
        edu_btn = (ImageView)findViewById(R.id.edu_img);
        college_btn = (ImageView)findViewById(R.id.college_img);
        sahre_btn = (ImageView)findViewById(R.id.sahre_img);
        lecture_btn = (ImageView)findViewById(R.id.lecture_img);

        edu_btn.setOnClickListener(this);
        college_btn.setOnClickListener(this);
        sahre_btn.setOnClickListener(this);
        lecture_btn.setOnClickListener(this);

        ((TextView)findViewById(R.id.item_mode_name)).setText("校园资讯");
    }

    /**
     * 初始化
     */
    private void initView() {
        final BottomNavigationView bnv = (BottomNavigationView) findViewById(R.id.navigation_2);
        bnv.setSelectedItemId(R.id.navigation_dashboard);
        //点击选择item
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        intent = new Intent(NavigationActivity.this, WallListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        return true;
                    case R.id.navigation_dashboard:
                        return true;
                    case R.id.navigation_notifications:
                        intent = new Intent(NavigationActivity.this, UserActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == edu_btn.getId()){
            Intent intent = new Intent(this, ItemListActivity.class);
            intent.putExtra("mode_name","教务处公告");
            intent.putExtra("mode","/edu");
            intent.putExtra("modeI",MODE_EDU);
            startActivity(intent);
        }
        else if (v.getId() == college_btn.getId()){
            Intent intent = new Intent(this, ItemListActivity.class);
            intent.putExtra("mode_name","社团活动");
            intent.putExtra("mode","/college");
            intent.putExtra("modeI",MODE_COLLEGE);
            startActivity(intent);
        }
        else if (v.getId() == sahre_btn.getId()){
            Intent intent = new Intent(this, ItemListActivity.class);
            intent.putExtra("mode_name","解题习集");
            intent.putExtra("mode","/share");
            intent.putExtra("modeI",MODE_SHARE);
            startActivity(intent);
        }else if (v.getId() == lecture_btn.getId()){
            Intent intent = new Intent(this, ItemListActivity.class);
            intent.putExtra("mode_name","讲座一览");
            intent.putExtra("mode","/lecture");
            intent.putExtra("modeI",MODE_LECTURE);
            startActivity(intent);
        }

    }
}
