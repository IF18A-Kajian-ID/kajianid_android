package com.kajianid.android.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kajianid.android.R;
import com.kajianid.android.databinding.ActivityMainBinding;

import java.util.Set;

import static androidx.navigation.ui.NavigationUI.setupActionBarWithNavController;
import static androidx.navigation.ui.NavigationUI.setupWithNavController;

public class MainActivity extends AppCompatActivity {

    //deklarasi variabel
    private AppBarConfiguration mAppBarConfiguration;

    //inisialisasi variabel
    private boolean doubleBackToExitPressedOnce = false;

    //deklarasi variabel untuk menu samping
    private DrawerLayout drawerLayout;

    //deklarasi variabel
    private ActivityMainBinding binding;

    //perintah yang akan dilaksanakan ketika pengguna memilih tombol kembali
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) super.onBackPressed();

        doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Tekan sekali lagi untuk keluar dari Kajian.ID!", Toast.LENGTH_SHORT).show();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            doubleBackToExitPressedOnce = false;
        }, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        drawerLayout = binding.drawerLayout;

        //menu atas
        setSupportActionBar(binding.appBarMain.toolbar);

        //pengontrol tampilan menu samping
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        //mendefinisikan menu
        //mengatur aksi setelah memilih salah satu menu samping
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_downloaded_articles,
                R.id.nav_saved_kajian)
                .setDrawerLayout(drawerLayout)
                .build();

        //menggabungkan action bar dan menu samping
        setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        //menggabungkan tampilan menu samping dengan pengontrol aksi setelah memilih salah satu menu samping
        setupWithNavController(binding.navMain, navController);

        //menampilkan akun
        View headerView = binding.navMain.getHeaderView(0);

        //mencari model
        TextView txtUsername = headerView.findViewById(R.id.txtNamaUser2);

        //mencari manufaktur
        TextView txtEmail = headerView.findViewById(R.id.txtEmail);

        //mengubah properti yang berbentuk teks
        txtUsername.setText(Build.MODEL);
        txtEmail.setText(Build.MANUFACTURER);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            //jika memilih komponen berid home (titik tiga)
            case android.R.id.home:

                //akan menampilkan menu samping dari kiri ke kanan
                drawerLayout.openDrawer(GravityCompat.START);
                break;

            //jika memilih komponen berid menuAbout
            case R.id.menuAbout:

                //pindah activity ke AboutActivity.java
                Intent i = new Intent(this, AboutActivity.class);
                startActivity(i);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //menambahkan menu_main.xml
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}