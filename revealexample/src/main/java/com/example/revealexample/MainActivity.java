package com.example.revealexample;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private int gravity= Gravity.TOP|Gravity.END;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar actionBar=(Toolbar)(findViewById(R.id.mainToolbar));
        if(getSupportActionBar()==null) {
            setSupportActionBar(actionBar);
        }

        drawerLayout=(DrawerLayout)(findViewById(R.id.drawer_layout));
        NavigationView navigation=(NavigationView)(findViewById(R.id.listItem));
        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_first_fragment: {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.mainContent, PlayerExample.newInstance(gravity))
                                .commit();
                        break;
                    }

                    case R.id.nav_second_fragment: {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.mode_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.topStart:
                return true;
            case R.id.topEnd:
                return true;
            case R.id.bottomStart:
                return true;
            case R.id.bottomEnd:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
