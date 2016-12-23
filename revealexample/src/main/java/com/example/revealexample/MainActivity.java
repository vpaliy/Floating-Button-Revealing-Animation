package com.example.revealexample;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar actionBar=(Toolbar)(findViewById(R.id.mainToolbar));
        if(getSupportActionBar()==null) {
            setSupportActionBar(actionBar);
            getSupportActionBar().setTitle("");
        }

        drawerLayout=(DrawerLayout)(findViewById(R.id.drawer_layout));
        NavigationView navigation=(NavigationView)(findViewById(R.id.listItem));
        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_first_fragment: {
                        getSupportActionBar().show();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.mainContent, new PlayerExample())
                                .commit();
                        break;
                    }

                    case R.id.nav_second_fragment: {
                        getSupportActionBar().hide();
                        getSupportFragmentManager().beginTransaction()
                            .replace(R.id.mainContent,new FullScreenExample())
                            .commit();
                            break;
                    }


                }

                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

}
