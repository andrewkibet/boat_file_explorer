package com.bomboverk.boat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bomboverk.boat.Fragments.FavsFragment;
import com.bomboverk.boat.Fragments.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    //private Toolbar mToolbar;

    private FrameLayout home;
    private FrameLayout favs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mToolbar = findViewById(R.id.act_main_toolbar);
        //setSupportActionBar(mToolbar);
        //getSupportActionBar().setTitle("Boat - File Explorer");

        bottomNav = findViewById(R.id.act_main_bottomNav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        home = findViewById(R.id.fragment_cont_home);
        favs = findViewById(R.id.fragment_cont_favs);

        loadContents();
    }

    private void loadContents(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_cont_home, new HomeFragment()).commit();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_cont_favs, new FavsFragment()).commit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_cont_home, new HomeFragment()).commit();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_cont_favs, new FavsFragment()).commit();
            }else{
                loadContents();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.menu_nav_bottom_home:
                    favs.setVisibility(View.INVISIBLE);
                    home.setVisibility(View.VISIBLE);
                    break;
                case R.id.menu_nav_bottom_favs:
                    home.setVisibility(View.INVISIBLE);
                    favs.setVisibility(View.VISIBLE);
                    break;
            }
            return true;
        }
    };
}
