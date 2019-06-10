package com.example.swalls;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);
        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        final BottomNavigationView bnv = (BottomNavigationView) findViewById(R.id.navigation_3);
        bnv.setSelectedItemId(R.id.navigation_notifications);
        //点击选择item
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        Intent intent2 = new Intent(UserActivity.this, WallListActivity.class);
                        startActivity(intent2);
                        return true;
                    case R.id.navigation_dashboard:
                        Intent intent1 = new Intent(UserActivity.this, NavigationActivity.class);
                        startActivity(intent1);
                        return true;
                    case R.id.navigation_notifications:
                }
                return false;
            }
        });
    }
}
