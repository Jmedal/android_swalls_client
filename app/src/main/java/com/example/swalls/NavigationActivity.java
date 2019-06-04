package com.example.swalls;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class NavigationActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView edu_btn;

    private ImageView college_btn;

    private ImageView sahre_btn;

    private ImageView lecture_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_activity);
        edu_btn = (ImageView)findViewById(R.id.edu_img);
        college_btn = (ImageView)findViewById(R.id.college_img);
        sahre_btn = (ImageView)findViewById(R.id.sahre_img);
        lecture_btn = (ImageView)findViewById(R.id.lecture_img);
        edu_btn.setOnClickListener(this);
        college_btn.setOnClickListener(this);
        sahre_btn.setOnClickListener(this);
        lecture_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == edu_btn.getId()){
            Intent intent = new Intent(NavigationActivity.this, EduListActivity.class);
            intent.putExtra("mode_name","教务处公告");
            intent.putExtra("mode","/edu");
            intent.putExtra("modeI",0);
            startActivity(intent);
        }
        else if (v.getId() == college_btn.getId()){
            Intent intent = new Intent(NavigationActivity.this, EduListActivity.class);
            intent.putExtra("mode_name","社团活动");
            intent.putExtra("mode","/college");
            intent.putExtra("modeI",1);
            startActivity(intent);
        }
        else if (v.getId() == sahre_btn.getId()){
//            Intent intent = new Intent(NavigationActivity.this, EduListActivity.class);
//            intent.putExtra("mode","/sahre");
//            startActivity(intent);
        }else if (v.getId() == lecture_btn.getId()){
            Intent intent = new Intent(NavigationActivity.this, EduListActivity.class);
            intent.putExtra("mode_name","讲座一览");
            intent.putExtra("mode","/lecture");
            intent.putExtra("modeI",2);
            startActivity(intent);
        }

    }
}
